package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 应用使用统计参数
 *
 * @author codex
 * @since 2026-03-23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "应用使用统计参数")
public class AppUsageStatsParam {

    @NotBlank(message = "应用类型不能为空")
    @Schema(description = "应用类型编码，例如 TTS")
    private String appCode;
}
