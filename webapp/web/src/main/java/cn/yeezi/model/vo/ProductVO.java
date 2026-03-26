package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 产品信息
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@Schema(description = "产品信息")
public class ProductVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "产品名称")
    private String productName;

    @Schema(description = "类目id")
    private Long categoryId;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
