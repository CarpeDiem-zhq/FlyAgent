package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "核心卖点更新参数")
public class FeatureSellingPointUpdateParam {

    @NotNull(message = "核心卖点id不能为空")
    @Schema(description = "核心卖点id")
    private Long id;

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @NotNull(message = "功能id不能为空")
    @Schema(description = "功能id")
    private Long featureId;

    @NotBlank(message = "核心卖点不能为空")
    @Schema(description = "核心卖点")
    private String sellingPointName;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
