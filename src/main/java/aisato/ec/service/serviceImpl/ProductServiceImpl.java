package aisato.ec.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aisato.ec.entity.Product;
import aisato.ec.mapper.ProductMapper;
import aisato.ec.service.ProductService;
import lombok.extern.slf4j.Slf4j;


/**
 * 商品サービス実装
 * 
 * @author zhl
 * @version 1.0.0 
 */
@Service
@Slf4j
public class ProductServiceImpl implements ProductService{
	
	/**
	 * 商品マーケティング側呼び出す
	 */
	@Autowired
	private  ProductMapper productMapper;
	
	/**
     * 全商品を取得（販売中のみ）
     */
    @Override
    public List<Product> getAllProducts() {
        return productMapper.findAll();
    }
    
    /**
     * カテゴリー別に商品を取得
     */
    @Override
    public List<Product> getProductsByCategory(Long categoryId) {
        return productMapper.findByCategory(categoryId);
    }
    
    /**
     * 検索キーワードで商品を検索
     * 商品名、説明、ブランドで部分一致検索
     */
    @Override
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllProducts();
        }
        return productMapper.search("%" + keyword.trim() + "%");
    }
    
    /**
     * 商品詳細を取得
     */
    @Override
    public Product getProductDetail(Long productId) {
        return productMapper.findById(productId);
    }
	
}
