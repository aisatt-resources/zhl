package aisato.ec.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

/**
 * 会員登録リクエストDTO
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @author zhl
 * @version 1.0.0
 */
@Data
public class RegisterRequest {
    
    /** ユーザー名 */
    @NotBlank(message = "ユーザー名を入力してください")
    @Size(min = 3, max = 50, message = "ユーザー名は3〜50文字で入力してください")
    private String username;
    
    /** パスワード */
    @NotBlank(message = "パスワードを入力してください")
    @Size(min = 6, message = "パスワードは6文字以上で入力してください")
    private String password;
    
    /** メールアドレス */
    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "有効なメールアドレスを入力してください")
    private String email;
    
    /** 電話番号 */
    @Pattern(regexp = "^\\d{10,11}$", message = "有効な電話番号を入力してください")
    private String phone;
    
    /** 本名 */
    @NotBlank(message = "本名を入力してください")
    private String realName;
}