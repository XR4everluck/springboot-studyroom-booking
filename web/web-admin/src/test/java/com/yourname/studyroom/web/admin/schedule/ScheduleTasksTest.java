package com.yourname.studyroom.web.admin.schedule;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * ScheduleTasksTest
 */
@SpringBootTest
class ScheduleTasksTest {

    @Autowired
    ScheduleTasks scheduleTasks;

    @Test
    public void test(){
        scheduleTasks.checkBookingStatus();
    }
}