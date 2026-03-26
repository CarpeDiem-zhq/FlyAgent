package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "策略")
public class StrategyVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "策略名称")
    private String strategyName;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "功能id")
    private Long featureId;

    @Schema(description = "核心卖点id")
    private Long coreSellingPointId;

    @Schema(description = "目标受众")
    private String targetAudience;

    @Schema(description = "目标场景")
    private String targetScene;

    @Schema(description = "语调风格")
    private String toneStyle;

    @Schema(description = "行动指令")
    private String callToAction;

    @Schema(description = "字数限制")
    private String adWords;

    @Schema(description = "提示词id")
    private Long promptId;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
