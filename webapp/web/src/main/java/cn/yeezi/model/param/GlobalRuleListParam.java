package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局规则列表参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "全局规则列表参数")
public class GlobalRuleListParam {

    @Schema(description = "关键词")
    private String keyword;
}
