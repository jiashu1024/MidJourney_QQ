package com.zjs.mj.util;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.LocalDateTime;

public class DateTimeUtil {

    public static LocalDateTime tomorrow() {
        LocalDateTime localDateTime = LocalDateTime.now();
        LocalDateTime begin = LocalDateTimeUtil.beginOfDay(localDateTime);
        return begin.plusDays(1);
    }

    public static LocalDateTime nextMonth() {
        LocalDateTime now = LocalDateTime.now();
        return now.plusMonths(1);
    }

    public static boolean expire(LocalDateTime expireTime) {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(expireTime);
    }
}
