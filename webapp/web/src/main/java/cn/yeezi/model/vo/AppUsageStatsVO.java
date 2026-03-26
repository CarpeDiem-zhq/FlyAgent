package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 应用使用统计
 *
 * @author codex
 * @since 2026-03-23
 */
@Data
@Schema(description = "应用使用统计")
public class AppUsageStatsVO {

    @Schema(description = "应用类型编码")
    private String appCode;

    @Schema(description = "当前登录用户使用次数")
    private Long currentUserCount;

    @Schema(description = "全局总使用次数")
    private Long totalCount;
}
