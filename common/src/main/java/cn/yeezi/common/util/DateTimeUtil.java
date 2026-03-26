package cn.yeezi.common.util;

import cn.yeezi.common.exception.BusinessException;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Locale;

/**
 * @author wanghh
 * @since 2022-11-17
 */
public class DateTimeUtil {

    public static Long toTimestamp(LocalDateTime dateTime) {
        return dateTime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * mm:ss 转 秒
     */
    public static int toSeconds(String time) {
        String[] array = time.split(":");
        int hours = Integer.parseInt(array[0]);
        return (60 * 60 * hours) + (Integer.parseInt(array[1]) * 60);
    }


    /**
     * 秒 转 mm:ss
     */
    public static String parseTime(int seconds) {
        int minute = (seconds % (60 * 60)) / 60;
        int hour = seconds / (60 * 60);
        String strHour = String.valueOf(hour);
        String strMinute = String.valueOf(minute);
        if (hour == 0) {
            strHour += "0";
        }
        if (minute == 0) {
            strMinute += "0";
        }
        return strHour + ":" + strMinute;
    }

    /**
     * 获取时间戳（秒）
     */
    public static Long getSecondsTimestamp(LocalDateTime dateTime) {
        return dateTime.toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 获取时间戳（秒）
     */
    public static Long getSecondsTimestamp(LocalDate date) {
        return date.atStartOfDay().toInstant(ZoneOffset.of("+8")).toEpochMilli() / 1000;
    }

    /**
     * 时间戳（秒）to LocalDateTime
     */
    public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp * 1000);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }

    /**
     * 获取当天最大时间（秒）
     */
    public static LocalDateTime getStartOfDay(LocalDateTime dateTime) {
        return dateTime.with(LocalTime.MIN.withNano(0));
    }

    /**
     * 获取当天最大时间（秒）
     */
    public static LocalDateTime getEndOfDay(LocalDateTime dateTime) {
        return dateTime.with(LocalTime.MAX.withNano(0));
    }

    /**
     * 获取两天间隔的天数(以日为单位计算 忽略时分秒)
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 相隔天数(int)
     */
    public static int getDaysDifference(LocalDateTime startDate, LocalDateTime endDate) {
        return (int) ChronoUnit.DAYS.between(startDate.toLocalDate(), endDate.toLocalDate()) + 1;
    }


    public static int getDaysDifference(LocalDate startDate, LocalDate endDate) {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }

    /**
     * 获取当月第一天
     */
    public static LocalDateTime getFirstOfMonth(LocalDateTime localDateTime) {
        LocalDate date = localDateTime.toLocalDate();
        return date.with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
    }

    /**
     * 获取当月最后一天
     */
    public static LocalDateTime getLastOfMonth(LocalDateTime localDateTime) {
        LocalDate date = localDateTime.toLocalDate();
        return date.with(TemporalAdjusters.lastDayOfMonth()).atStartOfDay().with(LocalTime.MAX.withNano(0));
    }

    /**
     * 日期格式转换
     * 例：2024年06月19日 周三 17:30-18:30PM
     */
    public static String formatDateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日 EEEE", Locale.CHINA);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        // Format the start date and time
        String startDate = startTime.format(dateFormatter);
        String startTimeFormatted = startTime.format(timeFormatter);

        // Format the end time
        String endTimeFormatted = endTime.format(timeFormatter);

        // Determine AM/PM period
        String period = endTime.getHour() >= 12 ? "PM" : "AM";

