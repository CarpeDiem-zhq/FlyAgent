package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "产品功能")
public class ProductFeatureVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "功能名称")
    private String featureName;

    @Schema(description = "是否启用")
    private Boolean enabled;
}
