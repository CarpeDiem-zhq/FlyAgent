package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签项更新参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "标签项更新参数")
public class TagItemUpdateParam {

    @NotNull(message = "标签id不能为空")
    @Schema(description = "标签id")
    private Long id;

    @NotBlank(message = "标签名称不能为空")
    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "所属产品功能标签项id")
    private Long featureItemId;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "排序")
    private Integer sortOrder;
}
