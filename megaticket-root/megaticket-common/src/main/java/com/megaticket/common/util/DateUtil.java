package com.megaticket.common.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期时间工具类
 * author Yang JunJie
 * date 2026/1/12
 */
public class DateUtil {

    /** 标准日期时间格式 */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";
    /** 标准日期格式 */
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    /** 紧凑格式 (常用于生成订单号/流水号) */
    public static final String PATTERN_COMPACT = "yyyyMMddHHmmss";

    // formatter 是线程安全的，可以定义为 static final
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATETIME);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_DATE);
    private static final DateTimeFormatter COMPACT_FORMATTER = DateTimeFormatter.ofPattern(PATTERN_COMPACT);

    /**
     * 获取当前时间字符串 (yyyy-MM-dd HH:mm:ss)
     */
    public static String nowStr() {
        return LocalDateTime.now().format(DATETIME_FORMATTER);
    }

    /**
     * 获取当前日期字符串 (yyyy-MM-dd)
     */
    public static String todayStr() {
        return LocalDate.now().format(DATE_FORMATTER);
    }

    /**
     * 格式化时间
     * @param dateTime LocalDateTime对象
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMATTER) : null;
    }

    /**
     * 解析时间字符串
     * @param timeStr "2025-11-12 12:00:00"
     * @return LocalDateTime对象
     */
    public static LocalDateTime parse(String timeStr) {
        return LocalDateTime.parse(timeStr, DATETIME_FORMATTER);
    }


    /**
     * 计算订单过期时间
     * @param minutes 多少分钟后过期
     * @return 过期时间点
     */
    public static LocalDateTime getExpireTime(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    /**
     * 计算两个时间相差的秒数 (用于计算倒计时)
     * @param start 开始时间 (通常是 now)
     * @param end 结束时间 (比如 订单过期时间)
     * @return 相差秒数，如果 end < start 返回 0 或负数
     */
    public static long secondsBetween(LocalDateTime start, LocalDateTime end) {
        return ChronoUnit.SECONDS.between(start, end);
    }

    /**
     * 兼容旧代码：Date 转 LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 兼容旧代码：LocalDateTime 转 Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
