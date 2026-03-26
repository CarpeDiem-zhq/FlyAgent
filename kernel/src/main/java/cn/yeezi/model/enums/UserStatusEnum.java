package cn.yeezi.model.enums;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author wanghh
 * @since 2023-08-09
 */
@AllArgsConstructor
public enum UserStatusEnum {
    ENABLE(1, "正常"),
    DISABLE(2, "禁用"),
    //
    ;

    public final int value;
    public final String text;

    public static UserStatusEnum valueOf(Integer value) {
        return Arrays.stream(values()).filter(e -> Objects.equals(e.value, value)).findFirst().orElse(null);
    }
}
