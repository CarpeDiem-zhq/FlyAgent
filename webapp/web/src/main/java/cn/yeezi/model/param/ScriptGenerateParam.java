package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本生成参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "脚本生成参数")
public class ScriptGenerateParam {

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

    @NotNull(message = "策略id不能为空")
    @Schema(description = "策略id")
    private Long strategyId;
}
