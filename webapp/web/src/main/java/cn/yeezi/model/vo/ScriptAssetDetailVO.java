package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "用户id")
    private Long userId;

    @Schema(description = "提示词id")
    private Long promptId;

    @Schema(description = "提示词快照")
    private String promptSnapshot;

    @Schema(description = "规则快照")
    private String ruleSnapshot;

    @Schema(description = "输入快照")
    private String inputSnapshot;

    @Schema(description = "标签快照")
    private String tagSnapshot;

    @Schema(description = "用户输入快照")
    private String userInputSnapshot;

    @Schema(description = "案例快照")
    private String caseSnapshot;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "路由策略")
    private String routeStrategy;

    @Schema(description = "生成内容")
    private String outputContent;

    @Schema(description = "父资产id")
    private Long parentAssetId;

    @Schema(description = "修订序号")
    private Integer revisionSeq;

    @Schema(description = "复制次数")
    private Integer copyCount;

    @Schema(description = "点赞次数")
    private Integer likeCount;

    @Schema(description = "收藏次数")
    private Integer favoriteCount;

    @Schema(description = "正向反馈次数")
    private Integer positiveFeedbackCount;

    @Schema(description = "负向反馈次数")
    private Integer negativeFeedbackCount;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
}
