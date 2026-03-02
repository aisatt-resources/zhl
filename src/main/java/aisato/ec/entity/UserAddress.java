package aisato.ec.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザー住所エンティティクラス
 * user_addressesテーブルに対応
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @NoArgsConstructor 引数のないデフォルトコンストラクタを⾃動⽣成する。
 * @AllArgsConstructor　全てのフィールドを引数とするコンストラクタを⾃動⽣成する。
 * @author zhl
 * @version 1.0.0 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAddress {
    
    /** 住所ID */
    private Long addressId;
    
    /** ユーザーID */
    private Long userId;
    
    /** 受取人氏名 */
    private String receiverName;
    
    /** 電話番号 */
    private String phone;
    
    /** 都道府県 */
    private String province;
    
    /** 市区町村 */
    private String city;
    
    /** 区/郡 */
    private String district;
    
    /** 詳細住所 */
    private String detailAddress;
    
    /** 郵便番号 */
    private String postalCode;
    
    /** デフォルト住所フラグ (0:いいえ, 1:はい) */
    private Integer isDefault;
    
    /** 作成日時 */
    private LocalDateTime createTime;
    
    /** 更新日時 */
    private LocalDateTime updateTime;
    
    /**
     * 完全な住所を取得
     */
    public String getFullAddress() {
        return province + city + district + detailAddress;
    }
}