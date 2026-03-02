package aisato.ec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import aisato.ec.entity.CartItem;

/**
 * カートアイテムマーピングインターフェース
 * データベースのcart_itemsテーブルへのアクセスを定義
 * @author zhl
 * @version 1.0.0
 * @Mapper インターフェースをMyBatisのマッパーとしてマークする。Springとの連携時に使用。
 */
@Mapper
public interface CartItemMapper {
    
	/**
     * ユーザーのカートアイテムを取得
     * @param userId ユーザーID
     * @return カートアイテムリスト
     */
    List<CartItem> findByUserId(@Param("userId") Long userId);
    
    /**
     * カートアイテムを追加
     * @param cartItem カートアイテムエンティティ
     * @return 影響を受けた行数
     */
    int insert(CartItem cartItem);
    
    /**
     * カートアイテムを更新
     * @param cartItem カートアイテムエンティティ
     * @return 影響を受けた行数
     */
    int update(CartItem cartItem);
    
    /**
     * 指定されたユーザーID、商品ID、およびオプション（色・サイズ）でカートアイテムを検索する。
     * 
     * @param userId        ユーザーID
     * @param productId     商品ID
     * @param selectedColor 選択された色（null可）
     * @param selectedSize  選択されたサイズ（null可）
     * @return 条件に一致するカートアイテム、存在しない場合はnull
     */
    CartItem findByUserIdAndProductIdAndOptions(
        @Param("userId") Long userId,
        @Param("productId") Long productId,
        @Param("selectedColor") String selectedColor,
        @Param("selectedSize") String selectedSize
    );
    
    /**
     * 指定したカートアイテムIDのアイテムを削除する。
     * 
     * @param cartItemId 削除対象のカートアイテムID
     */
    void deleteById(@Param("cartItemId") Long cartItemId);
    
    /**
     * ユーザーの全カートアイテムを削除
     * @param userId ユーザーID
     * @return 影響を受けた行数
     */
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * カートアイテムを商品別に取得（色、サイズも考慮）
     * @param userId ユーザーID
     * @param productId 商品ID
     * @param selectedColor 選択された色（オプション）
     * @param selectedSize 選択されたサイズ（オプション）
     * @return カートアイテム
     */
    CartItem findByUserAndProduct(
        @Param("userId") Long userId, 
        @Param("productId") Long productId,
        @Param("selectedColor") String selectedColor,
        @Param("selectedSize") String selectedSize
    );
    
    /**
     * カートアイテムを主キーで取得
     * @param cartItemId カートアイテムID
     * @return カートアイテム
     */
    CartItem findById(@Param("cartItemId") Long cartItemId);

}