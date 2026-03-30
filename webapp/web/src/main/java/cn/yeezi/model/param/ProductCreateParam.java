package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 产品创建参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "产品创建参数")
public class ProductCreateParam {

    @Schema(description = "产品描述")
    private String productDesc;

    @NotBlank(message = "产品名称不能为空")
    @Schema(description = "产品名称")
    private String productName;
}
