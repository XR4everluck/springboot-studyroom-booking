package com.yourname.studyroom.common.utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 营业时间校验工具类。
 * 营业时间格式约定为 "HH:mm-HH:mm"，如 "08:00-22:00"。
 */
public class BusinessHoursUtil {

    private static final DateTimeFormatter HH_MM = DateTimeFormatter.ofPattern("HH:mm");

    private BusinessHoursUtil() {
    }

    /**
     * 解析营业时间字符串，非法或为空返回 null。
     */
    public static LocalTime[] parse(String businessHours) {
        if (businessHours == null || businessHours.trim().isEmpty()) {
            return null;
        }
        String[] parts = businessHours.trim().split("-");
        if (parts.length != 2) {
            return null;
        }
        try {
            LocalTime open = LocalTime.parse(parts[0].trim(), HH_MM);
            LocalTime close = LocalTime.parse(parts[1].trim(), HH_MM);
            return new LocalTime[]{open, close};
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断给定时间的时分是否落在营业区间 [open, close] 内。
     * 无法解析营业时间时返回 true（视为不限制），由调用方决定是否放行。
     */
    public static boolean isWithin(String businessHours, Date time) {
        if (time == null) {
            return false;
        }
        LocalTime[] range = parse(businessHours);
        if (range == null) {
            return true;
        }
        LocalTime actual = toLocalTime(time);
        LocalTime open = range[0];
        LocalTime close = range[1];
        if (close.isBefore(open)) {
            // 跨天营业，如 00:00-06:00
            return !actual.isBefore(open) || !actual.isAfter(close);
        }
        return !actual.isBefore(open) && !actual.isAfter(close);
    }

    /**
     * 判断预订起止时间是否整体落在同一个营业日区间内。
     * 当起止时间不含具体时分（即为 00:00:00 的纯日期，按天预订）时，视为整天预订、不做时段限制，直接放行。
     */
    public static boolean isRangeWithin(String businessHours, Date start, Date end) {
        boolean startHasTime = hasTimeOfDay(start);
        boolean endHasTime = hasTimeOfDay(end);
        // 纯日期（00:00:00）表示按天预订，无需按营业时间卡点
        if (!startHasTime && !endHasTime) {
            return true;
        }
        boolean startOk = !startHasTime || isWithin(businessHours, start);
        boolean endOk = !endHasTime || isWithin(businessHours, end);
        return startOk && endOk;
    }

    /**
     * 判断时间是否携带具体的时分（非 00:00）。
     */
    public static boolean hasTimeOfDay(Date date) {
        if (date == null) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY) != 0
                || calendar.get(Calendar.MINUTE) != 0
                || calendar.get(Calendar.SECOND) != 0;
    }

    private static LocalTime toLocalTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return LocalTime.of(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE));
    }
}
