package aisato.ec.service.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisato.ec.entity.CartItem;
import aisato.ec.entity.Product;
import aisato.ec.mapper.CartItemMapper;
import aisato.ec.mapper.ProductMapper;
import aisato.ec.service.CartService;
import aisato.ec.service.ProductService;
import lombok.extern.slf4j.Slf4j;

/**
 * カートサービス実装クラス
 * カート関連のビジネスロジックを実装
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {

	@Autowired
	private CartItemMapper cartItemMapper;

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductMapper productMapper;

	/**
	 * ユーザーのカートを取得
	 * 
	 * @param userId 
	 * @return 商品情報も含めて返す
	 */
	@Override
	public List<CartItem> getCart(Long userId) {
		List<CartItem> cartItems = cartItemMapper.findByUserId(userId);

		// cartItemIdの確認
		log.info("getCart - userId: {}, カートアイテム数: {}", userId, cartItems.size());

		for (CartItem item : cartItems) {
			// cartItemIdが存在するか確認
			if (item.getCartItemId() == null) {
				log.warn("警告: cartItemId が null のカートアイテムがあります。userId: {}, productId: {}",
						userId, item.getProductId());
			} else {
				log.debug("カートアイテム - cartItemId: {}, productId: {}, quantity: {}",
						item.getCartItemId(), item.getProductId(), item.getQuantity());
			}

			// 商品情報を取得
			Product product = productService.getProductDetail(item.getProductId());
			item.setProduct(product);

		}
		return cartItems;
	}

	/**
	 * カートに商品を追加（色・サイズ指定）
	 * @param userId ユーザーID
	 * @param productId 商品ID
	 * @param selectedColor 選択した色（任意）
	 * @param selectedSize 選択したサイズ（任意）
	 * @param quantity 数量
	 * @return 追加した商品表示 
	 */
	@Override
	@Transactional
	public void addToCart(Long userId, Long productId, String selectedColor, String selectedSize, Integer quantity) {
		if (userId == null || productId == null || quantity == null || quantity <= 0) {
			throw new IllegalArgumentException("無効なパラメータです");
		}

		// 既に同じ商品（色・サイズ含む）がカートにあるかチェック
		CartItem existingItem = cartItemMapper.findByUserIdAndProductIdAndOptions(
				userId, productId, selectedColor, selectedSize);

		if (existingItem != null) {
			// 既存の商品は数量を加算
			existingItem.setQuantity(existingItem.getQuantity() + quantity);
			cartItemMapper.update(existingItem);
		} else {
			// 新規商品をカートに追加
			CartItem newItem = new CartItem();
			newItem.setUserId(userId);
			newItem.setProductId(productId);
			newItem.setSelectedColor(selectedColor != null && selectedColor.trim().equals("なし") ? null : selectedColor);
			newItem.setSelectedSize(selectedSize != null && selectedSize.trim().equals("なし") ? null : selectedSize);
			newItem.setQuantity(quantity);

			cartItemMapper.insert(newItem);
		}
	}

	/**
	 * カートアイテムを更新（数量、色、サイズ）
	 * @param cartItemId カートアイテムID
	 * @param quantity 数量（nullの場合は変更なし）
	 * @param color 選択された色（nullの場合は変更なし）
	 * @param size 選択されたサイズ（nullの場合は変更なし）
	 * @return 追加した商品表示 
	 */
	@SuppressWarnings("null")
	@Override
	@Transactional
	public void updateCartItem(Long cartItemId, Integer quantity, String color, String size) {
		// パラメータ検証
		if (cartItemId == null) {
			throw new IllegalArgumentException("カートアイテムIDが無効です");
		}

		// カートアイテムを取得
		CartItem existingItem = cartItemMapper.findById(cartItemId);

		if (existingItem == null) {
			throw new RuntimeException("カートアイテムが見つかりません。ID: " + cartItemId);
		}

		// 商品情報を取得
		Product product = productMapper.findById(existingItem.getProductId());
		if (product == null) {
			throw new RuntimeException("商品が見つかりません。商品ID: " + existingItem.getProductId());
		}

		// 商品が販売中か確認
		if (product.getSalesStatus() == 0) {
			throw new RuntimeException("この商品は現在販売されていません");
		}

		// 数量が変更された場合
		if (quantity != null) {
			if (quantity <= 0) {
				throw new IllegalArgumentException("数量は1以上で入力してください");
			}

			if (quantity > product.getStockQuantity()) {
				throw new RuntimeException(
						"在庫が不足しています。現在の在庫: " + product.getStockQuantity() +
								"、希望数量: " + quantity);
			}

			existingItem.setQuantity(quantity);
		}

		// 色が変更された場合
		if (color != null) {
			// 色オプションの検証（必要に応じて）
			// 例: 商品の色オプションに含まれているか確認
			existingItem.setSelectedColor(color);
		}

		// サイズが変更された場合
		if (size != null) {
			// サイズオプションの検証（必要に応じて）
			existingItem.setSelectedSize(size);
		}

		// 色とサイズが両方変更された場合、同じ商品が既にカートにあるか確認
		if ((color != null || size != null) &&
				(color != null || !color.equals(existingItem.getSelectedColor()) ||
						size != null || !size.equals(existingItem.getSelectedSize()))) {

			// 新しい色・サイズの組み合わせで既にカートにあるか確認
			CartItem duplicateItem = cartItemMapper.findByUserAndProduct(
					existingItem.getUserId(),
					existingItem.getProductId(),
					color != null ? color : existingItem.getSelectedColor(),
					size != null ? size : existingItem.getSelectedSize());

			if (duplicateItem != null && !duplicateItem.getCartItemId().equals(cartItemId)) {
				throw new RuntimeException("同じ商品（色・サイズ）が既にカートに存在します");
			}
		}

		// 更新日時を設定
		existingItem.setUpdateTime(LocalDateTime.now());

		// データベースを更新
		int result = cartItemMapper.update(existingItem);
		if (result == 0) {
			throw new RuntimeException("カートアイテムの更新に失敗しました");
		}
	}

	/**
	 * 指定したカートアイテムの数量を更新する。
	 *
	 * @param cartItemId 更新対象のカートアイテムID
	 * @param quantity   更新後の数量
	 * @throws IllegalArgumentException 数量が無効またはカートアイテムが存在しない場合
	 */
	@Override
	@Transactional
	public void updateCartItemQuantity(Long cartItemId, Integer quantity) {
		if (quantity == null || quantity <= 0) {
			throw new IllegalArgumentException("数量は1以上で指定してください");
		}

		CartItem item = cartItemMapper.findById(cartItemId);
		if (item == null) {
			throw new IllegalArgumentException("カートアイテムが見つかりません");
		}

		item.setQuantity(quantity);
		cartItemMapper.update(item);
	}

	/**
	 * 指定したカートアイテムを削除する。
	 *
	 * @param cartItemId 削除対象のカートアイテムID
	 */
	@Override
	@Transactional
	public void deleteCartItem(Long cartItemId) {
		cartItemMapper.deleteById(cartItemId);
	}

	/**
	 * 指定ユーザーのカートを全て削除する。
	 * 注文確定後やユーザーのカート初期化時に使用。
	 *
	 * @param userId カートをクリアする対象ユーザーのID
	 */
	@Override
	@Transactional
	public void clearCart(Long userId) {
		cartItemMapper.deleteByUserId(userId);
	}

}