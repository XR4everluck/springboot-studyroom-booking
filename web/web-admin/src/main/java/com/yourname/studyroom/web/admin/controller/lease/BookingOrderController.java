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


@Tag(name = "预订订单管理")
@RestController
@RequestMapping("/admin/order")
public class BookingOrderController {

    @Autowired
    BookingOrderService bookingOrderService;

    /**
     * 保存或修改预订订单信息
     *
     * @param bookingOrder 预订订单信息实体
     * @return 操作结果
     */
    @Operation(summary = "保存或修改预订订单信息", description = "含营业时间校验与时段冲突校验")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody BookingOrder bookingOrder) {
        bookingOrderService.createOrUpdateOrder(bookingOrder);
        return Result.ok();
    }

    /**
     * 根据条件分页查询预订订单列表
     *
     * @param current 当前页码
     * @param size    每页数量
     * @param queryVo 查询条件
     * @return 查询结果
     */
    @Operation(summary = "根据条件分页查询预订订单列表")
    @GetMapping("page")
    public Result<IPage<AgreementVo>> page(@RequestParam long current, @RequestParam long size, AgreementQueryVo queryVo) {
        IPage<AgreementVo> page = new Page<>(current, size);
        IPage<AgreementVo> result = bookingOrderService.pageAgreementByQuery(page, queryVo);
        return Result.ok(result);
    }

    /**
     * 根据id查询预订订单信息
     *
     * @param id 预订订单ID
     * @return 查询结果
     */
    @Operation(summary = "根据id查询预订订单信息")
    @GetMapping(name = "getById")
    public Result<AgreementVo> getById(@RequestParam Long id) {
        AgreementVo result = bookingOrderService.getAgreementById(id);
        return Result.ok(result);
    }

    /**
     * 根据id删除预订订单信息
     *
     * @param id 预订订单ID
     * @return 操作结果
     */
    @Operation(summary = "根据id删除预订订单信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        bookingOrderService.removeById(id);
        return Result.ok();
    }

    /**
     * 根据id更新预订订单状态
     *
     * @param id     预订订单ID
     * @param status 预订订单状态
     * @return 操作结果
     */
    @Operation(summary = "根据id更新预订订单状态")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam BookingStatus status) {
        LambdaUpdateWrapper<BookingOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BookingOrder::getId,id);
        updateWrapper.set(BookingOrder::getStatus,status);
        bookingOrderService.update(updateWrapper);
        return Result.ok();
    }

    /**
     * 订单签到：已预订 → 使用中
     *
     * @param id 预订订单ID
     * @return 操作结果
     */
    @Operation(summary = "订单签到", description = "已预订订单签到后转为使用中")
    @PostMapping("checkIn")
    public Result checkIn(@RequestParam Long id) {
        bookingOrderService.checkIn(id);
        return Result.ok();
    }

    /**
     * 订单签退：使用中 → 已完成
     *
     * @param id 预订订单ID
     * @return 操作结果
     */
    @Operation(summary = "订单签退", description = "使用中订单签退后转为已完成并释放座位")
    @PostMapping("checkOut")
    public Result checkOut(@RequestParam Long id) {
        bookingOrderService.checkOut(id);
        return Result.ok();
    }

}

