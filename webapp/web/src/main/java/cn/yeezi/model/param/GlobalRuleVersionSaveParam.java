package cn.yeezi.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 全局规则版本保存参数
 *
 * @author codex
 * @since 2026-01-12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "全局规则版本保存参数")
public class GlobalRuleVersionSaveParam {

    @NotEmpty(message = "规则内容不能为空")
    @Schema(description = "规则内容列表")
    private List<String> rules;
}
