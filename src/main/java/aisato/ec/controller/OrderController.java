package aisato.ec.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import aisato.ec.entity.CartItem;
import aisato.ec.entity.Order;
import aisato.ec.entity.OrderItem;
import aisato.ec.entity.PaymentMethod;
import aisato.ec.entity.User;
import aisato.ec.entity.UserAddress;
import aisato.ec.service.CartService;
import aisato.ec.service.OrderService;
import aisato.ec.service.PaymentMethodService;
import aisato.ec.service.UserService;

/**
 * 注文コントローラー
 * 注文関連のリクエストを処理（チェックアウト、注文作成、注文履歴など）
 * ログインユーザーのみを対象に処理
 * 
 * @author zhl
 * @version 1.0.0
 */
@Controller
@RequestMapping("/order")
public class OrderController {

	@Autowired
	private CartService cartService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private UserService userService;

	@Autowired
	private PaymentMethodService paymentMethodService;

	/**
	 * チェックアウトページを表示
	 * 
	 * @param model モデル
	 * @param session セッション情報（ログインユーザーの取得など）
	 * @return order/checkoutビュー名、未ログインはログインページへリダイレクト
	 */
	@GetMapping("/checkout")
	public String checkoutPage(Model model, HttpSession session) {
		// セッションから現在のユーザーを取得
		User currentUser = (User) session.getAttribute("currentUser");
		// 未ログインの場合はログインページへリダイレクト
		if (currentUser == null) {
			session.setAttribute("redirectUrl", "/order/checkout");
			return "redirect:/login";
		}

		List<CartItem> cartItems = cartService.getCart(currentUser.getUserId());

		if (cartItems.isEmpty()) {
			return "redirect:/cart";
		}

		model.addAttribute("cartItems", cartItems);

		// 合計金額と合計数量を計算
		BigDecimal totalAmount = BigDecimal.ZERO;
		int totalQuantity = 0;

		for (CartItem item : cartItems) {
			if (item.getProduct() != null) {
				totalAmount = totalAmount.add(
						item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())));
				totalQuantity += item.getQuantity();
			}
		}

		model.addAttribute("totalAmount", totalAmount);
		model.addAttribute("totalQuantity", totalQuantity);

		// ユーザー情報を取得
		User user = userService.getUserInfo(currentUser.getUserId());
		model.addAttribute("user", user);

		// ユーザー住所を取得
		List<UserAddress> addresses = userService.getUserAddresses(currentUser.getUserId());
		model.addAttribute("addresses", addresses);

		UserAddress defaultAddress = addresses.stream()
				.filter(addr -> addr.getIsDefault() != null && addr.getIsDefault() == 1)
				.findFirst()
				.orElse(null);
		model.addAttribute("defaultAddress", defaultAddress);

		// 支払い方法を取得
		List<PaymentMethod> paymentMethods = paymentMethodService.getAllPaymentMethods();
		model.addAttribute("paymentMethods", paymentMethods);

		// ユーザーのデフォルト支払い方法IDを取得
		Long defaultPaymentMethodId = userService.getDefaultPaymentMethodId(currentUser.getUserId());

		// デフォルト支払い方法がない場合は、最初の支払い方法を使用
		if (defaultPaymentMethodId == null && paymentMethods != null && !paymentMethods.isEmpty()) {
			defaultPaymentMethodId = paymentMethods.get(0).getMethodId();
		}

		model.addAttribute("defaultPaymentMethodId", defaultPaymentMethodId);
		model.addAttribute("currentUser", currentUser);

		return "order/checkout";
	}

	/**
     * 注文確定処理
     * カート内の商品を注文として登録し、カートをクリアする
     * 
     * @param order 注文情報（配送先や支払い方法など）
     * @param session セッション情報
     * @return 注文完了ページへリダイレクト
     */
	@PostMapping("/confirm")
	public String confirmOrder(Order order, HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");

		// カートアイテムを取得
		List<CartItem> cartItems = cartService.getCart(currentUser.getUserId());

		// 注文件を作成
		List<OrderItem> orderItems = new ArrayList<>();
		BigDecimal totalAmount = BigDecimal.ZERO;

		for (CartItem cartItem : cartItems) {
			OrderItem orderItem = new OrderItem();
			orderItem.setProductId(cartItem.getProductId());
			orderItem.setProductName(cartItem.getProduct().getProductName());
			orderItem.setProductPrice(cartItem.getProduct().getPrice());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setSelectedColor(cartItem.getSelectedColor());
			orderItem.setSelectedSize(cartItem.getSelectedSize());

			// 小計を計算
			BigDecimal subtotal = cartItem.getProduct().getPrice()
					.multiply(new BigDecimal(cartItem.getQuantity()));
			orderItem.setSubtotalAmount(subtotal);

			orderItems.add(orderItem);
			totalAmount = totalAmount.add(subtotal);
		}

		// 注文情報を設定
		order.setUserId(currentUser.getUserId());
		order.setTotalAmount(totalAmount);
		order.setShippingFee(BigDecimal.ZERO); // 送料無料
		order.setOrderItems(orderItems);

		// 注文を作成
		Long orderId = orderService.createOrder(order);

		// カートをクリア
		cartService.clearCart(currentUser.getUserId());

		session.setAttribute("orderId", orderId);

		return "redirect:/order/success";
	}

	 /**
     * 注文完了ページを表示
     * 
     * @param model モデル
     * @param session セッション情報（注文ID、ユーザー）
     * @return order/successビュー
     */
	@GetMapping("/success")
	public String successPage(Model model, HttpSession session) {
		Long orderId = (Long) session.getAttribute("orderId");
		User currentUser = (User) session.getAttribute("currentUser");
		if (orderId == null) {
			return "redirect:/";
		}

		Order order = orderService.getOrderDetail(orderId);
		model.addAttribute("order", order);
		model.addAttribute("currentUser", currentUser);
		session.removeAttribute("orderId");

		return "order/success";
	}

	/**
     * 注文履歴ページを表示
     * 
     * @param model モデル
     * @param session セッション情報（ログインユーザー取得）
     * @return order/historyビュー、未ログイン時はログインページにリダイレクト
     */
	@GetMapping("/history")
	public String orderHistory(Model model, HttpSession session) {
		User currentUser = (User) session.getAttribute("currentUser");

		if (currentUser == null) {
			return "redirect:/login";
		}

		List<Order> orders = orderService.getOrderHistory(currentUser.getUserId());
		model.addAttribute("orders", orders);
		model.addAttribute("currentUser", currentUser);
		return "order/history";
	}
}