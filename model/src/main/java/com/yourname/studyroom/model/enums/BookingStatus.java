package com.yourname.studyroom.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum BookingStatus implements BaseEnum {

    CONFIRM_PENDING(1, "待确认"),
    BOOKED(2, "已预订"),
    CANCELED(3, "已取消"),
    EXPIRED(4, "已到期"),
    CHECKOUT_PENDING(5, "签退待确认"),
    COMPLETED(6, "已完成"),
    RENEW_PENDING(7, "续期待确认"),
    IN_USE(8, "使用中");

    @EnumValue
    @JsonValue
    private Integer code;

    private String name;

    BookingStatus(Integer code, String name) {
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

}
