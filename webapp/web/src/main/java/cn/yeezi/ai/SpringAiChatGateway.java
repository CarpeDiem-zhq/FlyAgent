package cn.yeezi.ai;

import cn.yeezi.ai.config.DeepSeekOptionsProperties;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.service.llm.LlmChatResponse;
import cn.yeezi.service.llm.LlmProviderType;
import java.util.List;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

/**
 * 基于 Spring AI 的统一对话门面
 *
 * @author codex
 * @since 2026-03-18
 */
@Service
public class SpringAiChatGateway implements AiChatGateway {

    private final DeepSeekOptionsProperties deepSeekOptionsProperties;
    private final ChatClient deepSeekChatClient;

    public SpringAiChatGateway(
            DeepSeekOptionsProperties deepSeekOptionsProperties,
            ChatClient deepSeekChatClient
    ) {
        this.deepSeekOptionsProperties = deepSeekOptionsProperties;
        this.deepSeekChatClient = deepSeekChatClient;
    }

    @Override
    public LlmChatResponse chatCompletion(AiSceneType sceneType, String systemPrompt, String userContent) {
        String content = executeDeepSeek(systemPrompt, userContent);
        if (content == null || content.isBlank()) {
            throw new BusinessException("大模型返回内容为空");
        }
        return LlmChatResponse.builder()
                .provider(LlmProviderType.DEEPSEEK)
                .model(getCurrentModel(sceneType))
                .rawResponseBody(null)
                .rawContent(content)
                .build();
    }

    @Override
    public LlmProviderType resolveProvider(AiSceneType sceneType) {
        return LlmProviderType.DEEPSEEK;
    }

    @Override
    public String getCurrentModel(AiSceneType sceneType) {
        return deepSeekOptionsProperties.getModel();
    }

    private String executeDeepSeek(String systemPrompt, String userContent) {
        Prompt prompt = new Prompt(List.of(
                new SystemMessage(systemPrompt),
                new UserMessage(userContent)
        ));
        return deepSeekChatClient.prompt(prompt)
                .call()
                .content();
    }
}
