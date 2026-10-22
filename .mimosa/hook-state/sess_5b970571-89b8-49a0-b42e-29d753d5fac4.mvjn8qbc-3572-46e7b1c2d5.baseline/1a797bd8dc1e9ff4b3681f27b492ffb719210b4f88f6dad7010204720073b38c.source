package com.yourname.studyroom.model.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;


public enum ItemType implements BaseEnum {

    STORE(1, "自习室门店"),

    SEAT(2, "座位");

    //MybatisPlus TypeHandler 使code和枚举实例完成映射 1 <-> STORE
    //http参数和枚举类型之间的转换
    @EnumValue
    //Spring Mvc HttpMessageConverter 使code和枚举实例完成映射 1 <-> STORE
    @JsonValue
    private Integer code;
    private String name;

    @Override
    public Integer getCode() {
        return this.code;
    }


    @Override
    public String getName() {
        return name;
    }

    ItemType(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

}
