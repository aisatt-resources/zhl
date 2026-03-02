package aisato.ec.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支払い方法エンティティクラス
 * payment_methodsテーブルに対応
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @NoArgsConstructor 引数のないデフォルトコンストラクタを⾃動⽣成する。
 * @AllArgsConstructor　全てのフィールドを引数とするコンストラクタを⾃動⽣成する。
 * @author zhl
 * @version 1.0.0 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {
    
    /** 支払い方法ID */
    private Long methodId;
    
    /** 支払い方法名称（例: クレジットカード, PayPay） */
    private String methodName;
    
    /** 支払い方法コード（例: credit_card, paypay） */
    private String methodCode;
    
    /** 支払い方法の説明文 */
    private String description;
    
    /** 有効フラグ (0: 無効, 1: 有効) */
    private Integer isActive;
    
    /** 表示順序（小さい順に表示） */
    private Integer sortOrder;
    
    /** 作成日時 */
    private LocalDateTime createTime;
}