package com.yourname.studyroom.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;


public enum BookingSourceType implements BaseEnum {

    NEW(1, "新签"),
    RENEW(2, "续期");

    @JsonValue
    @EnumValue
    private Integer code;

    private String name;

    BookingSourceType(Integer code, String name) {
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
