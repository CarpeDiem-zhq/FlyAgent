package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本回炉参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "脚本回炉参数")
public class ScriptRerunParam {

    @Schema(description = "资产id")
    private Long assetId;

    @NotNull(message = "提示词快照不能为空")
    @Schema(description = "提示词快照")
    private String promptSnapshot;

    @NotNull(message = "输入快照不能为空")
    @Schema(description = "输入快照")
    private String inputSnapshot;

    @NotNull(message = "当前脚本内容不能为空")
    @Schema(description = "当前脚本内容")
    private String outputContent;

    @NotNull(message = "补充指令不能为空")
    @Schema(description = "补充指令")
    private String additionalInstruction;
}
