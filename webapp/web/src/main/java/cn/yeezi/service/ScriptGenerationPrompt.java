package cn.yeezi.service;

import lombok.Builder;
import lombok.Getter;

/**
 * 脚本生成提示内容
 *
 * @author codex
 * @since 2026-03-12
 */
@Getter
@Builder
public class ScriptGenerationPrompt {

    private final String systemPrompt;

    private final String userContent;

    private final String promptSnapshot;
}
