package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * 标签项信息
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@Schema(description = "标签项信息")
public class TagItemVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "标签组id")
    private Long groupId;

    @Schema(description = "标签名称")
    private String tagName;

    @Schema(description = "所属产品功能标签项id")
    private Long featureItemId;

    @Schema(description = "所属产品功能标签项名称")
    private String featureTagName;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "产品功能子标签")
    private List<TagItemVO> children;
}
