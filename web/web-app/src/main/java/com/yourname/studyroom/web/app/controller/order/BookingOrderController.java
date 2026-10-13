package com.yourname.studyroom.web.app.controller.order;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.app.service.BookingOrderService;
import com.yourname.studyroom.web.app.vo.order.OrderDetailVo;
import com.yourname.studyroom.web.app.vo.order.OrderItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/app/order")
@Tag(name = "预订订单信息")
public class BookingOrderController {

    @Autowired
    BookingOrderService bookingOrderService;

    /**
     * 获取个人预订订单基本信息列表
     *
     * @return 个人预订订单基本信息列表
     */
    @Operation(summary = "获取个人预订订单基本信息列表")
    @GetMapping("listItem")
    public Result<List<OrderItemVo>> listItem() {
        List<OrderItemVo> result = bookingOrderService.listItemByPhone(LoginUserHolder.getLoginUser().getUsername());
        return Result.ok(result);
    }

    /**
     * 根据id获取预订订单详细信息
     *
     * @param id 预订订单ID
     * @return 预订订单详细信息对象
     */
    @Operation(summary = "根据id获取预订订单详细信息")
    @GetMapping("getDetailById")
    public Result<OrderDetailVo> getDetailById(@RequestParam Long id) {
        OrderDetailVo orderDetailVo = bookingOrderService.getDetailById(id);
        return Result.ok(orderDetailVo);
    }

    /**
     * 根据id更新预订订单状态
     *
     * @param id        预订订单ID
     * @param status 预订订单状态
     * @return 操作结果
     * @description 用于确认预订订单和提前退座
     */
    @Operation(summary = "根据id更新预订订单状态", description = "用于确认预订订单和提前退座")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam BookingStatus status) {
        LambdaUpdateWrapper<BookingOrder> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(BookingOrder::getId, id);
        updateWrapper.set(BookingOrder::getStatus, status);
        bookingOrderService.update(updateWrapper);
        return Result.ok();
    }

    /**
     * 保存或更新预订订单
     *
     * @param bookingOrder 预订订单对象
     * @return 操作结果
     * @description 用于下单与续期，含营业时间校验与时段冲突校验
     */
    @Operation(summary = "保存或更新预订订单", description = "用于下单与续期，含营业时间校验与时段冲突校验")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody BookingOrder bookingOrder) {
        if (bookingOrder.getUserId() == null) {
            bookingOrder.setUserId(LoginUserHolder.getLoginUser().getUserId());
        }
        bookingOrderService.createOrUpdateOrder(bookingOrder);
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
