package aisato.ec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import aisato.ec.entity.Product;


/**
 * 商品マーケティングインターフェース
 * 
 * @author zhl
 * @version 1.0.0
 * @Mapper インターフェースをMyBatisのマッパーとしてマークする。Springとの連携時に使⽤。
 */
@Mapper
public interface ProductMapper {

	  /**
     * 全商品を取得（販売中のみ）
     * @return 商品リスト
     */
    List<Product> findAll();
    
    /**
     * カテゴリー別に商品を取得
     * @param categoryId カテゴリーID
     * @return 商品リスト
     */
    List<Product> findByCategory(@Param("categoryId") Long categoryId);
    
    /**
     * 検索キーワードで商品を検索（商品名、説明、ブランド）
     * @param keyword 検索キーワード
     * @return 商品リスト
     */
    List<Product> search(@Param("keyword") String keyword);
    
    /**
     * 商品IDで商品を取得
     * @param productId 商品ID
     * @return 商品エンティティ
     */
    Product findById(@Param("productId") Long productId);
    
    /**
     * 複数の商品を取得
     * @param productIds 商品IDリスト
     * @return 商品リスト
     */
    List<Product> findByIds(@Param("productIds") List<Long> productIds);
	

}
