package cn.yeezi.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 返回结果
 *
 * @author wanghh
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultVO<T> implements Serializable {

    private boolean success;

    private Integer code;

    private String msg;

    private T data;

    public static <T> ResultVO<T> success(T data) {
        return new ResultVO<>(true, ResultCodeEnum.SUCCESS.value, ResultCodeEnum.SUCCESS.text, data);
    }

    public static <T> ResultVO<T> success() {
        return success(null);
    }

    public static <T> ResultVO<T> fail(IResultCode code) {
        return new ResultVO<>(false, code.getValue(), code.getText(), null);
    }

    public static <T> ResultVO<T> fail(IResultCode code, String message) {
        return new ResultVO<>(false, code.getValue(), message, null);
    }

    public static <T> ResultVO<T> fail(String error) {
        return new ResultVO<>(false, ResultCodeEnum.ERROR.value, error, null);
    }
}
