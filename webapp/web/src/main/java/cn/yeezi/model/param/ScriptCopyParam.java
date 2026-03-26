package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本复制参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "脚本复制参数")
public class ScriptCopyParam {

    @NotNull(message = "资产id不能为空")
    @Schema(description = "资产id")
    private Long assetId;
}