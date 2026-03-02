package aisato.ec.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



/**
 * 商品情報エンティティ
 * 商品管理情報
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @NoArgsConstructor 引数のないデフォルトコンストラクタを⾃動⽣成する。
 * @AllArgsConstructor　全てのフィールドを引数とするコンストラクタを⾃動⽣成する。
 * @author zhl
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    /** 商品ID */
    private Long productId;
    
    /** 商品名称 */
    private String productName;
    
    /** 商品コード */
    private String productCode;
    
    /** カテゴリーID */
    private Long categoryId;
    
    /** ブランド */
    private String brand;
    
    /** 著者 */
    private String author;
    
    /** 価格 */
    private BigDecimal price;
    
    /** 在庫数量 */
    private Integer stockQuantity;
    
    /** 商品説明 */
    private String description;
    
    /** 画像URL */
    private String imageUrl;
    
    /** 販売ステータス(0:非販売,1:販売中) */
    private Integer salesStatus;
    
    /** 重さ(g) */
    private BigDecimal weight;
    
    /** 寸法(長さ×幅×高さ) */
    private String dimensions;
    
    /** 色オプション(JSON形式) */
    private String colorOptions;
    
    /** サイズオプション(JSON形式) */
    private String sizeOptions;
    
    /** 作成日時 */
    private LocalDateTime createTime;
    
    /** 更新日時 */
    private LocalDateTime updateTime;
    
    /** カテゴリー名（JOIN用） */
    private String categoryName;
}
