package com.yourname.studyroom.common.storage;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * 本地磁盘文件存储配置类：把存储目录映射为可公开访问的静态资源
 */
@Configuration
@EnableConfigurationProperties(LocalStorageProperties.class)
@ConditionalOnProperty(name = "storage.upload-dir")
public class LocalStorageConfiguration {

    @Bean
    public WebMvcConfigurer localStorageResourceConfigurer(LocalStorageProperties properties) {
        return new WebMvcConfigurer() {
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
                registry.addResourceHandler(properties.getUrlPrefix() + "/**")
                        .addResourceLocations(toResourceLocation(properties.getUploadDir()));
            }
        };
    }

    private String toResourceLocation(String uploadDir) {
        File dir = new File(uploadDir).getAbsoluteFile();
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("无法创建文件存储目录: " + dir);
        }
        return "file:" + dir.getAbsolutePath().replace('\\', '/') + "/";
    }

    @Bean
    public LocalStorageOperator localStorageOperator(LocalStorageProperties properties) {
        return new LocalStorageOperator(properties);
    }
}
