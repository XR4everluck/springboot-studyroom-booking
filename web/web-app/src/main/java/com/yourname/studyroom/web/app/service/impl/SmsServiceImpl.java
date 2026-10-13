package com.yourname.studyroom.web.app.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.yourname.studyroom.web.app.service.SmsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsServiceImpl implements SmsService {

    private final ObjectProvider<Client> clientProvider;

    public SmsServiceImpl(ObjectProvider<Client> clientProvider) {
        this.clientProvider = clientProvider;
    }

    @Override
    public void sendCode(String phone, String code) {
        Client client = clientProvider.getIfAvailable();
        // 未配置阿里云短信凭证时不真正发送，验证码仍写入 Redis，仅打印到日志供本地联调使用
        if (client == null) {
            log.info("未配置阿里云短信服务，手机号 {} 的登录验证码为：{}", phone, code);
            return;
        }
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phone);
        request.setSignName("阿里云短信测试");
        request.setTemplateCode("SMS_154950909");
        request.setTemplateParam("{\"code\":\"" + code + "\"}");
        try {
            client.sendSms(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
