package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "产品列表参数")
public class ProductListParam {

    @Schema(description = "产品名称")
    private String productName;
}
