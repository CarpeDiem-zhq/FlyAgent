package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 脚本生成结果
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@Schema(description = "脚本生成结果")
public class ScriptGenerateVO {

    @Schema(description = "批次id")
    private Long batchId;

    @Schema(description = "历史记录id")
    private Long historyId;

    @Schema(description = "批次序号")
    private Integer itemSeq;

    @Schema(description = "资产id")
    private Long assetId;

    @Schema(description = "产品id")
    private Long productId;

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
}
