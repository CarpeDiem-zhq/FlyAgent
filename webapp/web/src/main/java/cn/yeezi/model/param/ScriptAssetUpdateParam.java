package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本资产更新参数
 *
 * @author codex
 * @since 2026-01-22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "脚本资产更新参数")
public class ScriptAssetUpdateParam {

    @NotNull(message = "资产id不能为空")
    @Schema(description = "资产id")
    private Long assetId;

    @Schema(description = "提示词id")
    private Long promptId;

    @Schema(description = "提示词快照")
    private String promptSnapshot;

    @Schema(description = "规则快照")
    private String ruleSnapshot;

    @Schema(description = "输入快照")
    private String inputSnapshot;

    @Schema(description = "标签快照")
    private String tagSnapshot;

    @Schema(description = "用户输入快照")
    private String userInputSnapshot;

    @Schema(description = "案例快照")
    private String caseSnapshot;

    @Schema(description = "模型名称")
    private String modelName;

    @Schema(description = "路由策略")
    private String routeStrategy;

    @NotBlank(message = "生成内容不能为空")
    @Schema(description = "生成内容")
    private String outputContent;
}
