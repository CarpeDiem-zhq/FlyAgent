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
@Schema(description = "产品功能更新参数")
public class ProductFeatureUpdateParam {

    @NotNull(message = "功能id不能为空")
    @Schema(description = "功能id")
    private Long id;

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @NotBlank(message = "功能名称不能为空")
    @Schema(description = "功能名称")
    private String featureName;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
