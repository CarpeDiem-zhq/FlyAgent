package cn.yeezi.model.param;

import cn.yeezi.model.dto.OpenClawScriptDraftDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "OpenClaw 解析请求")
public class OpenClawScriptResolveParam {

    @NotBlank(message = "sessionKey不能为空")
    @Schema(description = "会话key")
    private String sessionKey;

    @Schema(description = "渠道")
    private String channel;

    @Schema(description = "用户id")
    private Long userId;

    @NotBlank(message = "消息不能为空")
    @Schema(description = "用户消息")
    private String messageText;

    @Schema(description = "当前草稿")
    private OpenClawScriptDraftDTO currentDraft;
}
