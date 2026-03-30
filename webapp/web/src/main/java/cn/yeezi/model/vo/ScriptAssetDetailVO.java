package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 脚本资产详情
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@Schema(description = "脚本资产详情")
public class ScriptAssetDetailVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "功能id")
    private Long featureId;

    @Schema(description = "核心卖点id列表")
    private List<Long> coreSellingPointIds;

    @Schema(description = "策略id")
    private Long strategyId;

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "提示词id")
    private Long promptId;

    @Schema(description = "系统提示词快照")
    private String systemPromptSnapshot;

    @Schema(description = "策略快照")
    private String strategySnapshot;

    @Schema(description = "功能快照")
    private String featureSnapshot;

    @Schema(description = "卖点快照")
    private String sellingPointSnapshot;

    @Schema(description = "脚本标题")
    private String scriptTitle;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "脚本内容")
    private String scriptContent;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
