package com.yourname.studyroom.web.app.custom.config;

import com.yourname.studyroom.web.app.custom.interceptor.AuthenticationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private AuthenticationInterceptor authenticationInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 浏览类接口（选座/门店/地区/时段/支付方式）允许未登录访问，下单与个人中心仍需登录
        registry.addInterceptor(this.authenticationInterceptor).
                addPathPatterns("/app/**").
                excludePathPatterns(
                        "/app/login/**",
                        "/app/seat/**",
                        "/app/store/**",
                        "/app/region/**",
                        "/app/term/**",
                        "/app/payment/**");
    }
}