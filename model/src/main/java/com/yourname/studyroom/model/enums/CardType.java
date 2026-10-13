package com.yourname.studyroom.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 会员卡类型
 */
public enum CardType implements BaseEnum {

    TIMES(1, "次卡"),
    HOURS(2, "小时卡"),
    MONTH(3, "月卡");

    @EnumValue
    @JsonValue
    private Integer code;

    private String name;

    CardType(Integer code, String name) {
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

    public static CardType ofCode(Integer code) {
        if (code != null) {
            for (CardType value : values()) {
                if (value.code.equals(code)) {
                    return value;
                }
            }
        }
        return null;
    }
}
