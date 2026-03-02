package aisato.ec.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注文件エンティティクラス
 * order_itemsテーブルに対応
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @NoArgsConstructor 引数のないデフォルトコンストラクタを⾃動⽣成する。
 * @AllArgsConstructor　全てのフィールドを引数とするコンストラクタを⾃動⽣成する。
 * @author zhl
 * @version 1.0.0 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    
    /** 注文件ID */
    private Long orderItemId;
    
    /** 注文ID */
    private Long orderId;
    
    /** 商品ID */
    private Long productId;
    
    /** 商品名称 */
    private String productName;
    
    /** 商品価格 */
    private BigDecimal productPrice;
    
    /** 数量 */
    private Integer quantity;
    
    /** 選択された色 */
    private String selectedColor;
    
    /** 選択されたサイズ */
    private String selectedSize;
    
    /** 小計金額 */
    private BigDecimal subtotalAmount;
    
    /** 作成日時 */
    private LocalDateTime createTime;
    
    /** 関連商品情報（JOIN用） */
    private Product product;
}