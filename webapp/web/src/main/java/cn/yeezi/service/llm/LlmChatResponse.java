package cn.yeezi.service.llm;

import lombok.Builder;
import lombok.Getter;

/**
 * 统一大模型对话响应
 *
 * @author codex
 * @since 2026-03-12
 */
@Getter
@Builder
public class LlmChatResponse {

    private final LlmProviderType provider;

    private final String model;

    private final String rawResponseBody;

    private final String rawContent;
}
