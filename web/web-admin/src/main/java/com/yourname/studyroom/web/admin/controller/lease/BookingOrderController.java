package com.yourname.studyroom.web.admin.controller.lease;


import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.admin.service.BookingOrderService;
import com.yourname.studyroom.web.admin.vo.agreement.AgreementQueryVo;
import com.yourname.studyroom.web.admin.vo.agreement.AgreementVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Tag(name = "租约管理")
@RestController
@RequestMapping("/admin/agreement")
public class BookingOrderController {

    @Autowired
    BookingOrderService bookingOrderService;

    /**
     * 保存或修改租约信息
     *
     * @param bookingOrder 租约信息实体
     * @return 操作结果
     */
    @Operation(summary = "保存或修改租约信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody BookingOrder bookingOrder) {
        bookingOrderService.saveOrUpdate(bookingOrder);
        return Result.ok();
    }

    /**
     * 根据条件分页查询租约列表
     *
     * @param current 当前页码
     * @param size    每页数量
     * @param queryVo 查询条件
     * @return 查询结果
     */
    @Operation(summary = "根据条件分页查询租约列表")
    @GetMapping("page")
    public Result<IPage<AgreementVo>> page(@RequestParam long current, @RequestParam long size, AgreementQueryVo queryVo) {
        IPage<AgreementVo> page = new Page<>(current, size);
        IPage<AgreementVo> result = bookingOrderService.pageAgreementByQuery(page, queryVo);
        return Result.ok(result);
    }

    /**
     * 根据id查询租约信息
     *
     * @param id 租约ID
     * @return 查询结果
     */
    @Operation(summary = "根据id查询租约信息")
    @GetMapping(name = "getById")
    public Result<AgreementVo> getById(@RequestParam Long id) {
        AgreementVo result = bookingOrderService.getAgreementById(id);
        return Result.ok(result);
    }

    /**
     * 根据id删除租约信息
     *
     * @param id 租约ID
     * @return 操作结果
     */
    @Operation(summary = "根据id删除租约信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        bookingOrderService.removeById(id);
        return Result.ok();
    }

    /**
     * 根据id更新租约状态
     *
     * @param id     租约ID
     * @param status 租约状态
     * @return 操作结果
     */
    @Operation(summary = "根据id更新租约状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam BookingStatus status) {
        LambdaUpdateWrapper<BookingOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BookingOrder::getId,id);
        updateWrapper.set(BookingOrder::getStatus,status);
        bookingOrderService.update(updateWrapper);
        return Result.ok();
    }

}

