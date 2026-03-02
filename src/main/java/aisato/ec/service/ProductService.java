package aisato.ec.service;

import java.util.List;

import aisato.ec.entity.Product;

/**
 * 商品サービスインターフェース
 * 
 */
public interface ProductService {
	
	/**
     * 全商品を取得
     * @return 商品リスト
     */
    List<Product> getAllProducts();
    
    /**
     * カテゴリー別に商品を取得
     * @param categoryId カテゴリーID
     * @return 商品リスト
     */
    List<Product> getProductsByCategory(Long categoryId);
    
    /**
     * 検索キーワードで商品を検索
     * @param keyword 検索キーワード
     * @return 商品リスト
     */
    List<Product> searchProducts(String keyword);
    
    /**
     * 商品詳細を取得
     * @param productId 商品ID
     * @return 商品エンティティ
     */
    Product getProductDetail(Long productId);

}
