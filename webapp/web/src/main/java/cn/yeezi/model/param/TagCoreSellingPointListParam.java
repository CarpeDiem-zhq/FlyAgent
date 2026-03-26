package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 核心卖点和优势查询参数
 *
 * @author codex
 * @since 2026-02-26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "核心卖点和优势查询参数")
public class TagCoreSellingPointListParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @NotNull(message = "产品功能标签项id不能为空")
    @Schema(description = "产品功能标签项id")
    private Long featureItemId;
}
