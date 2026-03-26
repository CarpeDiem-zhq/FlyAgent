package cn.yeezi.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * DeepSeek 对话默认配置
 *
 * @author codex
 * @since 2026-03-18
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "spring.ai.deepseek.chat.options")
public class DeepSeekOptionsProperties {

    private String model = "deepseek-chat";

    private Double temperature = 1.0D;

    private Integer maxTokens = 4096;
}
