package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 脚本反馈参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "脚本反馈参数")
public class ScriptFeedbackParam {

    @NotNull(message = "资产id不能为空")
    @Schema(description = "资产id")
    private Long assetId;

    @NotNull(message = "满意度不能为空")
    @Schema(description = "是否满意")
    private Boolean satisfied;

    @Schema(description = "原因编码列表")
    private List<String> reasonCodes;

    @Schema(description = "建议")
    private String suggestion;

    @NotNull(message = "是否回炉不能为空")
    @Schema(description = "是否回炉")
    private Boolean rerun;
}