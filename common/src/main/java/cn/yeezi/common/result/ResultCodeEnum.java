package cn.yeezi.common.result;

import lombok.AllArgsConstructor;

/**
 * 默认code,范围值：1~9999
 *
 * @author wanghh
 * @since 2021-05-20
 */
@AllArgsConstructor
public enum ResultCodeEnum implements IResultCode {
    SUCCESS(200, "请求成功"),
    ERROR(500, "未知错误"),

    PARAM_ERROR(1001, "参数错误"),
    JSON_ERROR(1002, "参数错误"),
    NOT_FOUND(1003, "未找到"),
    ILLEGAL_OPERATION(1004, "非法操作"),
    NOT_IMPLEMENTED(1005, "代码未实现"),

    NOT_LOGIN(5000, "未登录"),
    //
    ;

    public final int value;
    public final String text;

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public String getText() {
        return text;
    }
}