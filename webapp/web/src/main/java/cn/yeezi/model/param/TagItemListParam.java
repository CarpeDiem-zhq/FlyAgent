package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签项列表参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "标签项列表参数")
public class TagItemListParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "标签组id")
    private Long groupId;

    @Schema(description = "所属产品功能标签项id")
    private Long featureItemId;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
