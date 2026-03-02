package aisato.ec.service.serviceImpl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aisato.ec.entity.Order;
import aisato.ec.entity.OrderItem;
import aisato.ec.entity.User;
import aisato.ec.mapper.OrderMapper;
import aisato.ec.mapper.UserMapper;
import aisato.ec.service.OrderItemService;
import aisato.ec.service.OrderService;

/**
 * 注文サービス実装クラス（OrderItemServiceを使用）
 * 注文関連のビジネスロジックを実装
 */
@Service
public class OrderServiceImpl implements OrderService {
    
	/**
	 * 注文マーピング側呼び出す
	 */
    @Autowired
    private OrderMapper orderMapper;
    
	/**
	 * 注文件サービス側呼び出す
	 */
    @Autowired
    private OrderItemService orderItemService;
    
	/**
	 * ユーザーマーピング側呼び出す
	 */
    @Autowired
    private UserMapper userMapper;
    
    /**
     * ユーザーの注文履歴を取得（注文件含む）
     */
    @Override
    public List<Order> getOrderHistory(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("ユーザーIDが無効です");
        }
        
        List<Order> orders = orderMapper.findByUserId(userId);
        
        // 各注文の注文件を取得
        for (Order order : orders) {
            List<OrderItem> orderItems = orderItemService.getOrderItemsWithProductByOrderId(order.getOrderId());
            order.setOrderItems(orderItems);
        }
        
