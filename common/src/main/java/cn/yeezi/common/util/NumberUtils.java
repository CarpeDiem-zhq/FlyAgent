package cn.yeezi.common.util;

import cn.hutool.core.collection.CollUtil;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

/**
 * @author wanghh
 * @since 2022-07-05
 */
public class NumberUtils {


    public static Boolean isGreaterThanZero(Long value) {
        if (value == null) {
            return false;
        }
        return value > 0;
    }

    public static Boolean isGreaterThanZero(Integer value) {
        if (value == null) {
            return false;
        }
        return value > 0;
    }

    public static String parseNumber(BigDecimal number) {
        if (number == null) {
            return null;
        }
        return number.stripTrailingZeros().toPlainString();
    }

    /**
     * 计算中位数
     *
     * @param list 需要计算的集合
     * @return 中位数
     */
    public static Integer calculateMedian(List<Integer> list) {
        if (CollUtil.isEmpty(list)) {
            return null;
        }
        //从小到大排序
        Integer median = null;
        list.sort(Comparator.comparingInt(Integer::intValue));
        int size = list.size();
        int mid = size / 2;
        if (size % 2 == 0) {//偶数
            median = (list.get(mid) + list.get(mid - 1)) / 2;
        } else {//奇数
            median = list.get(mid);
        }
        return median;
    }

    public static BigDecimal safe(BigDecimal bd) {
        return bd == null ? BigDecimal.ZERO : bd;
    }
}
