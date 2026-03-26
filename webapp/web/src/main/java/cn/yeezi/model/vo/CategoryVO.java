package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类目信息
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@Schema(description = "类目信息")
public class CategoryVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "类目名称")
    private String categoryName;

    @Schema(description = "排序")
    private Integer sortOrder;
}