package com.yourname.studyroom.common.minio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Minio属性类
 */

//类属性应该映射到哪个字符串开头的属性
//minio.*
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    //属性endpoint 自动映射到 minio.endpoint
    private String endpoint;
    //属性accessKey 自动映射到 minio.access-key
    private String accessKey;

    private String secretKey;

    private String bucketName;
}
