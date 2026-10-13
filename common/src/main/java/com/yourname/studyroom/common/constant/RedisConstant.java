package com.yourname.studyroom.common.constant;

/**
 * Redis 缓存 key 前缀与过期时间常量
 */
public class RedisConstant {
    public static final String ADMIN_LOGIN_PREFIX = "admin:login:";
    public static final Integer ADMIN_LOGIN_CAPTCHA_TTL_SEC = 60;
    public static final String APP_LOGIN_PREFIX = "app:login:";
    public static final Integer APP_LOGIN_CODE_RESEND_TIME_SEC = 60;
    public static final Integer APP_LOGIN_CODE_TTL_SEC = 60 * 10;
    public static final String APP_SEAT_PREFIX = "app:seat:";
    /** 座位实时状态缓存前缀，值为 SeatRealtimeStatus 的 code */
    public static final String APP_SEAT_STATUS_PREFIX = "app:seat:status:";
    /** 座位实时状态缓存过期时间（秒），1 天 */
    public static final Integer APP_SEAT_STATUS_TTL_SEC = 60 * 60 * 24;

    /** 首页热门门店缓存 key（值为按浏览量降序的门店 id 列表） */
    public static final String APP_HOT_STORE_LIST = "app:store:hot:list";
    /** 首页热门座位缓存 key（值为按浏览量降序的座位 id 列表） */
    public static final String APP_HOT_SEAT_LIST = "app:seat:hot:list";
    /** 首页热门榜单缓存过期时间（秒），1 小时 */
    public static final Integer APP_HOT_LIST_TTL_SEC = 60 * 60;
}
