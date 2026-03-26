package cn.yeezi.service;

import cn.yeezi.model.param.ControlParamsParam;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

/**
 * 脚本生成提示构造上下文
 *
 * @author codex
 * @since 2026-03-12
 */
@Getter
@Builder
public class ScriptGenerationPromptContext {

    private final Long productId;

    private final Long userId;

    private final String productName;

    private final Integer itemSeq;

    private final Integer batchSize;

    private final String systemPrompt;

    private final String ruleSnapshot;

    private final List<String> caseSnippets;

    private final Map<String, List<String>> tagValues;

    private final ControlParamsParam controlParams;

    private final OpeningStrategy openingStrategy;
}
