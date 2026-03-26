package cn.yeezi.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "OpenClaw 草稿")
public class OpenClawScriptDraftDTO {

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
}
