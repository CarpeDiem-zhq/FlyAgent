package cn.yeezi.common.generator;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author wanghh
 * @since 2022-11-14
 */
@AllArgsConstructor
public enum FileTypeEnum {
    ENTITY_GETTER("Getter.java"),
    CORE(".java"),
    SERVICE("Service.java"),
    PARAM_PAGE("PageParam.java"),
    PARAM_ADD("AddParam.java"),
    PARAM_EDIT("EditParam.java"),
    VO("VO.java"),
    //
    ;

    public final String suffix;


    public static FileTypeEnum suffixOf(String value) {
        return Arrays.stream(values()).filter(e -> Objects.equals(e.suffix, value)).findFirst().orElse(null);
    }
}
