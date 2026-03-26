package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提示词更新参数
 *
 * @author codex
 * @since 2026-01-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "提示词更新参数")
public class PromptUpdateParam {

    @NotNull(message = "提示词id不能为空")
    @Schema(description = "提示词id")
    private Long id;

    @NotBlank(message = "提示词名称不能为空")
    @Schema(description = "提示词名称")
    private String promptName;

    @NotBlank(message = "系统提示词不能为空")
    @Schema(description = "系统提示词")
    private String systemPrompt;

    @Schema(description = "是否启用")
    private Boolean enabled;
}