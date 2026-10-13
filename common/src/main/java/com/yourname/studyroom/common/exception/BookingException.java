package com.yourname.studyroom.common.exception;

import com.yourname.studyroom.common.result.ResultCodeEnum;
import lombok.Data;

/**
 * @ClassName BookingException
 * @Description TODO 自定义异常类
 * @Author LuKey_C
 * @Date 2025/2/4 9:22
 * @Version 1.0
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
