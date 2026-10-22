package com.yourname.studyroom.common.exception;

import com.yourname.studyroom.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 全局异常处理类
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 异常处理方法
     * <p>
     * 当控制器中抛出任何异常时，此方法会被调用，用于统一处理异常。
     *
     * @param e 抛出的异常对象
     * @return Result 对象，表示处理结果。这里调用 Result.fail() 方法返回一个失败的结果。
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        log.error("系统异常", e);
        return Result.fail();
    }


    /**
     * 业务异常（未登录、token 过期等）属于可预期情况，只记一行日志，避免刷堆栈
     */
    @ExceptionHandler(BookingException.class)
    @ResponseBody
    public Result handle(BookingException e) {
        log.warn("业务异常: code={} msg={}", e.getCode(), e.getMessage());
        return Result.fail(e.getCode(), e.getMessage());
    }
}
