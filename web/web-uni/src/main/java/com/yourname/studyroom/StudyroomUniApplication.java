package com.yourname.studyroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 单端口统一应用启动类（8080）
 * <p>
 * 同时承载管理端（/console 前端 + /admin REST）与用户端（/h5 前端 + /app REST）：
 * 组件扫描覆盖整个 com.yourname.studyroom，装配两个业务库（web-admin / web-app）的
 * Controller/Service/拦截器；Bean 名采用全限定类名生成，规避两库同名类的 Bean 名冲突
 * （沿用 web-portal 已验证的方案）。两套 JWT 拦截器分别在各自 WebMvcConfiguration 中
 * 限定 /admin/** 与 /app/** 路径，令牌分型互不通用，聚合后互不越界。
 * <p>
 * 扫描排除项：
 * 1. 两个业务库的旧启动类——避免其 @MapperScan/@EnableScheduling 与本类重复生效；
 * 2. 两库各自的 Knife4jConfiguration——两份 @Bean 方法名（customOpenAPI/loginAPI）冲突，
 *    统一改用 web.uni.config.Knife4jConfiguration 合并全部分组。
 */
@SpringBootApplication
@EnableScheduling //开启定时任务支持（原管理端）
@EnableAsync //开启异步请求支持（原用户端）
@ComponentScan(
        basePackages = "com.yourname.studyroom",
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class,
        excludeFilters = @Filter(type = FilterType.REGEX, pattern = {
                "com\\.yourname\\.studyroom\\.Studyroom(Admin|App|Uni)Application",
                "com\\.yourname\\.studyroom\\.web\\..*\\.custom\\.config\\.Knife4jConfiguration"
        }))
@MapperScan(
        basePackages = {
                "com.yourname.studyroom.web.admin.mapper",
                "com.yourname.studyroom.web.app.mapper"
        },
        nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
public class StudyroomUniApplication {

    public static void main(String[] args) {
        SpringApplication.run(StudyroomUniApplication.class, args);
    }
}
