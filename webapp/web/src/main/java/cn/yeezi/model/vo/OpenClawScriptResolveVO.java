package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "OpenClaw 解析响应")
public class OpenClawScriptResolveVO {

    @Schema(description = "状态")
    private String status;

    @Schema(description = "当前步骤")
    private String currentStep;

    @Schema(description = "追踪id")
    private String traceId;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "功能id")
    private Long featureId;

    @Schema(description = "核心卖点id")
    private Long coreSellingPointId;

    @Schema(description = "策略id")
    private Long strategyId;

    @Schema(description = "追问文案")
    private String askMessage;

    @Schema(description = "产品候选")
    private List<OpenClawOptionVO> productOptions;

    @Schema(description = "功能候选")
    private List<OpenClawOptionVO> featureOptions;

    @Schema(description = "卖点候选")
    private List<OpenClawOptionVO> sellingPointOptions;

    @Schema(description = "策略候选")
    private List<OpenClawOptionVO> strategyOptions;
}
