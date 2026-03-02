package aisato.ec.service;

import java.util.List;

import aisato.ec.entity.OrderItem;

/**
 * 注文件サービスインターフェース
 * 注文件関連のビジネスロジックを定義
 */
public interface OrderItemService {
    
    /**
     * 指定された注文の注文件を取得
     * @param orderId 注文ID
     * @return 注文件リスト
     */
    List<OrderItem> getOrderItemsByOrderId(Long orderId);
    
    /**
     * 指定された注文の注文件を取得（商品情報含む）
     * @param orderId 注文ID
     * @return 注文件リスト
     */
    List<OrderItem> getOrderItemsWithProductByOrderId(Long orderId);
    
    /**
     * 複数の注文件を登録
     * @param orderItems 注文件リスト
     * @return 登録された件数
     */
    int saveOrderItems(List<OrderItem> orderItems);
    
    /**
     * 指定された注文の注文件を削除
     * @param orderId 注文ID
     */
    void deleteOrderItemsByOrderId(Long orderId);
}