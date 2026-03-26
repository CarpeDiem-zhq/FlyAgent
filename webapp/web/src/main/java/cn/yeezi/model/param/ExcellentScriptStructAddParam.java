package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 增加优秀脚本参数
 *
 * @author codex
 * @since 2026-03-02
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "增加优秀脚本参数")
public class ExcellentScriptStructAddParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @NotBlank(message = "优秀脚本不能为空")
    @Schema(description = "优秀脚本")
    private String excellentScript;

    @NotBlank(message = "产品名称不能为空")
    @Schema(description = "产品名称")
    private String productName;

    @NotBlank(message = "产品功能名称不能为空")
    @Schema(description = "产品功能名称")
    private String functionName;
}
