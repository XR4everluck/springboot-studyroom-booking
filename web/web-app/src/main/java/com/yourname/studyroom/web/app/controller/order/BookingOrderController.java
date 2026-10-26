package com.yourname.studyroom.web.app.controller.order;

import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.common.login.LoginUser;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.common.result.ResultCodeEnum;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.entity.UserInfo;
import com.yourname.studyroom.model.enums.BookingSourceType;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.app.service.BookingOrderService;
import com.yourname.studyroom.web.app.service.UserInfoService;
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

    @Autowired
    UserInfoService userInfoService;

    /**
     * 获取个人预订订单基本信息列表
     *
     * @return 个人预订订单基本信息列表
     */
    @Operation(summary = "获取个人预订订单基本信息列表")
    @GetMapping("listItem")
    public Result<List<OrderItemVo>> listItem() {
        List<OrderItemVo> result = bookingOrderService.listItemByUserId(LoginUserHolder.getLoginUser().getUserId());
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
        // 先校验订单归属，防止查看他人订单
        bookingOrderService.getOwnedOrder(currentUserId(), id);
        OrderDetailVo orderDetailVo = bookingOrderService.getDetailById(id);
        return Result.ok(orderDetailVo);
    }

    /**
     * 申请提前签退：使用中 → 签退待确认，等待管理员确认
     *
     * @param id 预订订单ID
     * @return 操作结果
     * @description 仅订单本人可操作；原先"任意状态随意改"的接口已收紧为该专用流转
     */
    @Operation(summary = "申请提前签退", description = "使用中订单申请提前签退，流转为签退待确认")
    @PostMapping("updateStatusById")
    public Result updateStatusById(@RequestParam Long id, @RequestParam BookingStatus status) {
        if (status != BookingStatus.CHECKOUT_PENDING) {
            // 用户端仅允许发起提前签退这一种状态流转，其余状态变更由后台管理端操作
            throw new BookingException(ResultCodeEnum.APP_ORDER_STATUS_NOT_ALLOW);
        }
        bookingOrderService.requestEarlyCheckout(currentUserId(), id);
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
        LoginUser loginUser = LoginUserHolder.getLoginUser();
        // 续期（更新）时校验订单归属，防止冒改他人订单；新单 id 为空无需校验
        if (bookingOrder.getId() != null) {
            bookingOrderService.getOwnedOrder(loginUser.getUserId(), bookingOrder.getId());
        }
        // 订单归属以登录用户为准，联系电话取用户绑定的手机号（注册时选填），避免越权为他人下单
        bookingOrder.setUserId(loginUser.getUserId());
        UserInfo loginUser_ = userInfoService.getById(loginUser.getUserId());
        bookingOrder.setPhone(loginUser_ == null ? null : loginUser_.getPhone());
        if (bookingOrder.getSourceType() == null) {
            bookingOrder.setSourceType(BookingSourceType.NEW);
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
        bookingOrderService.getOwnedOrder(currentUserId(), id);
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
        bookingOrderService.getOwnedOrder(currentUserId(), id);
        bookingOrderService.checkOut(id);
        return Result.ok();
    }

    private Long currentUserId() {
        return LoginUserHolder.getLoginUser().getUserId();
    }

}
