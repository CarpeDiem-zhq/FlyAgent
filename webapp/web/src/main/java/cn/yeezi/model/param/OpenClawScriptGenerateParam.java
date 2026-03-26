package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "OpenClaw 生成请求")
public class OpenClawScriptGenerateParam {

    @NotBlank(message = "traceId不能为空")
    @Schema(description = "追踪id")
    private String traceId;

    @NotBlank(message = "sessionKey不能为空")
    @Schema(description = "会话key")
    private String sessionKey;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "用户id")
    private Long userId;

    @NotNull(message = "产品id不能为空")
    @Schema(description = "产品id")
    private Long productId;

    @NotNull(message = "功能id不能为空")
    @Schema(description = "功能id")
    private Long featureId;

    @NotNull(message = "核心卖点id不能为空")
    @Schema(description = "核心卖点id")
    private Long coreSellingPointId;

    @NotNull(message = "策略id不能为空")
    @Schema(description = "策略id")
    private Long strategyId;

    @Schema(description = "生成条数")
    private Integer adNumber;
}
