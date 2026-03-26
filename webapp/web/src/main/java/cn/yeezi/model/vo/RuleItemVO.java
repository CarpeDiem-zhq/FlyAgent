package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 规则项信息
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@Schema(description = "规则项信息")
public class RuleItemVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "规则内容")
    private String ruleContent;

    @Schema(description = "版本号")
    private Integer version;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}