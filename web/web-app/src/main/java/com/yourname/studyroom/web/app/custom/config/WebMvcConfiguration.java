package com.yourname.studyroom.web.app.custom.config;

import com.yourname.studyroom.web.app.custom.converter.StringToBaseEnumConverterFactory;
import com.yourname.studyroom.web.app.custom.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Autowired
    private StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;

    /**
     * 注册数字字符串 -> 枚举 的转换器，保证 /app 端以数字码传枚举参数（如订单状态）时能正确绑定
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(stringToBaseEnumConverterFactory);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 浏览类接口（选座/门店/地区/时段/支付方式）允许未登录访问，下单与个人中心仍需登录
        registry.addInterceptor(this.authenticationInterceptor).
                addPathPatterns("/app/**").
                excludePathPatterns(
                        "/app/login/**",
                        "/app/register/**",
                        "/app/seat/**",
                        "/app/store/**",
                        "/app/region/**",
                        "/app/term/**",
                        "/app/payment/**");
    }
}