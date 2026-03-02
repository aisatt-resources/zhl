package aisato.ec.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import aisato.ec.entity.CartItem;
import aisato.ec.entity.Product;
import aisato.ec.entity.User;
import aisato.ec.service.CartService;
import aisato.ec.service.ProductService;
import lombok.extern.slf4j.Slf4j;

/**
 * ホームページcontroller
 * ホームページリクエスト処理
 *   
 * @author zhl
 * @version 1.0.0
 */
@Controller
@Slf4j
public class HomeController {

	//商品サービス側呼び出し
	@Autowired
	private ProductService productService;

	@Autowired
	private CartService cartService;

	/**
	* ホームページ表示
	* 
	* すべての商品を取得し、モデルに追加
	* ログインユーザーの場合はDBからカート情報を取得
	* 未ログインの場合はセッションの一時カートを使用
	* 
	* @param model モデル
	* @param session セッション情報
	* @return homeビュー名
	*/
	@GetMapping("/")
	public String homePage(Model model, HttpSession session) {
		log.info("全て商品データ取得");

		//商品データを取得
		List<Product> products = productService.getAllProducts();
		log.info("取得した商品は：product{}", products);

		//商品のデータをテンプレートに渡す	
		model.addAttribute("products", products);

		// セッションからユーザー情報を取得
		User currentUser = (User) session.getAttribute("currentUser");
		model.addAttribute("currentUser", currentUser);

		// カートアイテム数を計算
		int cartItemCount = calculateCartItemCount(currentUser, session);
		model.addAttribute("cartItemCount", cartItemCount);

		//templates/home.html
		return "home";
	}

	/**
	* カートアイテム数を計算（ログイン/未ログイン対応）
	* 
	* @param currentUser 現在ログイン中のユーザー情報
	* @param session セッション情報
	* @return cartItemCount カート内の合計アイテム数
	*/
	private int calculateCartItemCount(User currentUser, HttpSession session) {
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
	* カテゴリー別に商品を表示
	* 
	* @param categoryId 表示対象カテゴリーID
	* @param model モデル
	* @param session セッション情報
	* @return homeビュー名
	*/
	@GetMapping("/category")
	public String category(@RequestParam Long categoryId, Model model, HttpSession session) {
		List<Product> products = productService.getProductsByCategory(categoryId);
		model.addAttribute("products", products);

		User currentUser = (User) session.getAttribute("currentUser");
		model.addAttribute("currentUser", currentUser);

		return "home";
	}

	/**
	 * 商品検索
	 * 
	 * @param keyword 検索キーワード
	 * @param model モデル
	 * @param session セッション情報
	 * @return homeビュー名
	 */
	@GetMapping("/search")
	public String search(@RequestParam String keyword, Model model, HttpSession session) {
		List<Product> products = productService.searchProducts(keyword);
		model.addAttribute("products", products);
		model.addAttribute("searchKeyword", keyword);

		User currentUser = (User) session.getAttribute("currentUser");
		model.addAttribute("currentUser", currentUser);

		return "home";
	}

}
