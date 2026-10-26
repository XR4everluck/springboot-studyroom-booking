package com.yourname.studyroom.portal.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 门户 MVC 配置：枚举转换器、页面认证拦截器、根路径跳转与上传文件静态映射
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory;

    public WebConfig(StringToBaseEnumConverterFactory stringToBaseEnumConverterFactory) {
        this.stringToBaseEnumConverterFactory = stringToBaseEnumConverterFactory;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverterFactory(stringToBaseEnumConverterFactory);
    }

    /**
     * /admin/**、/app/** 全部要求登录且角色匹配；登录页、静态资源放行
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new PageAuthInterceptor())
                .addPathPatterns("/admin/**", "/app/**")
                .excludePathPatterns("/css/**", "/img/**", "/upload/**", "/favicon.ico", "/error");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 启动后首先显示登录界面
        registry.addRedirectViewController("/", "/login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 本地磁盘上传文件对外访问
        registry.addResourceHandler("/upload/**").addResourceLocations("file:upload-data/");
    }
}
