package cn.yeezi.config;

import cn.dev33.satoken.exception.NotLoginException;
import cn.yeezi.common.result.ResultCodeEnum;
import cn.yeezi.common.result.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 鍏ㄥ眬寮傚父澶勭悊鍣?
 *
 * @author wanghh
 * @since 2021-05-11
 */
@RestControllerAdvice
@Slf4j
public class WebExceptionHandler {

    /**
     * Sa-token鏈櫥褰曞紓甯稿鐞?
     */
    @org.springframework.web.bind.annotation.ExceptionHandler(value = NotLoginException.class)
    public <T> ResultVO<T> notLoginExceptionHandler() {
        return ResultVO.fail(ResultCodeEnum.NOT_LOGIN);
    }
}
