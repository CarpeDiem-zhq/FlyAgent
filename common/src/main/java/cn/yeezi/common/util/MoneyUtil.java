package cn.yeezi.common.util;

import cn.hutool.core.util.StrUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MoneyUtil {
    /**
     * long 转 BigDecimal，long 的单位是厘
     * @param amountInLi long 类型，单位：厘
     * @return BigDecimal，单位：元
     */
    public static BigDecimal liToYuan(long amountInLi) {
        return BigDecimal.valueOf(amountInLi)
                         .divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
    }

    /**
     * 元转厘
     * @param yuan 金额字符串，例如 "12.34"
     * @return 厘，long 类型
     */
    public static long yuanToLi(String yuan) {
        if (yuan == null || yuan.isEmpty()) {
            throw new IllegalArgumentException("金额不能为空");
        }
        // 使用 BigDecimal 避免浮点误差
        BigDecimal amount = new BigDecimal(yuan);
        BigDecimal li = amount.multiply(BigDecimal.valueOf(1000));
        return li.longValueExact(); // 精确转换，如果有小数部分会抛异常
    }

    /**
     * 字符串转换为BigDecimal，自动去除逗号
     * 
     * @param amountStr 金额字符串，如 "1,000.50" 或 "1000.50"
     * @return BigDecimal 金额
     * @throws IllegalArgumentException 如果转换失败
     */
    public static BigDecimal stringToBigDecimal(String amountStr) {
        if (StrUtil.isBlank(amountStr)) {
            throw new IllegalArgumentException("金额字符串不能为空");
        }
        
        try {
            // 清理逗号和空格
            String cleanAmount = amountStr.trim().replace(",", "");
            return new BigDecimal(cleanAmount);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("无法转换金额字符串: " + amountStr + "，请输入有效的数字格式", e);
        }
    }

    /**
     * 相加多个BigDecimal并按指定小数位进行四舍五入
     * 注意：在计算过程中不进行四舍五入，只在最终结果上进行四舍五入
     *
     * @param scale 小数位数，必须大于等于0
     * @param amounts 要相加的BigDecimal数组
     * @return 相加后的结果，按指定小数位四舍五入
     * @throws IllegalArgumentException 如果参数不合法
     */
    public static BigDecimal addWithRounding(int scale, BigDecimal... amounts) {
        if (scale < 0) {
            throw new IllegalArgumentException("小数位数不能为负数");
        }
        if (amounts == null || amounts.length == 0) {
            return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP);
        }

        // 相加过程中不进行四舍五入，保持精确计算
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal amount : amounts) {
            if (amount != null) {
                sum = sum.add(amount);
            }
        }

        // 最终结果进行四舍五入
        return sum.setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 相加多个BigDecimal列表并按指定小数位进行四舍五入
     * 注意：在计算过程中不进行四舍五入，只在最终结果上进行四舍五入
     *
     * @param scale 小数位数，必须大于等于0
     * @param amounts 要相加的BigDecimal列表
     * @return 相加后的结果，按指定小数位四舍五入
     * @throws IllegalArgumentException 如果参数不合法
     */
    public static BigDecimal addWithRounding(int scale, List<BigDecimal> amounts) {
        if (scale < 0) {
            throw new IllegalArgumentException("小数位数不能为负数");
        }
        if (amounts == null || amounts.isEmpty()) {
            return BigDecimal.ZERO.setScale(scale, RoundingMode.HALF_UP);
        }

        // 相加过程中不进行四舍五入，保持精确计算
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal amount : amounts) {
            if (amount != null) {
                sum = sum.add(amount);
            }
        }

        // 最终结果进行四舍五入
        return sum.setScale(scale, RoundingMode.HALF_UP);
    }

}
