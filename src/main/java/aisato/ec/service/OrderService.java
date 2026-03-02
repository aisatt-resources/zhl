package aisato.ec.service;

import java.util.List;

import aisato.ec.entity.Order;

/**
 * 注文サービスインターフェース
 * 注文関連のビジネスロジックを定義
 */
public interface OrderService {
    
    /**
     * ユーザーの注文履歴を取得
     * @param userId ユーザーID
     * @return 注文リスト
     */
    List<Order> getOrderHistory(Long userId);
    
    /**
     * 注文詳細を取得
     * @param orderId 注文ID
     * @return 注文エンティティ
     */
    Order getOrderDetail(Long orderId);
    
    /**
     * 注文を作成
     * @param order 注文エンティティ
     * @return 作成された注文ID
     */
    Long createOrder(Order order);
    
    /**
     * 支払いを処理
     * @param orderId 注文ID
     * @param transactionId トランザクションID
     */
    void processPayment(Long orderId, String transactionId);
    
    /**
     * キャンセル処理
     * @param orderId 注文ID
     */
    void cancelOrder(Long orderId);
}