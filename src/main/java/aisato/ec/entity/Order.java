package aisato.ec.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注文エンティティクラス
 * ordersテーブルに対応
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @NoArgsConstructor 引数のないデフォルトコンストラクタを⾃動⽣成する。
 * @AllArgsConstructor　全てのフィールドを引数とするコンストラクタを⾃動⽣成する。
 * @author zhl
 * @version 1.0.0 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    /** 注文ID */
    private Long orderId;
    
    /** 注文番号 */
    private String orderNo;
    
    /** ユーザーID */
    private Long userId;
    
    /** 注文ステータス(10:注文中,20:支払確認,30:発送準備,40:発送済み,50:完了,60:キャンセル) */
    private Integer orderStatus;
    
    /** 合計金額 */
    private BigDecimal totalAmount;
    
    /** 送料 */
    private BigDecimal shippingFee;
    
    /** 受取人氏名 */
    private String receiverName;
    
    /** 受取人電話番号 */
    private String receiverPhone;
    
    /** 受取人都道府県 */
    private String receiverProvince;
    
    /** 受取人市区町村 */
    private String receiverCity;
    
    /** 受取人区/郡 */
    private String receiverDistrict;
    
    /** 受取人詳細住所 */
    private String receiverDetailAddress;
    
    /** 受取人郵便番号 */
    private String receiverPostalCode;
    
    /** 支払い方法ID */
    private Long paymentMethodId;
    
    /** 支払いステータス(0:未支払い,1:支払い済み,2:支払い失敗,3:返金) */
    private Integer paymentStatus;
    
    /** 支払いトランザクションID */
    private String paymentTransactionId;
    
    /** 備考 */
    private String remarks;
    
    /** 作成日時 */
    private LocalDateTime createTime;
    
    /** 更新日時 */
    private LocalDateTime updateTime;
    
    /** 関連注文件リスト */
    private List<OrderItem> orderItems;
    
    /** 支払い方法名 */
    private String paymentMethodName;
    
    /** ユーザー情報 */
    private User user;
    
    /**
     * 合計金額を計算
     * 注文件の小計金額の合計
     */
    public BigDecimal calculateTotalAmount() {
        if (this.orderItems == null || this.orderItems.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            total = total.add(item.getSubtotalAmount());
        }
        
        return total;
    }
    
    /**
     * 合計数量を計算
     */
    public Integer calculateTotalQuantity() {
        if (this.orderItems == null || this.orderItems.isEmpty()) {
            return 0;
        }
        
        int total = 0;
        for (OrderItem item : orderItems) {
            total += item.getQuantity();
        }
        
        return total;
    }
    
    /**
     * 支払い済みかどうか
     */
    public boolean isPaid() {
        return this.paymentStatus != null && this.paymentStatus == 1;
    }
    
    /**
     * 完了した注文かどうか
     */
    public boolean isCompleted() {
        return this.orderStatus != null && this.orderStatus == 50;
    }
    
    /**
     * キャンセルされた注文かどうか
     */
    public boolean isCancelled() {
        return this.orderStatus != null && this.orderStatus == 60;
    }
}