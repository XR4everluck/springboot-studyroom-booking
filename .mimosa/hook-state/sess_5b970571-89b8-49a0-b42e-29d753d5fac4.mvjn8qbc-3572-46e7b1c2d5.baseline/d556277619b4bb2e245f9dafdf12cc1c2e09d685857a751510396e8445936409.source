package com.yourname.studyroom.web.app.controller.bookingterm;

import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.BookingTerm;
import com.yourname.studyroom.web.app.mapper.BookingTermMapper;
import com.yourname.studyroom.web.app.service.BookingTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/term/")
@Tag(name = "预订时长信息")
public class BookingTermController {

    @Autowired
    BookingTermService bookingTermService;

    /**
     * 根据座位id获取可选预订时长列表
     *
     * @param id 座位id
     * @return 包含可选预订时长列表的响应结果
     */
    @GetMapping("listBySeatId")
    @Operation(summary = "根据座位id获取可选获取预订时长列表")
    public Result<List<BookingTerm>> list(@RequestParam Long id) {
        List<BookingTerm> result = bookingTermService.listBySeatId(id);
        return Result.ok(result);
    }
}
