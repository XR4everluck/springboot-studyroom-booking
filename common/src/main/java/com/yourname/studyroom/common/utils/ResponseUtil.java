package com.yourname.studyroom.common.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.result.Result;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 拦截器写回工具
 * <p>
 * 拦截器里抛出的异常不会被全局异常处理器接管，会一路逃逸成容器的 500 错误页，
 * 前端只能看到「服务器内部错误」而无法按业务码引导重新登录，因此这里直接写出统一返回结构。
 */
public class ResponseUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void writeFail(HttpServletResponse response, BookingException e) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        try {
            MAPPER.writeValue(response.getWriter(), Result.fail(e.getCode(), e.getMessage()));
        } catch (IOException ex) {
            throw new IllegalStateException("写回登录校验结果失败", ex);
        }
    }
}
