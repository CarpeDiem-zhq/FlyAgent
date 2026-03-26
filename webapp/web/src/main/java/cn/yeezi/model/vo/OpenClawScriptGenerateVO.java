package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "OpenClaw 生成响应")
public class OpenClawScriptGenerateVO {

    @Schema(description = "追踪id")
    private String traceId;

    @Schema(description = "批次id")
    private Long batchId;

    @Schema(description = "历史id")
    private Long historyId;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "功能id")
    private Long featureId;

    @Schema(description = "核心卖点id")
    private Long coreSellingPointId;

    @Schema(description = "策略id")
    private Long strategyId;

    @Schema(description = "展示文本")
    private String displayText;

    @Schema(description = "脚本文本")
    private String outputContent;

    @Schema(description = "质检结果")
    private ScriptQualityCheckVO qualityCheck;
}
