package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提示词详情参数
 *
 * @author codex
 * @since 2026-01-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "提示词详情参数")
public class PromptDetailParam {

    @NotNull(message = "提示词id不能为空")
    @Schema(description = "提示词id")
    private Long promptId;
}