package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 标签组创建参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "标签组创建参数")
public class TagGroupCreateParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @NotBlank(message = "标签组编码不能为空")
    @Schema(description = "标签组编码")
    private String groupCode;

    @NotBlank(message = "标签组名称不能为空")
    @Schema(description = "标签组名称")
    private String groupName;

    @NotBlank(message = "输入类型不能为空")
    @Schema(description = "输入类型")
    private String inputType;

    @Schema(description = "是否必填")
    private Boolean required;

    @Schema(description = "排序")
    private Integer sortOrder;
}
