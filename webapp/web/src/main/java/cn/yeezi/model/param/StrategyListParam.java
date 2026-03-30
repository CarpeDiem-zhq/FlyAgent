package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "策略列表参数")
public class StrategyListParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "功能id")
    private Long featureId;

    @Schema(description = "核心卖点id列表")
    private List<Long> coreSellingPointIds;
}
