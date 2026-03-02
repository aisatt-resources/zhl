package aisato.ec.service.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisato.ec.entity.OrderItem;
import aisato.ec.mapper.OrderItemMapper;
import aisato.ec.service.OrderItemService;

/**
 * 注文件サービス実装クラス
 * 注文件関連のビジネスロジックを実装
 */
@Service
public class OrderItemServiceImpl implements OrderItemService {
    
	/**
	 * 注文件マーピング側呼び出す
	 */
    @Autowired
    private OrderItemMapper orderItemMapper;
    
    /**
     * 指定された注文の注文件を取得
     */
    @Override
    public List<OrderItem> getOrderItemsByOrderId(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("注文IDが無効です");
        }
        
        List<OrderItem> orderItems = orderItemMapper.findByOrderId(orderId);
        
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("注文件が見つかりません。注文ID: " + orderId);
        }
        
        return orderItems;
    }
    
    /**
     * 指定された注文の注文件を取得（商品情報含む）
     */
    @Override
    public List<OrderItem> getOrderItemsWithProductByOrderId(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("注文IDが無効です");
        }
        
        List<OrderItem> orderItems = orderItemMapper.findByOrderIdWithProduct(orderId);
        
        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("注文件が見つかりません。注文ID: " + orderId);
        }
        
        return orderItems;
    }
    
    /**
     * 複数の注文件を登録
     */
    @Override
    @Transactional
    public int saveOrderItems(List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("注文件リストが空です");
        }
        
        // 各注文件の検証
        for (OrderItem item : orderItems) {
            validateOrderItem(item);
        }
        
        // バッチインサート実行
        int count = orderItemMapper.batchInsert(orderItems);
        
        if (count != orderItems.size()) {
            throw new RuntimeException("注文件の登録に失敗しました。期待: " + orderItems.size() + ", 実際: " + count);
        }
        
        return count;
    }
    
    /**
     * 指定された注文の注文件を削除
     */
    @Override
    @Transactional
    public void deleteOrderItemsByOrderId(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("注文IDが無効です");
        }
        
        orderItemMapper.deleteByOrderId(orderId);
    }
    
    /**
     * 注文件の検証
     */
    private void validateOrderItem(OrderItem orderItem) {
        if (orderItem.getOrderId() == null) {
            throw new IllegalArgumentException("注文IDが必須です");
        }
        
        if (orderItem.getProductId() == null) {
            throw new IllegalArgumentException("商品IDが必須です");
        }
        
        if (orderItem.getProductName() == null || orderItem.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("商品名称が必須です");
        }
        
        if (orderItem.getProductPrice() == null) {
            throw new IllegalArgumentException("商品価格が必須です");
        }
        
        if (orderItem.getQuantity() == null || orderItem.getQuantity() <= 0) {
            throw new IllegalArgumentException("数量は1以上で入力してください");
        }
        
        if (orderItem.getSubtotalAmount() == null) {
            throw new IllegalArgumentException("小計金額が必須です");
        }
        
        // 小計金額の検証（商品価格 × 数量）
        double expectedSubtotal = orderItem.getProductPrice().doubleValue() * orderItem.getQuantity();
        double actualSubtotal = orderItem.getSubtotalAmount().doubleValue();
        
        if (Math.abs(expectedSubtotal - actualSubtotal) > 0.01) {
            throw new IllegalArgumentException(
                "小計金額が正しくありません。期待: " + expectedSubtotal + ", 実際: " + actualSubtotal
            );
        }
    }
}