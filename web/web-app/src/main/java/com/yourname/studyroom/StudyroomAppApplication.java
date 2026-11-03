package com.yourname.studyroom;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 移动端启动类
 */
@SpringBootApplication
@EnableAsync //开启异步请求支持
@MapperScan("com.yourname.studyroom.web.app.mapper")
public class StudyroomAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyroomAppApplication.class, args);
    }
}
