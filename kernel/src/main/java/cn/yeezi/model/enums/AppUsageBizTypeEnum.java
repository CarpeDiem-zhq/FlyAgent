package cn.yeezi.model.enums;

import java.util.Arrays;
import java.util.Objects;
import lombok.AllArgsConstructor;

/**
 * 应用使用埋点业务类型
 *
 * @author codex
 * @since 2026-03-23
 */
@AllArgsConstructor
public enum AppUsageBizTypeEnum {
    TTS("TTS", "TTS 使用"),
    //
    ;

    public final String code;
    public final String text;

    public static AppUsageBizTypeEnum fromCode(String code) {
        return Arrays.stream(values())
                .filter(item -> Objects.equals(item.code, code))
                .findFirst()
                .orElse(null);
    }
}
