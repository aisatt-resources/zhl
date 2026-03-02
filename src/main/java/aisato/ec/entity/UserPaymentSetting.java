package aisato.ec.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー支払い設定エンティティクラス
 * user_payment_settingsテーブルに対応
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @NoArgsConstructor 引数のないデフォルトコンストラクタを⾃動⽣成する。
 * @AllArgsConstructor　全てのフィールドを引数とするコンストラクタを⾃動⽣成する。
 * @author zhl
 * @version 1.0.0 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserPaymentSetting {
    
    /** 設定ID */
    private Long settingId;
    
    /** ユーザーID */
    private Long userId;
    
    /** デフォルト支払い方法ID */
    private Long defaultMethodId;
    
    /** 作成日時 */
    private LocalDateTime createTime;
    
    /** 更新日時 */
    private LocalDateTime updateTime;
}