package com.yourname.studyroom.portal.page.app;

import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.web.app.service.BookingOrderService;
import com.yourname.studyroom.web.app.vo.order.OrderDetailVo;
import com.yourname.studyroom.web.app.vo.order.OrderItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 用户端我的订单：列表、详情、签到、签退、申请提前签退
 * （所有操作先经 getOwnedOrder 校验订单归属，防止越权操作他人订单）
 */
@Controller
public class AppOrderController {

    private final BookingOrderService appBookingOrderService;

    public AppOrderController(BookingOrderService appBookingOrderService) {
        this.appBookingOrderService = appBookingOrderService;
    }

    @GetMapping("/app/orders")
    public String list(Model model) {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        List<OrderItemVo> orders = appBookingOrderService.listItemByUserId(userId);
        model.addAttribute("orders", orders);
        return "app/orders";
    }

    @GetMapping("/app/orders/{id}")
    public String detail(@PathVariable Long id, Model model, RedirectAttributes ra) {
        try {
            Long userId = LoginUserHolder.getLoginUser().getUserId();
            appBookingOrderService.getOwnedOrder(userId, id);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", "订单不存在");
            return "redirect:/app/orders";
        }
        OrderDetailVo order = appBookingOrderService.getDetailById(id);
        model.addAttribute("order", order);
        return "app/order-detail";
    }

    @PostMapping("/app/orders/{id}/checkIn")
    public String checkIn(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Long userId = LoginUserHolder.getLoginUser().getUserId();
            appBookingOrderService.getOwnedOrder(userId, id);
            appBookingOrderService.checkIn(id);
            ra.addFlashAttribute("msg", "签到成功，开始学习吧！");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/orders";
    }

    @PostMapping("/app/orders/{id}/checkOut")
    public String checkOut(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Long userId = LoginUserHolder.getLoginUser().getUserId();
            appBookingOrderService.getOwnedOrder(userId, id);
            appBookingOrderService.checkOut(id);
            ra.addFlashAttribute("msg", "签退成功，本次使用已结束");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/orders";
    }

    /**
     * 申请提前签退：使用中 → 签退待确认，等待管理员确认
     */
    @PostMapping("/app/orders/{id}/earlyCheckout")
    public String earlyCheckout(@PathVariable Long id, RedirectAttributes ra) {
        try {
            Long userId = LoginUserHolder.getLoginUser().getUserId();
            appBookingOrderService.requestEarlyCheckout(userId, id);
            ra.addFlashAttribute("msg", "已提交提前签退申请，等待管理员确认");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/app/orders";
    }
}
