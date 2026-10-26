package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.admin.service.BookingOrderService;
import com.yourname.studyroom.web.admin.vo.order.OrderQueryVo;
import com.yourname.studyroom.web.admin.vo.order.OrderVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 管理端订单管理：分页列表（按联系人/手机号/门店过滤）、详情、状态确认与签到签退
 */
@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    private final BookingOrderService bookingOrderService;

    public AdminOrderController(BookingOrderService bookingOrderService) {
        this.bookingOrderService = bookingOrderService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long size,
                       @RequestParam(required = false) String name,
                       @RequestParam(required = false) String phone,
                       @RequestParam(required = false) Long storeId,
                       @RequestParam(required = false) BookingStatus status,
                       Model model) {
        OrderQueryVo queryVo = new OrderQueryVo();
        queryVo.setName(name);
        queryVo.setPhone(phone);
        queryVo.setStoreId(storeId);
        queryVo.setStatus(status);
        Page<OrderVo> page = new Page<>(current, size);
        model.addAttribute("page", bookingOrderService.pageOrderByQuery(page, queryVo));
        model.addAttribute("name", name);
        model.addAttribute("phone", phone);
        model.addAttribute("storeId", storeId);
        model.addAttribute("status", status);
        return "admin/order-list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        model.addAttribute("order", bookingOrderService.getOrderById(id));
        return "admin/order-detail";
    }

    /**
     * 状态确认：待确认→已预订、签退待确认→已完成、续期待确认→已预订/使用中、任意→已取消
     */
    @PostMapping("/status/{id}")
    public String confirm(@PathVariable Long id,
                          @RequestParam BookingStatus status,
                          RedirectAttributes ra) {
        bookingOrderService.confirmStatus(id, status);
        ra.addFlashAttribute("msg", "订单状态已更新为「" + status.getName() + "」");
        return "redirect:/admin/order/" + id;
    }

    @PostMapping("/checkIn/{id}")
    public String checkIn(@PathVariable Long id, RedirectAttributes ra) {
        bookingOrderService.checkIn(id);
        ra.addFlashAttribute("msg", "订单已签到");
        return "redirect:/admin/order/" + id;
    }

    @PostMapping("/checkOut/{id}")
    public String checkOut(@PathVariable Long id, RedirectAttributes ra) {
        bookingOrderService.checkOut(id);
        ra.addFlashAttribute("msg", "订单已签退");
        return "redirect:/admin/order/" + id;
    }
}
