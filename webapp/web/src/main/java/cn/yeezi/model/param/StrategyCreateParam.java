package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "策略创建参数")
public class StrategyCreateParam {

    @NotBlank(message = "策略名称不能为空")
    @Schema(description = "策略名称")
    private String strategyName;

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @NotNull(message = "功能id不能为空")
    @Schema(description = "功能id")
    private Long featureId;

    @NotNull(message = "核心卖点id不能为空")
    @Size(min = 1, message = "至少选择一个核心卖点")
    @Schema(description = "核心卖点id列表")
    private List<Long> coreSellingPointIds;

    @NotBlank(message = "目标受众不能为空")
    @Schema(description = "目标受众")
    private String targetAudience;

    @NotBlank(message = "目标场景不能为空")
    @Schema(description = "目标场景")
    private String targetScene;

    @NotBlank(message = "语调风格不能为空")
    @Schema(description = "语调风格")
    private String toneStyle;

    @NotBlank(message = "行动指令不能为空")
    @Schema(description = "行动指令")
    private String callToAction;

    @NotBlank(message = "字数限制不能为空")
    @Schema(description = "字数限制")
    private String adWords;

}
