package com.yourname.studyroom.common.storage;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 本地磁盘文件存储属性
 */
@Data
@ConfigurationProperties(prefix = "storage")
public class LocalStorageProperties {

    /**
     * 文件存放的本地目录
     */
    private String uploadDir;

    /**
     * 对外访问的 URL 前缀
     */
    private String urlPrefix = "/upload";
}
