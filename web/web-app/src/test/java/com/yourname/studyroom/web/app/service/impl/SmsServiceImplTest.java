package com.yourname.studyroom.web.app.service.impl;

import com.yourname.studyroom.web.app.service.SmsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SmsServiceImplTest
 */
@SpringBootTest
class SmsServiceImplTest {
    @Autowired
    SmsService smsService;
    @Test
    void sendCode() {
        smsService.sendCode("15515691952","8888");
    }
}