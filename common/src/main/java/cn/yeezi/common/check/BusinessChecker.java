package cn.yeezi.common.check;

import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * @author wanghh
 * @since 2022-07-25
 */
@Slf4j
public class BusinessChecker {

    /**
     * 检查品牌隔离性
     */
    public static void checkIsolation(Long currentId, Long sourceId) {
        if (!Objects.equals(currentId, sourceId)) {
            String message = String.format("Inconsistent,currentId:%d,sourceId:%d", currentId, sourceId);
            log.error(message);
            throw new BusinessException(ResultCodeEnum.ILLEGAL_OPERATION);
        }
    }
}
