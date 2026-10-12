package com.yourname.studyroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @ClassName StudyroomAppApplication
 * @Description TODO 移动端启动类
 * @Author LuKey_C
 * @Date 2025/2/10 8:52
 * @Version 1.0
 */
@SpringBootApplication
@EnableAsync //开启异步请求支持
public class StudyroomAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyroomAppApplication.class);
    }
}
