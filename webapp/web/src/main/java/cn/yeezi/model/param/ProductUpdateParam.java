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
@Schema(description = "产品更新参数")
public class ProductUpdateParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long id;

    @Schema(description = "类目id")
    private Long categoryId;

    @NotBlank(message = "产品名称不能为空")
    @Schema(description = "产品名称")
    private String productName;
}
