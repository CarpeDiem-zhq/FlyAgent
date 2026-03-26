package cn.yeezi.common.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Configuration
public class ObjectMapperConfig {

    //全局统一使用斜杠格式
    private static final String DATE_TIME_PATTERN = "yyyy/MM/dd HH:mm:ss";
    private static final String DATE_PATTERN = "yyyy/MM/dd";
    private static final String TIME_PATTERN = "HH:mm:ss";

    // 兼容多格式输入（允许前端传“-”或“/”）
    private static final List<String> SUPPORTED_PATTERNS = List.of(
            "yyyy/MM/dd HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy/MM/dd",
            "yyyy-MM-dd"
    );

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // ---- 时间模块注册 ----
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // 序列化（输出统一格式）
        javaTimeModule.addSerializer(Date.class, new DateSerializer(false, new SimpleDateFormat(DATE_TIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DateTimeFormatter.ofPattern(DATE_TIME_PATTERN)));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_PATTERN)));
        javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));

        // 反序列化（支持多种输入）
        SimpleModule multiFormatModule = new SimpleModule();

        // LocalDate
        multiFormatModule.addDeserializer(LocalDate.class, new JsonDeserializer<>() {
            @Override
            public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getText().trim();
                for (String pattern : SUPPORTED_PATTERNS) {
                    try {
                        return LocalDate.parse(value, DateTimeFormatter.ofPattern(pattern));
                    } catch (Exception ignored) {}
                }
                throw new IllegalArgumentException("无法解析 LocalDate 格式: " + value);
            }
        });

        // LocalDateTime
        multiFormatModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String value = p.getText().trim();
                for (String pattern : SUPPORTED_PATTERNS) {
                    try {
                        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(pattern));
                    } catch (Exception ignored) {}
                }
                throw new IllegalArgumentException("无法解析 LocalDateTime 格式: " + value);
            }
        });

        // LocalTime
        javaTimeModule.addDeserializer(LocalTime.class,
                new com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer(DateTimeFormatter.ofPattern(TIME_PATTERN)));

        // Date 类型
        javaTimeModule.addDeserializer(Date.class,
                new DateDeserializers.DateDeserializer(DateDeserializers.DateDeserializer.instance,
                        new SimpleDateFormat(DATE_TIME_PATTERN), DATE_TIME_PATTERN));

        // 注册模块
        objectMapper.registerModule(javaTimeModule);
        objectMapper.registerModule(multiFormatModule);

        // ---- 通用配置 ----
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        //时区（建议保持东八区）
        objectMapper.setTimeZone(java.util.TimeZone.getTimeZone("Asia/Shanghai"));

        return objectMapper;
    }
}
