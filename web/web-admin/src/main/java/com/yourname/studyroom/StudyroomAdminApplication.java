package com.yourname.studyroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 启动类
 */

@SpringBootApplication
@EnableScheduling //开启定时任务支持
public class StudyroomAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(StudyroomAdminApplication.class, args);
    }
}
