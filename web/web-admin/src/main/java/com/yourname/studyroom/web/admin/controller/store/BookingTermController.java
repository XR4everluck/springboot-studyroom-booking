package com.yourname.studyroom.web.admin.controller.store;


import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.BookingTerm;
import com.yourname.studyroom.web.admin.service.BookingTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Tag(name = "预订时长管理")
@RequestMapping("/admin/term")
@RestController
public class BookingTermController {

    @Autowired
    BookingTermService bookingTermService;

    /**
     * 查询全部预订时长列表
     * @return Result<List<BookingTerm>>
     */
    @GetMapping("list")
    @Operation(summary = "查询全部预订时长列表")
    public Result<List<BookingTerm>> listBookingTerm() {
        List<BookingTerm> bookingTerms = bookingTermService.list();
        return Result.ok(bookingTerms);
    }

    /**
     * 保存或更新预订时长信息
     * @param bookingTerm
     * @return Result
     */
    @PostMapping("saveOrUpdate")
    @Operation(summary = "保存或更新预订时长信息")
    public Result saveOrUpdate(@RequestBody BookingTerm bookingTerm) {
        bookingTermService.saveOrUpdate(bookingTerm);
        return Result.ok();
    }

    /**
     * 根据ID删除预订时长
     * @param id
     * @return Result
     */
    @DeleteMapping("deleteById")
    @Operation(summary = "根据ID删除预订时长")
    public Result deleteBookingTermById(@RequestParam Long id) {
        bookingTermService.removeById(id);
        return Result.ok();
    }
}
