package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Data;

/**
 * 标签组信息
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@Schema(description = "标签组信息")
public class TagGroupVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "标签组编码")
    private String groupCode;

    @Schema(description = "标签组名称")
    private String groupName;

    @Schema(description = "输入类型")
    private String inputType;

    @Schema(description = "是否必填")
    private Boolean required;

    @Schema(description = "排序")
    private Integer sortOrder;

    @Schema(description = "标签项列表")
    private List<TagItemVO> tags;
}