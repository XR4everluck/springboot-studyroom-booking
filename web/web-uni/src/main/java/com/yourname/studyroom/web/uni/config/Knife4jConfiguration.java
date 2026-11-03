package com.yourname.studyroom.web.uni.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 统一应用接口文档配置：合并两库原有分组（管理端 5 组 + 用户端 3 组）。
 * 两库各自的 Knife4jConfiguration 已在启动类扫描中排除，避免同名 @Bean 方法冲突。
 */
@Configuration
public class Knife4jConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("上岸自习室统一应用API（管理端 /admin + 用户端 /app）")
                .version("1.0")
                .description("单端口聚合应用接口文档，/doc.html 查看"));
    }

    // ---------- 管理端分组（原 web-admin Knife4jConfiguration） ----------

    @Bean
    public GroupedOpenApi systemAPI() {
        return GroupedOpenApi.builder().group("系统信息管理").
                pathsToMatch(
                        "/admin/system/**"
                ).
                build();
    }

    @Bean
    public GroupedOpenApi adminLoginAPI() {
        return GroupedOpenApi.builder().group("后台登录管理").
                pathsToMatch(
                        "/admin/login/**",
                        "/admin/info"
                ).
                build();
    }

    @Bean
    public GroupedOpenApi storeAPI() {
        return GroupedOpenApi.builder().group("自习室门店信息管理").
                pathsToMatch(
                        "/admin/store/**",
                        "/admin/seat/**",
                        "/admin/label/**",
                        "/admin/facility/**",
                        "/admin/fee/**",
                        "/admin/attr/**",
                        "/admin/payment/**",
                        "/admin/region/**",
                        "/admin/term/**",
                        "/admin/file/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi bookingAPI() {
        return GroupedOpenApi.builder().group("预订订单管理").
                pathsToMatch(
                        "/admin/visit/**",
                        "/admin/order/**"
                ).build();
    }

    @Bean
    public GroupedOpenApi userAPI() {
        return GroupedOpenApi.builder().group("平台用户管理").
                pathsToMatch(
                        "/admin/user/**"
                ).build();
    }

    // ---------- 用户端分组（原 web-app Knife4jConfiguration） ----------

    @Bean
    public GroupedOpenApi appLoginAPI() {
        return GroupedOpenApi.builder().group("登录信息").
                pathsToMatch("/app/login/**", "/app/info").
                build();
    }

    @Bean
    public GroupedOpenApi appPersonAPI() {
        return GroupedOpenApi.builder().group("个人信息").
                pathsToMatch(
                        "/app/history/**",
                        "/app/visit/**",
                        "/app/order/**"
                ).
                build();
    }

    @Bean
    public GroupedOpenApi appBrowseStoreAPI() {
        return GroupedOpenApi.builder().group("找自习室信息").
                pathsToMatch(
                        "/app/store/**",
                        "/app/seat/**",
                        "/app/payment/**",
                        "/app/region/**",
                        "/app/term/**"
                ).
                build();
    }
}
