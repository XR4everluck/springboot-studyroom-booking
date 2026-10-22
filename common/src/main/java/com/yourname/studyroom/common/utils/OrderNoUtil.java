package com.yourname.studyroom.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 预订订单编号生成工具，格式：SO + 下单时间（秒级） + 4 位随机数。
 */
public class OrderNoUtil {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private OrderNoUtil() {
    }

    public static String generate() {
        return "SO" + LocalDateTime.now().format(TIME_FORMAT) + ThreadLocalRandom.current().nextInt(1000, 10000);
    }
}
