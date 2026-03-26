package cn.yeezi.common.exception;

import cn.yeezi.common.result.IResultCode;
import cn.yeezi.common.result.ResultCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 涓氬姟寮傚父
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    protected IResultCode resultCode = ResultCodeEnum.ERROR;

    private Object data;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(IResultCode resultCode) {
        this(resultCode.getText());
        this.resultCode = resultCode;
    }

    public BusinessException(IResultCode resultCode, String message) {
        this(message);
        this.resultCode = resultCode;
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }
}
