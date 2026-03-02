package aisato.ec.dto;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

/**
 * ログインリクエストDTO
 * 
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @author zhl
 * @version 1.0.0
 */
@Data
public class LoginRequest {
    
    /** ユーザー名またはメールアドレス */
    @NotBlank(message = "ユーザー名またはメールアドレスを入力してください")
    private String username;
    
    /** パスワード */
    @NotBlank(message = "パスワードを入力してください")
    private String password;
}