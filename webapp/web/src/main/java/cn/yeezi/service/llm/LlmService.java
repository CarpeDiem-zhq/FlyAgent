package cn.yeezi.service.llm;

import cn.yeezi.ai.AiChatGateway;
import cn.yeezi.ai.AiSceneType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 统一大模型调用入口
 *
 * @author codex
 * @since 2026-03-18
 */
@Service
@RequiredArgsConstructor
public class LlmService {

    private final AiChatGateway aiChatGateway;

    public LlmChatResponse chatCompletion(AiSceneType sceneType, String systemPrompt, String userContent) {
        return aiChatGateway.chatCompletion(sceneType, systemPrompt, userContent);
    }

    public String getCurrentModel(AiSceneType sceneType) {
        return aiChatGateway.getCurrentModel(sceneType);
    }
}
