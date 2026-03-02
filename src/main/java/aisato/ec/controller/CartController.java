package aisato.ec.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import aisato.ec.entity.CartItem;
import aisato.ec.entity.Product;
import aisato.ec.entity.User;
import aisato.ec.service.CartService;
import aisato.ec.service.ProductService;
import lombok.extern.slf4j.Slf4j;

/**
 * カート機能を担当するコントローラークラス
 * 本クラスはカート画面の表示、商品追加、数量更新、
 * 削除処理などのリクエストを処理する。
 * 
 * @author liu
 * @version 1.0.0
 */
@Controller
@RequestMapping("/cart")
@Slf4j
public class CartController {

	/**
	 * カートサービス側呼び出す
	 */
	@Autowired
	private CartService cartService;

	/**
	 * 商品サービス側呼び出す
	 */
	@Autowired
	private ProductService productService;

	/**
	 * カートページを表示する
	 * @param model    Viewへデータを渡すためのModel
	 * @param session  セッション情報
	 * @return カート画面（cart/index）
	 */
	@GetMapping
	public String cartPage(Model model, HttpSession session) {

		// ユーザーセッション情報取得
		User currentUser = (User) session.getAttribute("currentUser");

		// ログイン状態のログ出力
		log.info("カートページアクセス - ユーザー: {}",
				currentUser != null ? currentUser.getUsername() : "未ログイン");

		List<CartItem> cartItems = new ArrayList<>(); // 初期カートアイテムリスト
		double totalAmount = 0; // 合計金額
		int totalQuantity = 0; // 合計数量

		// 未ログインの場合はセッション内の一時カートを使用する
		if (currentUser != null) {
			// ログイン時：データベースからカートを取得
			cartItems = cartService.getCart(currentUser.getUserId());
			log.info("ログインユーザーのカート取得 - ユーザーID: {}, カートアイテム数: {}",
					currentUser.getUserId(), cartItems.size());
		} else {
			// 未ログイン時：セッションから一時カートを取得
			@SuppressWarnings("unchecked")
			List<CartItem> tempCart = (List<CartItem>) session.getAttribute("tempCart");
			if (tempCart != null) {
				cartItems = tempCart;
				log.info("未ログインユーザーの一時カート取得 - アイテム数: {}", cartItems.size());
			}
		}
		// 合計金額・数量を計算
		for (CartItem item : cartItems) {
			if (item.getProduct() != null) {
				totalAmount += item.getProduct().getPrice().doubleValue() * item.getQuantity();
				totalQuantity += item.getQuantity();
			}
		}
		// 画面へ値を渡す
		model.addAttribute("cartItems", cartItems); //カートアイテムリスト
		model.addAttribute("totalAmount", totalAmount); //合計金額
		model.addAttribute("totalQuantity", totalQuantity); // 追加：合計数量
		model.addAttribute("currentUser", currentUser);// セッションからユーザー情報を取得

		//	カートアイテム数を計算
		int cartItemCount = calculateCartItemCount(currentUser, session);
		model.addAttribute("cartItemCount", cartItemCount);
		//	カート画面に映す
		return "cart/index";
	}

	/**
	 * カート内の商品総数量を計算する
	 * @param currentUser 現在ログイン中のユーザー
	 * @param session     セッション情報
	 * @return カート内商品の合計数量	
	 */
	private int calculateCartItemCount(User currentUser, HttpSession session) {
		//	カートアイテム合計数量
		int cartItemCount = 0;

		if (currentUser != null) {
			// ログインユーザー：データベースからカートを取得
			List<CartItem> cartItems = cartService.getCart(currentUser.getUserId());
			for (CartItem item : cartItems) {
				cartItemCount += item.getQuantity();
			}
		} else {
			// 未ログインユーザー：セッションから一時カートを取得
			@SuppressWarnings("unchecked")
			List<CartItem> tempCart = (List<CartItem>) session.getAttribute("tempCart");
			if (tempCart != null) {
				for (CartItem item : tempCart) {
					cartItemCount += item.getQuantity();
				}
			}
		}

		return cartItemCount;
	}

