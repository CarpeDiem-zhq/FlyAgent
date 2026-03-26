package cn.yeezi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 脚本生成线程池配置
 *
 * @author codex
 * @since 2026-01-27
 */
@Configuration
public class ScriptGenerateExecutorConfig {

    @Bean("scriptGenerateExecutor")
    public ThreadPoolTaskExecutor scriptGenerateExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("script-generate-");
        executor.initialize();
        return executor;
    }
}
