package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提示词创建参数
 *
 * @author codex
 * @since 2026-01-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "提示词创建参数")
public class PromptCreateParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @NotBlank(message = "系统提示词不能为空")
    @Schema(description = "系统提示词")
    private String systemPrompt;
}
