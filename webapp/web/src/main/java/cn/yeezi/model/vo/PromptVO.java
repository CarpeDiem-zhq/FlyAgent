package cn.yeezi.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 提示词信息
 *
 * @author codex
 * @since 2026-01-13
 */
@Data
@Schema(description = "提示词信息")
public class PromptVO {

    @Schema(description = "主键")
    private Long id;

    @Schema(description = "产品id")
    private Long productId;

    @Schema(description = "提示词名称")
    private String promptName;

    @Schema(description = "系统提示词")
    private String systemPrompt;

    @Schema(description = "是否启用")
    private Boolean enabled;
}