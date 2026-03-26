package cn.yeezi.config;

import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class OkHttpConfig {
    @Bean
    public OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)  // 连接超时：30秒
                .readTimeout(300, TimeUnit.SECONDS)    // 读取超时：5分钟（Dify Workflow 可能需要较长时间）
                .writeTimeout(30, TimeUnit.SECONDS)    // 写入超时：30秒
                .build();
    }
}