        // Construct the final string
        return String.format("%s %s-%s%s", startDate, startTimeFormatted, endTimeFormatted, period);
    }

    /**
     * 获取本周开始时间
     */
    public static LocalDateTime getStartOfWeek(LocalDateTime now) {
        return now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .with(LocalTime.MIN.withNano(0));
    }

    /**
     * 获取本周结束时间
     */
    public static LocalDateTime getEndOfWeek(LocalDateTime now) {
        return now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))
                .with(LocalTime.MAX.withNano(0));
    }



    /**
     * 格式化日期范围（使用“ 至 ”连接符）
     * 格式：yyyy-MM-dd 至 yyyy-MM-dd
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 格式化后的日期范围字符串，如：2025-09-10 至 2026-11-11
     */
    public static String formatDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return "";
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return startDate.format(formatter) + " 至 " + endDate.format(formatter);
    }

    /**
     * 获取季度
     * 传入LocalDate，返回季度字符串（Q1、Q2、Q3、Q4）
     *
     * @param date 日期
     * @return 季度字符串，如：Q1、Q2、Q3、Q4
     */
    public static String getQuarter(LocalDate date) {
        if (date == null) {
            return null;
        }
        int month = date.getMonthValue();
        int quarter = (month - 1) / 3 + 1;
        return "Q" + quarter;
    }

    public static String getQuarter(LocalDateTime date) {
        if (date == null) {
            return null;
        }
        int month = date.getMonthValue();
        int quarter = (month - 1) / 3 + 1;
        return "Q" + quarter;
    }

    /**
     * 获取月度
     * 传入LocalDate，返回月度字符串（1月、2月、...、12月）
     *
     * @param date 日期
     * @return 月度字符串，如：1月、2月、...、12月
     */
    public static String getMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        
        int month = date.getMonthValue();
        return month + "月";
    }

    public static String getMonth(LocalDateTime date) {
        if (date == null) {
            return null;
        }

        int month = date.getMonthValue();
        return month + "月";
    }

    /**
     * 获取年
     * 年度字符串，如：2023年
     *
     * @param date 日期
     * @return 年度字符串，如：2023年
     */
    public static String getYear(LocalDate date) {
        if (date == null) {
            return null;
        }

        int year = date.getYear();
        return year + "年";
    }

    public static String getYear(LocalDateTime date) {
        if (date == null) {
            return null;
        }

        int year = date.getYear();
        return year + "年";
    }

    public static String getYearMonth(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.getYear() + "年" + date.getMonthValue() + "月";
    }

    /**
     * 是否是正确的时间格式
     */
    public static boolean isValidTime(String time) {
        try {
            parseLocalDate(time);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    
    /**
     * 将字符串转换为LocalDate，支持多种日期格式
     * 
     * @param dateStr 日期字符串
     * @return LocalDate对象
     * @throws BusinessException 如果转换失败则抛出异常
     */
    public static LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new BusinessException("日期字符串不能为空");
        }
        
        DateTimeFormatter[] dateFormatters = {
                DateTimeFormatter.ofPattern("yyyy/M/d"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),
                DateTimeFormatter.ofPattern("yyyy/M/dd"),
                DateTimeFormatter.ofPattern("yyyy/MM/d"),
                DateTimeFormatter.ofPattern("yyyy-M-d"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy-M-dd"),
                DateTimeFormatter.ofPattern("yyyy-MM-d")
        };
        
        String trimmedDateStr = dateStr.trim();
        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                return LocalDate.parse(trimmedDateStr, formatter);
            } catch (Exception ignored) {
                // 继续尝试下一个格式
            }
        }
        
        throw new BusinessException("无法解析日期字符串: " + dateStr);
    }
    
    /**
     * 将字符串转换为LocalDateTime，支持多种日期时间格式
     * 
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime对象
     * @throws BusinessException 如果转换失败则抛出异常
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            throw new BusinessException("日期时间字符串不能为空");
        }
        
        DateTimeFormatter[] dateTimeFormatters = {
                DateTimeFormatter.ofPattern("yyyy/M/d H:m:s"),
                DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy/M/dd H:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy/MM/d H:m:s"),
                DateTimeFormatter.ofPattern("yyyy-M-d H:m:s"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-M-dd H:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-d H:m:s"),
                DateTimeFormatter.ofPattern("yyyy/M/d HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-M-d HH:mm:ss")
        };
        
        String trimmedDateTimeStr = dateTimeStr.trim();
        for (DateTimeFormatter formatter : dateTimeFormatters) {
            try {
                return LocalDateTime.parse(trimmedDateTimeStr, formatter);
            } catch (Exception ignored) {
                // 继续尝试下一个格式
            }
        }
        
        throw new BusinessException("无法解析日期时间字符串: " + dateTimeStr);
    }
}
