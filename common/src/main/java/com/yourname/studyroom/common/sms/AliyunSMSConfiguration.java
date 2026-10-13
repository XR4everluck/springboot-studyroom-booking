package com.yourname.studyroom.common.sms;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 阿里云SMS配置类
 */
@Configuration
@EnableConfigurationProperties(AliyunSMSProperties.class)
@ConditionalOnProperty(name = "aliyun.sms.access-key-id") //仅当配置了短信凭证才创建客户端，否则本地联调时验证码只打印日志
public class AliyunSMSConfiguration {

    @Autowired
    private AliyunSMSProperties properties;


    /**
     * 创建一个短信服务的客户端
     *
     * @return 返回配置好的短信服务客户端
     * @throws RuntimeException 如果创建客户端时发生异常，抛出运行时异常
     */
    @Bean
    public Client smsClient() {
        Config config = new Config();
        config.setAccessKeyId(properties.getAccessKeyId());
        config.setAccessKeySecret(properties.getAccessKeySecret());
        config.setEndpoint(properties.getEndpoint());
        try {
            return new Client(config);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}