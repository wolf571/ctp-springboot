package ctp.thostmduserapi.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * @author duxl
 * @remark 时间日期帮助类，常用方法另参考apache DateUtils。
 */
public final class DateHelper {

    /**
     * 日期常用格式
     */
    static String[] datePatterns = {"yyyy-MM-dd", "yyyyMMdd"};
    /**
     * 日期时间常用格式
     */
    static String[] dateTimePatterns = {"yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss"};

    /**
     * 获取当前日期
     *
     * @return yyyy-MM-dd
     */
    public static String getDateStr() {
        LocalDate localDate = LocalDate.now();
        return localDate.toString();
    }


    /**
     * 获取当前日期时间
     *
     * @return yyyyMMddHHmmss
     */
    public static String getDateTimeStr() {
//        LocalDateTime localDateTime = LocalDateTime.now();
//        localDateTime.toString();
        return getDateTimeStr(dateTimePatterns[0]);
    }

    /**
     * 获取当前日期时间
     *
     * @param pattern - 日期时间格式
     * @return 指定格式的日期时间
     */
    public static String getDateTimeStr(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime datetime = LocalDateTime.now();

        return datetime.format(formatter);
    }

    /**
     * 获取当前日期整数形式
     *
     * @return yyyyMMdd
     */
    public static int getDateInt() {
        String date = getDateTimeStr(datePatterns[1]);

        return Integer.parseInt(date);
    }

    /**
     * 获取当前日期long形式
     *
     * @return yyyyMMddHHmm
     */
    public static long getDateLong(String pattern) {
        String date = getDateTimeStr(pattern);

        return Long.parseLong(date);
    }

    /**
     * 获取给定日期long形式
     *
     * @return yyyyMMddHHmm
     */
    public static long getDateLong(long mills, String pattern) {
        LocalDateTime date = Instant.ofEpochMilli(mills).atZone(ZoneOffset.systemDefault()).toLocalDateTime();
        String dateStr = getDateTimeStr(pattern);

        return Long.parseLong(dateStr);
    }

    /**
     * 转换Str为LocalDate
     *
     * @param dateStr     日期字符串
     * @param datePattern 日期格式
     * @return 日期
     */
    public static LocalDate parseDate(String dateStr, String datePattern) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(datePattern);
        LocalDate date = LocalDate.parse(dateStr, df);

        return date;
    }

    /**
     * 转换Str为LocalTime
     *
     * @param timeStr 日期时间字符串
     * @return 日期
     */
    public static LocalTime parseTime(String timeStr) {
        LocalTime dateTime = LocalTime.parse(timeStr);

        return dateTime;
    }


    /**
     * 转换Str为DateTme
     *
     * @param datetimeStr 日期时间字符串
     * @return 日期
     */
    public static LocalDateTime parseDateTime(String datetimeStr, String dateTimePatterns) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(dateTimePatterns);
        LocalDateTime dateTime = LocalDateTime.parse(datetimeStr, df);

        return dateTime;
    }

    /**
     * 获取时间差天数
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static long getDiffDays(LocalDate startDate, LocalDate endDate) {
        long days = endDate.toEpochDay() - startDate.toEpochDay();

        return days;
    }

    /**
     * 获取到当前时间的差秒
     *
     * @param dateTime
     * @return
     */
    public static Long getDiffSeconds(LocalDateTime dateTime) {
        Duration duration = Duration.between(LocalDateTime.now(), dateTime);

        return duration.toMillis();
    }

    /**
     * 获取到当前时间的差秒
     *
     * @param time
     * @return
     */
    public static Long getDiffSeconds(LocalTime time) {
        Duration duration = Duration.between(LocalTime.now(), time);

        return duration.toMillis();
    }

    /**
     * 获取时间差秒
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static Long getDiffSeconds(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Duration duration = Duration.between(startDateTime, endDateTime);

        return duration.toMillis();
    }
}
