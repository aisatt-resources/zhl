package aisato.ec.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザーエンティティ
 * ユーザー管理情報
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @NoArgsConstructor 引数のないデフォルトコンストラクタを⾃動⽣成する。
 * @AllArgsConstructor　全てのフィールドを引数とするコンストラクタを⾃動⽣成する。
 * @author zhl
 * @version 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	/** ユーザーID */
    private Long userId;
    
    /** ユーザー名 */
    private String username;
    
    /** パスワード(暗号化保存) */
    private String password;
    
    /** メールアドレス */
    private String email;
    
    /** 電話番号 */
    private String phone;
    
    /** 本名 */
    private String realName;
    
    /** 性別(0:女性,1:男性) */
    private Integer gender;
    
    /** 生年月日 */
    private LocalDate birthDate;
    
    /** ステータス(0:無効,1:有効) */
    private Integer status;
    
    /** 作成日時 */
    private LocalDateTime createTime;
    
    /** 更新日時 */
    private LocalDateTime updateTime;
}