	/**
	 * 商品をカートに追加
	 * 未ログインの場合はセッションに一時保存し、
	 * ログイン済みの場合はデータベースへ保存する。
	 * 
	 * @param productId      商品ID
	 * @param selectedColor  選択された色
	 * @param selectedSize   選択されたサイズ
	 * @param quantity       数量
	 * @param session     セッション情報
	 * @return カート画面
	 */
	@PostMapping("/add")
	public String addToCart(
			@RequestParam Long productId,
			@RequestParam(required = false) String selectedColor,
			@RequestParam(required = false) String selectedSize,
			@RequestParam Integer quantity,
			HttpSession session) {

		// 未ログイン時の処理：セッションに一時カートを保存
		if (session.getAttribute("currentUser") == null) {
			// 一時カートを取得（なければ新規作成）
			@SuppressWarnings("unchecked")
			List<CartItem> tempCart = (List<CartItem>) session.getAttribute("tempCart");
			if (tempCart == null) {
				tempCart = new ArrayList<>();
				session.setAttribute("tempCart", tempCart);
			}
			// 既に同じ商品（色・サイズ含む）が存在するかチェック
			boolean exists = false;
			for (CartItem item : tempCart) {
				if (Objects.equals(item.getProductId(), productId) &&
						Objects.equals(item.getSelectedColor(), selectedColor) &&
						Objects.equals(item.getSelectedSize(), selectedSize)) {
					// 同じ商品は数量を加算
					item.setQuantity(item.getQuantity() + quantity);
					exists = true;
					break;
				}
			}
			// 新規商品の場合
			if (!exists) {
				CartItem newItem = new CartItem();
				newItem.setProductId(productId);
				newItem.setSelectedColor(selectedColor);
				newItem.setSelectedSize(selectedSize);
				newItem.setQuantity(quantity);
				// 商品情報を取得してセット（表示用）
				Product product = productService.getProductDetail(productId);
				newItem.setProduct(product);
				tempCart.add(newItem);
			}
			//	カート画面に映す
			return "redirect:/cart";
		}
		//	ログイン時の処理：データベースに保存
		User currentUser = (User) session.getAttribute("currentUser");
		cartService.addToCart(currentUser.getUserId(), productId, selectedColor, selectedSize, quantity);
		//	カート画面に映す
		return "redirect:/cart";
	}

	/**
	 * カートアイテム情報を更新する（数量・色・サイズ）
	 * 
	 * @param cartItemId カートアイテムID
	 * @param quantity   更新後の数量
	 * @param color      更新後の色
	 * @param size       更新後のサイズ
	 * @return 処理結果（success / message）
	 */
	@PostMapping("/update/{cartItemId}")
	@ResponseBody
	public Map<String, Object> updateCartItem(
			@PathVariable Long cartItemId,
			@RequestParam(required = false) Integer quantity,
			@RequestParam(required = false) String color,
			@RequestParam(required = false) String size) {
		// レスポンス用の結果マップを作成
		Map<String, Object> result = new HashMap<>();

		try {
			cartService.updateCartItem(cartItemId, quantity, color, size);
			result.put("success", true);
			result.put("message", "カートアイテムを更新しました");
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		// JSON形式で結果を返却
		return result;
	}

	/**
	 *カートアイテムの数量のみを更新する
	 *
	 * @param cartItemId カートアイテムID
	 * @param quantity   更新後の数量
	 * @param session    セッション情報
	 * @return 処理結果（success / message）
	 */
	@PostMapping("/update-quantity/{cartItemId}")
	@ResponseBody
	public Map<String, Object> updateCartItemQuantity(
			@PathVariable Long cartItemId,
			@RequestParam Integer quantity,
			HttpSession session) {

		// レスポンス用の結果マップを作成
		Map<String, Object> result = new HashMap<>();

		try {
			cartService.updateCartItemQuantity(cartItemId, quantity);
			//現在ユーザ情報取得
			User currentUser = (User) session.getAttribute("currentUser");
			//登録したユーザID.カートアイテムリスト取得
			List<CartItem> cartItems = cartService.getCart(currentUser.getUserId());

			double totalAmount = 0; // 合計金額
			int totalQuantity = 0; // 合計数量
			// 合計金額・数量を計算
			for (CartItem item : cartItems) {
				if (item.getProduct() != null) {
					totalAmount += item.getProduct().getPrice().doubleValue()
							* item.getQuantity();
					totalQuantity += item.getQuantity();
				}
			}
			//	画面に値を渡す
			result.put("success", true);
			result.put("message", "数量を更新しました");
			result.put("cartTotal", totalAmount);
			result.put("cartCount", totalQuantity);
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}
		// JSON形式で結果を返却
		return result;
	}
	
	/**
	 * 一時カートアイテムの数量を更新する（未ログインユーザー用）
	 * セッションに保存されている tempCart から指定インデックスの商品を取得し、
	 * 数量を更新後、カート全体の合計金額・合計数量を再計算して返却する。
	 *
	 * @param index    更新対象商品のインデックス（セッション内リスト位置）
	 * @param quantity 更新後の数量（1以上）
	 * @param session  セッション情報
	 * @return 処理結果（success / message / cartTotal / cartCount）
	 */
	@PostMapping("/update-temp-quantity")
	@ResponseBody
	public Map<String, Object> updateTempCartQuantity(
			@RequestParam int index,
			@RequestParam int quantity,
			HttpSession session) {

		Map<String, Object> result = new HashMap<>();

		try {
			@SuppressWarnings("unchecked")
			List<CartItem> tempCart = (List<CartItem>) session.getAttribute("tempCart");
			// カート存在チェック
			if (tempCart == null || tempCart.isEmpty()) {
				result.put("success", false);
				result.put("message", "カートが空です");
				return result;
			}
			// インデックス範囲チェック
			if (index < 0 || index >= tempCart.size()) {
				result.put("success", false);
				result.put("message", "無効なインデックスです");
				return result;
			}
			// 数量バリデーション
			if (quantity <= 0) {
				result.put("success", false);
				result.put("message", "数量は1以上で指定してください");
				return result;
			}
			
			double totalAmount = 0;// 合計金額
			int totalQuantity = 0; // 合計数量
			// 数量を更新
			tempCart.get(index).setQuantity(quantity);
			// 合計金額・数量を計算
			for (CartItem item : tempCart) {
				if (item.getProduct() != null) {
					totalAmount += item.getProduct().getPrice().doubleValue()
							* item.getQuantity();
					totalQuantity += item.getQuantity();
				}
			}
			//	画面に値を渡す
			session.setAttribute("tempCart", tempCart);
			result.put("success", true);
			result.put("message", "数量を更新しました");
			result.put("cartTotal", totalAmount);
			result.put("cartCount", totalQuantity);

		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "更新に失敗しました: " + e.getMessage());
		}

		return result;
	}

