package aisato.ec.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

/**
 * カート追加リクエストDTO
 * @Data @ToString 、@EqualsAndHashCode、@Getter 、@Setter 、@RequiredArgsConstructor以上の機能のまとめ。
 * @author zhl
 * @version 1.0.0
 */
@Data
public class CartAddRequest {
    
    /** 商品ID */
    @NotNull(message = "商品を選択してください")
    private Long productId;
    
    /** 数量 */
    @NotNull(message = "数量を入力してください")
    @Min(value = 1, message = "数量は1以上で入力してください")
    private Integer quantity;
    
    /** 選択された色 */
    private String selectedColor;
    
    /** 選択されたサイズ */
    private String selectedSize;
}