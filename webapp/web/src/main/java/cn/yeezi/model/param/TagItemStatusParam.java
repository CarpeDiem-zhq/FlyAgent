package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签项状态参数
 *
 * @author codex
 * @since 2026-01-21
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "标签项状态参数")
public class TagItemStatusParam {

    @NotNull(message = "标签项id不能为空")
    @Schema(description = "标签项id")
    private Long id;

    @NotNull(message = "启用状态不能为空")
    @Schema(description = "是否启用")
    private Boolean enabled;
}
