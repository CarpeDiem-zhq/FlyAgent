package cn.yeezi.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 脚本生成策略配置
 *
 * @author codex
 * @since 2026-03-12
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "script.generation")
public class ScriptGenerationStrategyProperties {

    private List<OpeningStrategyItem> openingStrategies = new ArrayList<>();

    @Getter
    @Setter
    public static class OpeningStrategyItem {

        private String code;

        private String name;

        private String instruction;
    }
}
