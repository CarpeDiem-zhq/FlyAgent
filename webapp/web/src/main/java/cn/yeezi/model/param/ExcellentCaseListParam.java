package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 优秀案例列表参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "优秀案例列表参数")
public class ExcellentCaseListParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;
}