        return orders;
    }
    
    /**
     * 注文詳細を取得（注文件含む）
     */
    @Override
    public Order getOrderDetail(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("注文IDが無効です");
        }
        
        // 注文情報を取得
        Order order = orderMapper.findById(orderId);
        
        if (order == null) {
            throw new RuntimeException("注文が見つかりません。注文ID: " + orderId);
        }
        
        // ユーザー情報を取得
        User user = userMapper.findById(order.getUserId());
        order.setUser(user);
        
        // 支払い方法名を取得
        String paymentMethodName = getPaymentMethodName(order.getPaymentMethodId());
        order.setPaymentMethodName(paymentMethodName);
        
        // 注文件を取得
        List<OrderItem> orderItems = orderItemService.getOrderItemsWithProductByOrderId(orderId);
        order.setOrderItems(orderItems);
        
        return order;
    }
    
    /**
     * 支払い方法名を取得
     */
    private String getPaymentMethodName(Long paymentMethodId) {
        // 実際の実装ではpayment_methodsテーブルから取得
        // ここでは簡略化してコードベースで返す
        return switch (paymentMethodId.intValue()) {
            case 1 -> "クレジットカード";
            case 2 -> "PayPay";
            case 3 -> "楽天ペイ";
            case 4 -> "銀行振込";
            default -> "不明";
        };
    }
    
    /**
     * 注文を作成
     * 注文番号を生成し、注文と注文件を登録
     */
    @Override
    @Transactional
    public Long createOrder(Order order) {
        // 検証 
        validateOrder(order);
        
        // ユーザーが存在するか確認
        User user = userMapper.findById(order.getUserId());
        if (user == null) {
            throw new RuntimeException("ユーザーが見つかりません。ユーザーID: " + order.getUserId());
        }
        
        // 受取人情報を設定（未設定の場合、ユーザー情報を使用）
        if (order.getReceiverName() == null || order.getReceiverName().trim().isEmpty()) {
            order.setReceiverName(user.getRealName() != null ? user.getRealName() : user.getUsername());
        }
        
        if (order.getReceiverPhone() == null || order.getReceiverPhone().trim().isEmpty()) {
            order.setReceiverPhone(user.getPhone());
        }
        
        // 合計金額を再計算
        BigDecimal calculatedTotal = order.calculateTotalAmount();
        order.setTotalAmount(calculatedTotal);
        
        // 合計数量を計算
      //  Integer totalQuantity = order.calculateTotalQuantity();
        
        // 送料の計算（例：10,000円以上で送料無料）
        if (calculatedTotal.compareTo(new BigDecimal("10000")) < 0) {
            order.setShippingFee(new BigDecimal("800")); // 送料800円
        } else {
            order.setShippingFee(BigDecimal.ZERO);
        }
        
        // 実際の合計金額（商品合計 + 送料）
        BigDecimal actualTotal = calculatedTotal.add(order.getShippingFee());
        order.setTotalAmount(actualTotal);
        
        // 初期ステータスを設定（注文中）
        order.setOrderStatus(10);
        order.setPaymentStatus(0); // 未支払い
        order.setCreateTime(LocalDateTime.now());
        
        // 重複防止のため、注文番号を生成
        String orderNo = generateOrderNo();
        order.setOrderNo(orderNo);
        
        // トランザクション開始
        try {
            // 1. 注文を登録
            orderMapper.insert(order);
            Long orderId = order.getOrderId();
            
            // 2. 各注文件に注文IDを設定
            for (OrderItem item : order.getOrderItems()) {
                item.setOrderId(orderId);
            }
            
            // 3. 注文件を登録
            orderItemService.saveOrderItems(order.getOrderItems());
            
            return orderId;
            
        } catch (Exception e) {
            // エラー発生時はロールバック
            throw new RuntimeException("注文の作成に失敗しました: " + e.getMessage(), e);
        }
    }
    
    /**
     * 支払いを処理
     */
    @Override
    @Transactional
    public void processPayment(Long orderId, String transactionId) {
        if (orderId == null) {
            throw new IllegalArgumentException("注文IDが無効です");
        }
        
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("注文が見つかりません。注文ID: " + orderId);
        }
        
        // 既に支払い済みの場合はエラー
        if (order.isPaid()) {
            throw new RuntimeException("この注文は既に支払い済みです");
        }
        
        // 支払いトランザクションIDを設定
        order.setPaymentTransactionId(transactionId);
        
        // 支払いステータスを更新
        order.setPaymentStatus(1); // 支払い済み
        
        // 注文ステータスを更新（支払確認）
        order.setOrderStatus(20);
        order.setUpdateTime(LocalDateTime.now());
        
        orderMapper.updateStatus(order);
    }
    
    /**
     * キャンセル処理
     */
    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("注文IDが無効です");
        }
        
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            throw new RuntimeException("注文が見つかりません。注文ID: " + orderId);
        }
        
        // 既にキャンセル済みまたは完了済みの場合はエラー
        if (order.isCancelled()) {
            throw new RuntimeException("この注文は既にキャンセルされています");
        }
        
        if (order.isCompleted()) {
            throw new RuntimeException("この注文は既に完了しています");
        }
        
        // 注文ステータスをキャンセルに更新
        order.setOrderStatus(60);
        order.setUpdateTime(LocalDateTime.now());
        
        orderMapper.updateStatus(order);
    }
    
    /**
     * オーダー番号を生成
     * 形式: ORD + 年月日時分秒 + 4桁ランダム文字列
     */
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
        );
        String randomStr = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD" + timestamp + randomStr;
    }
    
    /**
     * オーダーのバリデーション
     */
    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("注文情報が空です");
        }
        
        if (order.getUserId() == null) {
            throw new IllegalArgumentException("ユーザーIDが必須です");
        }
        
        if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
            throw new IllegalArgumentException("注文件が必須です");
        }
        
        // 受取人情報のバリデーション
        if (order.getReceiverName() == null || order.getReceiverName().trim().isEmpty()) {
            throw new IllegalArgumentException("受取人氏名が必須です");
        }
        
        if (order.getReceiverPhone() == null || order.getReceiverPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("受取人電話番号が必須です");
        }
        
        if (order.getReceiverProvince() == null || order.getReceiverProvince().trim().isEmpty()) {
            throw new IllegalArgumentException("都道府県が必須です");
        }
        
        if (order.getReceiverCity() == null || order.getReceiverCity().trim().isEmpty()) {
            throw new IllegalArgumentException("市区町村が必須です");
        }
        
        if (order.getReceiverDetailAddress() == null || order.getReceiverDetailAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("詳細住所が必須です");
        }
        
        if (order.getReceiverPostalCode() == null || order.getReceiverPostalCode().trim().isEmpty()) {
            throw new IllegalArgumentException("郵便番号が必須です");
        }
        
        if (order.getPaymentMethodId() == null) {
            throw new IllegalArgumentException("支払い方法が必須です");
        }
    }
}