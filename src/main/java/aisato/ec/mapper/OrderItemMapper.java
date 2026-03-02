package aisato.ec.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import aisato.ec.entity.OrderItem;

/**
 * 注文件マーピングインターフェース
 * データベースのorder_itemsテーブルへのアクセスを定義
 */
@Mapper
public interface OrderItemMapper {
    
    /**
     * 指定された注文の注文件を取得
     * @param orderId 注文ID
     * @return 注文件リスト
     */
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 複数の注文件を登録（バッチインサート）
     * @param orderItems 注文件リスト
     * @return 影響を受けた行数
     */
    int batchInsert(@Param("orderItems") List<OrderItem> orderItems);
    
    /**
     * 指定された注文の注文件を削除
     * @param orderId 注文ID
     * @return 影響を受けた行数
     */
    int deleteByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 指定された注文の注文件を取得（商品情報含む）
     * @param orderId 注文ID
     * @return 注文件リスト
     */
    List<OrderItem> findByOrderIdWithProduct(@Param("orderId") Long orderId);
}