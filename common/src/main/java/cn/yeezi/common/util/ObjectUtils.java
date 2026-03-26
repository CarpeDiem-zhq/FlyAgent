package cn.yeezi.common.util;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.yeezi.common.exception.BusinessException;
import cn.yeezi.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wanghh
 * @since 2022-07-07
 */
@Slf4j
public class ObjectUtils {

    /**
     * 查询结果集合转换，entity转VO
     */
    public static <DO, VO> IPage<VO> entityToVO(IPage<DO> page, Function<DO, VO> function) {
        IPage<VO> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        if (page.getTotal() > 0) {
            List<VO> voList = page.getRecords().stream().map(functionWrapper(function)).collect(Collectors.toList());
            voPage.setRecords(voList);
        }
        return voPage;
    }

    private static <DO, VO, E extends Exception> Function<DO, VO> functionWrapper(Function<DO, VO> function) {
        return DO -> {
            try {
                return function.apply(DO);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new BusinessException(ResultCodeEnum.ERROR);
            }
        };
    }
}
