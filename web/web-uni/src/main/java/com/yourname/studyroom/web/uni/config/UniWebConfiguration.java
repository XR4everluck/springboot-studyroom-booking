package com.yourname.studyroom.web.uni.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 统一应用页面路由：/console、/h5 短链转发到各自 index.html。
 * 两套前端均为 hash 路由，/console/index.html、/h5/** 下的静态资源
 * 由 static-locations（classpath:/uni-static/）直接命中，无需更多规则。
 * <p>
 * common 库的演示图片位于 classpath:/static/img/，因统一应用的 static-locations
 * 有意排除了默认的 classpath:/static/ 根（避免业务库 jar 内旧构建产物外泄），
 * 这里按 /upload/** 同样的方式为 /img/** 单独注册资源处理器。
 */
@Configuration
public class UniWebConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/console").setViewName("forward:/console/index.html");
        registry.addViewController("/console/").setViewName("forward:/console/index.html");
        registry.addViewController("/h5").setViewName("forward:/h5/index.html");
        registry.addViewController("/h5/").setViewName("forward:/h5/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**").addResourceLocations("classpath:/static/img/");
    }
}
