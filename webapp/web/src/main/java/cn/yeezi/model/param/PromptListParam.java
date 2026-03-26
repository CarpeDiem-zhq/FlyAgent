package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提示词列表参数
 *
 * @author codex
 * @since 2026-01-13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "提示词列表参数")
public class PromptListParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "是否启用")
    private Boolean enabled;
}