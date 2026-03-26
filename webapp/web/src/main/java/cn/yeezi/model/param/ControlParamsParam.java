package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 控制参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "控制参数")
public class ControlParamsParam {

    @Schema(description = "广告字数限制")
    private String ad_words;

    @Schema(description = "关键场景描述")
    private String key_scenes;
}
