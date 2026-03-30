package cn.yeezi.model.param;

import cn.yeezi.common.query.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 我的脚本资产分页参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "我的脚本资产分页参数")
public class ScriptAssetMyPageParam extends PageParam {

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "功能id")
    private Long featureId;
}
