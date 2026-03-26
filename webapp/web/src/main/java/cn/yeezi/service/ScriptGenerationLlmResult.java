package cn.yeezi.service;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 脚本生成模型结果
 *
 * @author codex
 * @since 2026-03-12
 */
@Getter
@Builder
public class ScriptGenerationLlmResult {

    private final String title;

    private final String script;

    private final String summary;

    private final String scene;

    private final String targetUser;

    private final String emotion;

    private final String tone;

    private final List<String> sellingPoints;

    private final ScriptStructure structure;

    private final List<String> tags;
}
