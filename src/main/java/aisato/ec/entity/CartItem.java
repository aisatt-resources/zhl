package aisato.ec.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カートアイテムエンティティ
 * cart_items情報
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @NoArgsConstructor 引数のないデフォルトコンストラクタを⾃動⽣成する。
 * @AllArgsConstructor　全てのフィールドを引数とするコンストラクタを⾃動⽣成する。
 * @author zhl
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItem {
    
    /** カートアイテムID */
    private Long cartItemId;
    
    /** ユーザーID */
    private Long userId;
    
    /** 商品ID */
    private Long productId;
    
    /** 数量 */
    private Integer quantity;
    
    /** 選択された色 */
    private String selectedColor;
    
    /** 選択されたサイズ */
    private String selectedSize;
    
    /** 追加日時 */
    private LocalDateTime addTime;
    
    /** 更新日時 */
    private LocalDateTime updateTime;
    
    /** 商品情報（JOIN用） */
    private Product product;
}