	/**
	 * カートアイテムを削除する（ログインユーザー用）
	 *
	 * 指定された cartItemId の商品をDBから削除し、
	 * 削除後のカート合計金額・合計数量を再計算して返却する。
	 *
	 * @param cartItemId 削除対象のカートアイテムID
	 * @param session    セッション情報
	 * @return 処理結果（success / message / cartTotal / cartCount）
	 */
	@PostMapping("/delete-ajax/{cartItemId}")
	@ResponseBody
	public Map<String, Object> deleteCartItemAjax(@PathVariable Long cartItemId, HttpSession session) {
		Map<String, Object> result = new HashMap<>();

		try {
			User currentUser = (User) session.getAttribute("currentUser");
			if (currentUser == null) {
				result.put("success", false);
				result.put("message", "ログインが必要です");
				return result;
			}
			
			cartService.deleteCartItem(cartItemId);
			
			List<CartItem> cartItems = cartService.getCart(currentUser.getUserId());

			double totalAmount = 0; // 合計金額
			int totalQuantity = 0; // 合計数量
			// 合計金額・数量を計算
			for (CartItem item : cartItems) {
				totalAmount += item.getProduct().getPrice().doubleValue() * item.getQuantity();
				totalQuantity += item.getQuantity();
			}
			// 画面に値を渡す
			result.put("cartTotal", totalAmount);
			result.put("cartCount", totalQuantity);
			result.put("success", true);
			result.put("message", "商品をカートから削除しました");
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getMessage());
		}

		return result;
	}

	/**
	 * 一時カートアイテムを削除する（未ログインユーザー用）
	 *
	 * セッションに保存されている tempCart から
	 * 指定インデックスの商品を削除し、削除後の合計を再計算する。
	 *
	 * @param index   削除対象のインデックス
	 * @param session セッション情報
	 * @return 処理結果（success / message / cartTotal / cartCount）
	 */
	@PostMapping("/delete-temp/{index}")
	@ResponseBody
	public Map<String, Object> deleteTempCartItem(@PathVariable int index, HttpSession session) {
		Map<String, Object> result = new HashMap<>();

		try {
			// 一時カートを取得
			@SuppressWarnings("unchecked")
			List<CartItem> tempCart = (List<CartItem>) session.getAttribute("tempCart");

			if (tempCart == null || tempCart.isEmpty()) {
				result.put("success", false);
				result.put("message", "カートが空です");
				return result;
			}

			// インデックスの範囲チェック
			if (index < 0 || index >= tempCart.size()) {
				result.put("success", false);
				result.put("message", "無効なインデックスです");
				return result;
			}

			// 指定インデックスのアイテムを削除
			tempCart.remove(index);
			double totalAmount = 0; // 合計金額
			int totalQuantity = 0; // 合計数量
			// 合計金額・数量を計算
			for (CartItem item : tempCart) {
				totalAmount += item.getProduct().getPrice().doubleValue() * item.getQuantity();
				totalQuantity += item.getQuantity();
			}
			// 画面に値を渡す
			session.setAttribute("tempCart", tempCart);
			result.put("success", true);
			result.put("message", "商品をカートから削除しました");
			result.put("cartTotal", totalAmount);
			result.put("cartCount", totalQuantity);

		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "削除に失敗しました: " + e.getMessage());
		}

		return result;
	}

	/**
	 * カートアイテムを削除する（通常リクエスト）
	 *
	 * 指定された cartItemId の商品を削除し、
	 * カート画面へリダイレクトする。
	 *
	 * @param cartItemId 削除対象のカートアイテムID
	 * @return カート画面へのリダイレクトパス
	 */
	@GetMapping("/delete/{cartItemId}")
	@ResponseBody
	public String deleteCartItem(@PathVariable Long cartItemId) {
		cartService.deleteCartItem(cartItemId);
		return "redirect:/cart";
	}
	
}
