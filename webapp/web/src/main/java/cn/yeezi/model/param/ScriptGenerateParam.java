package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Schema(description = "用户id")
    private Long userId;

    @NotNull(message = "请选择提示词")
    @Schema(description = "提示词id")
    private Long promptId;

    @Schema(description = "优秀案例id")
    private Long excellentCaseId;

    @Min(value = 1, message = "广告脚本生成条数最小为1")
    @Max(value = 10, message = "广告脚本生成条数最大为10")
    @Schema(description = "广告脚本生成条数")
    private Integer adNumber;

    @Schema(description = "标签输入")
    private List<TagInputParam> tags;

    @Schema(description = "控制参数")
    private ControlParamsParam controlParams;
}
