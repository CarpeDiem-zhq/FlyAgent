package cn.yeezi.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 百分数与小数转换工具类
 * @author luoguoliang
 * @since 2025-08-26
 */
public class PercentUtils {

    /**
     * 百分数转换为小数
     * @param percent 百分数值（例如：25.5表示25.5%）
     * @return 小数（例如：0.255）
     */
    public static BigDecimal percentToDecimal(BigDecimal percent) {
        if (percent == null) {
            return null;
        }
        return percent.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
    }

    /**
     * 百分数转换为小数（保留指定小数位数）
     * @param percent 百分数值
     * @param scale 保留的小数位数
     * @return 保留指定位数的小数
     */
    public static BigDecimal percentToDecimal(BigDecimal percent, int scale) {
        if (percent == null) {
            return null;
        }
        return percent.divide(BigDecimal.valueOf(100), scale, RoundingMode.HALF_UP);
    }

    /**
     * 小数转换为百分数
     * @param decimal 小数值（例如：0.255）
     * @return 百分数值（例如：25.5）
     */
    public static BigDecimal decimalToPercent(BigDecimal decimal) {
        if (decimal == null) {
            return null;
        }
        return decimal.multiply(BigDecimal.valueOf(100)).setScale(10, RoundingMode.HALF_UP);
    }

    /**
     * 小数转换为百分数（保留指定小数位数）
     * @param decimal 小数值
     * @param scale 保留的小数位数
     * @return 保留指定位数的百分数值
     */
    public static BigDecimal decimalToPercent(BigDecimal decimal, int scale) {
        if (decimal == null) {
            return null;
        }
        return decimal.multiply(BigDecimal.valueOf(100)).setScale(scale, RoundingMode.HALF_UP);
    }

    /**
     * 字符串形式的百分数转换为小数
     * @param percentStr 百分数字符串（例如："25.5%"或"25.5"）
     * @return 小数
     */
    public static BigDecimal percentStrToDecimal(String percentStr) {
        if (percentStr == null || percentStr.trim().isEmpty()) {
            return null;
        }
        // 移除百分号
        String cleanStr = percentStr.replace("%", "").trim();
        try {
            BigDecimal percent = new BigDecimal(cleanStr);
            return percentToDecimal(percent);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 小数转换为字符串形式的百分数
     * @param decimal 小数值
     * @return 百分数字符串（例如："25.5%"）
     */
    public static String decimalToPercentStr(BigDecimal decimal) {
        if (decimal == null) {
            return null;
        }
        BigDecimal percent = decimalToPercent(decimal);
        return percent.stripTrailingZeros().toPlainString() + "%";
    }

    /**
     * 小数转换为字符串形式的百分数（保留指定小数位数）
     * @param decimal 小数值
     * @param scale 保留的小数位数
     * @return 保留指定位数的百分数字符串
     */
    public static String decimalToPercentStr(BigDecimal decimal, int scale) {
        if (decimal == null) {
            return null;
        }
        BigDecimal percent = decimalToPercent(decimal, scale);
        return percent.stripTrailingZeros().toPlainString() + "%";
    }
}