package aisato.ec.service;

import java.util.List;

import aisato.ec.entity.CartItem;

/**
 * カードサービスインターフェース
 * 
 */
public interface CartService {

	/**
	* ユーザーのカートを取得
	* @param userId ユーザーID
	* @return カートア	イテムリスト
	*/
	List<CartItem> getCart(Long userId);

	/**
	 * カートに商品を追加（色・サイズ指定）
	 * @param userId ユーザーID
	 * @param productId 商品ID
	 * @param selectedColor 選択した色（任意）
	 * @param selectedSize 選択したサイズ（任意）
	 * @param quantity 数量
	 */
	void addToCart(Long userId, Long productId, String selectedColor, String selectedSize, Integer quantity);

	/**
	 * カートアイテムを更新（数量、色、サイズ）
	 * @param cartItemId カートアイテムID
	 * @param quantity 数量（nullの場合は変更なし）
	 * @param color 選択された色（nullの場合は変更なし）
	 * @param size 選択されたサイズ（nullの場合は変更なし）
	 */
	void updateCartItem(Long cartItemId, Integer quantity, String color, String size);

	/**
	 * 指定したカートアイテムの数量を更新する。
	 *
	 * @param cartItemId 更新対象のカートアイテムID
	 * @param quantity   更新後の数量
	 */
	void updateCartItemQuantity(Long cartItemId, Integer quantity);

	/**
	 * 指定したカートアイテムを削除する。
	 *
	 * @param cartItemId 削除対象のカートアイテムID
	 */
	void deleteCartItem(Long cartItemId);

	/**
	 * 指定したユーザーのカートをすべてクリアする。
	 * 注文完了後やログアウト時のカート初期化で利用。
	 *
	 * @param userId カートをクリアする対象のユーザーID
	 */
	void clearCart(Long userId);

}
