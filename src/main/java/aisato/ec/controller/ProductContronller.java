package aisato.ec.controller;

import java.util.List;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import aisato.ec.entity.CartItem;
import aisato.ec.entity.Product;
import aisato.ec.entity.User;
import aisato.ec.service.CartService;
import aisato.ec.service.ProductService;
import lombok.extern.slf4j.Slf4j;

/**
 * 商品コントローラー
 * 商品に関するHTTPリクエストを処理し、Service層とView層の連携を担う。
 * 商品詳細表示やカートアイテム数の取得などを提供。
 * 
 * @author liu
 * @version 1.0.0
 */
@Controller
@Slf4j
public class ProductContronller {
	
	/**
	 *商品サービス側を呼び出し
	 */
	@Autowired
	private ProductService productService;
	
	/**
	 * カードサービス側を呼び出し
	 */
	@Autowired
	private CartService cartService;
	
	/**
     * 商品詳細画面表示
     * 
     * 商品IDに基づき詳細情報を取得し、モデルに設定。
     * ログイン/未ログインに応じてカートアイテム数も計算してモデルに追加。
     * 
     * @param productId 商品ID
     * @param model モデル
     * @param session セッション情報
     * @return product-detailビュー名、商品未存在時は404ビュー
     */
	@GetMapping("/product/{productId}")
	public String productDetail(@PathVariable Long productId,
							   Model model,HttpSession session) {
		
		log.info("商品IDを取得:productId={}",productId);
		//商品IDを取得
		Product product= productService.getProductDetail(productId);
		if(product == null){
			log.warn("商品が見つかりません: productId={}", productId);
			return "erro/404";
		}
		model.addAttribute("product",product);
		
		// セッションからユーザー情報を取得
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        
        // カートアイテム数を計算
        int cartItemCount = calculateCartItemCount(currentUser, session);
        model.addAttribute("cartItemCount", cartItemCount);
        
		//商品詳細画面に遷移
		return "product/detail";
	}
	
	/**
     * カートアイテム数を計算（ログイン/未ログイン対応）
     * 
     * @param currentUser 現在ログイン中のユーザー
     * @param session セッション情報
     * @return カート内の合計アイテム数
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

}
