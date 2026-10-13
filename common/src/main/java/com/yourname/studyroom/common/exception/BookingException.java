package com.yourname.studyroom.common.exception;

import com.yourname.studyroom.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * 自定义异常类
 */
@Data
public class BookingException extends RuntimeException{

    private Integer code;

    public BookingException(Integer code,String msg){
        super(msg);
        this.code = code;
    }

    public BookingException(ResultCodeEnum resultCodeEnum){
        super(resultCodeEnum.getMessage());
        this.code= resultCodeEnum.getCode();
    }
}
