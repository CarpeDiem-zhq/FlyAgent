package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 产品功能信息
 *
 * @author codex
 * @since 2026-03-04
 */
@Data
@Schema(description = "产品功能信息")
public class TagFeatureVO {

    @Schema(description = "产品功能标签项id")
    private Long id;

    @Schema(description = "产品功能名称")
    private String featureName;
}

