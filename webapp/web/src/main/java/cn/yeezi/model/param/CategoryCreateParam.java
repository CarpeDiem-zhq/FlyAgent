package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 类目创建参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "类目创建参数")
public class CategoryCreateParam {

    @NotBlank(message = "类目名称不能为空")
    @Schema(description = "类目名称")
    private String categoryName;

    @Schema(description = "排序")
    private Integer sortOrder;
}