package com.yourname.studyroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 统一门户启动类
 * <p>
 * 全站为 Thymeleaf 服务端渲染（无任何 JavaScript/独立前端工程），
 * 启动后首先进入 /login 登录页，登录成功后按角色跳转管理端或用户端界面。
 * <p>
 * 组件扫描覆盖整个 com.yourname.studyroom 以装配两个业务库（web-admin / web-app）中的
 * Service/Mapper 组件；所有 Bean 名采用全限定类名生成，避免两库同名类的 Bean 名冲突。
 * 两个业务库残留的旧 REST 控制器、JWT 拦截器与启动类不属于页面应用，扫描时排除。
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(
        basePackages = "com.yourname.studyroom",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        excludeFilters = @Filter(type = FilterType.REGEX, pattern = {
                "com\\.yourname\\.studyroom\\.web\\..*\\.controller\\..*",
                "com\\.yourname\\.studyroom\\.web\\..*\\.custom\\..*",
                "com\\.yourname\\.studyroom\\.Studyroom(Admin|App)Application"
        }))
@MapperScan(
        basePackages = {
                "com.yourname.studyroom.web.admin.mapper",
                "com.yourname.studyroom.web.app.mapper"
        },
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class PortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortalApplication.class, args);
    }
}
