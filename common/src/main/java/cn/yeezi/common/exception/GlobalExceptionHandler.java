package cn.yeezi.common.exception;

import cn.yeezi.common.result.NoData;
import cn.yeezi.common.result.ResultCodeEnum;
import cn.yeezi.common.result.ResultVO;
import cn.yeezi.common.util.EnvUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 鍏ㄥ眬寮傚父澶勭悊鍣? *
 * @author wanghh
 * @since 2021-05-11
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 鍙傛暟寮傚父澶勭悊
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResultVO<NoData> badRequest(MethodArgumentNotValidException exception) {
        FieldError fieldError = exception.getBindingResult().getFieldError();
        assert fieldError != null;
        String message = fieldError.getDefaultMessage();
        log.warn(message, exception);
        return ResultVO.fail(ResultCodeEnum.PARAM_ERROR, message);
    }

    /**
     * 涓氬姟寮傚父澶勭悊
     */
    @ExceptionHandler(value = BusinessException.class)
    public <T> ResultVO<T> businessExceptionHandler(BusinessException exception) {
        String message = exception.getMessage();
        log.error(message, exception);
        return ResultVO.fail(exception.getResultCode(), exception.getMessage());
    }

    /**
     * 鏈煡寮傚父澶勭悊
     */
    @ExceptionHandler(value = Exception.class)
    public <T> ResultVO<T> UnknownExceptionHandler(Exception exception) {
        String message = exception.getMessage();
        log.error(message, exception);
        if (EnvUtil.isTest() || EnvUtil.isDev()) {
            return ResultVO.fail(ResultCodeEnum.ERROR, message);
        }
        return ResultVO.fail(ResultCodeEnum.ERROR);
    }
}