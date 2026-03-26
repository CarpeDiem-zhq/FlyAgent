package cn.yeezi.ai;

import cn.yeezi.service.llm.LlmChatResponse;
import cn.yeezi.service.llm.LlmProviderType;

/**
 * 统一 AI 对话门面
 *
 * @author codex
 * @since 2026-03-18
 */
public interface AiChatGateway {

    LlmChatResponse chatCompletion(AiSceneType sceneType, String systemPrompt, String userContent);

    LlmProviderType resolveProvider(AiSceneType sceneType);

    String getCurrentModel(AiSceneType sceneType);
}
