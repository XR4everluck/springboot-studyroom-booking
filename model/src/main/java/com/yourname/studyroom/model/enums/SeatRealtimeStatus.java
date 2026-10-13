package com.yourname.studyroom.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 座位实时状态（基于 Redis 缓存，不落库，用于列表/详情展示）
 */
public enum SeatRealtimeStatus implements BaseEnum {

    FREE(0, "空闲"),
    BOOKED(1, "已预订"),
    IN_USE(2, "使用中");

    @EnumValue
    @JsonValue
    private Integer code;

    private String name;

    SeatRealtimeStatus(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * 按编码解析实时状态，null 或未知编码统一视为空闲。
     */
    public static SeatRealtimeStatus ofCode(Integer code) {
        if (code != null) {
            for (SeatRealtimeStatus value : values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
        }
        return FREE;
    }
}
