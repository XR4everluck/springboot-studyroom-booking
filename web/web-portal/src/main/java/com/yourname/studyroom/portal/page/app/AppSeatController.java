package com.yourname.studyroom.portal.page.app;

import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.entity.MemberCard;
import com.yourname.studyroom.model.entity.UserInfo;
import com.yourname.studyroom.model.enums.BookingSourceType;
import com.yourname.studyroom.model.enums.SeatRealtimeStatus;
import com.yourname.studyroom.web.app.service.BookingOrderService;
import com.yourname.studyroom.web.app.service.BrowsingHistoryService;
import com.yourname.studyroom.web.app.service.MemberCardService;
import com.yourname.studyroom.web.app.service.SeatService;
import com.yourname.studyroom.web.app.service.UserInfoService;
import com.yourname.studyroom.web.app.vo.seat.SeatDetailVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 用户端座位详情与下单：
 * 展示座位信息与可选支付方式，提交起止时间后服务端计算金额并创建订单（支持会员卡抵扣），
 * 浏览详情时记录浏览历史。
 */
@Controller
public class AppSeatController {

    private final SeatService appSeatService;
    private final BookingOrderService appBookingOrderService;
    private final MemberCardService appMemberCardService;
    private final UserInfoService appUserInfoService;
    private final BrowsingHistoryService appBrowsingHistoryService;

    public AppSeatController(SeatService appSeatService,
                             BookingOrderService appBookingOrderService,
                             MemberCardService appMemberCardService,
                             UserInfoService appUserInfoService,
                             BrowsingHistoryService appBrowsingHistoryService) {
        this.appSeatService = appSeatService;
        this.appBookingOrderService = appBookingOrderService;
        this.appMemberCardService = appMemberCardService;
        this.appUserInfoService = appUserInfoService;
        this.appBrowsingHistoryService = appBrowsingHistoryService;
    }

    @GetMapping("/app/seat/{id}")
    public String detail(@PathVariable Long id, Model model) {
        SeatDetailVo seat = appSeatService.getDetailById(id);
        if (seat == null) {
            return "redirect:/app/home";
        }
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        appBrowsingHistoryService.saveHistory(userId, id);

        model.addAttribute("seat", seat);
        model.addAttribute("busy", seat.getRealtimeStatus() != null
                && seat.getRealtimeStatus().equals(SeatRealtimeStatus.IN_USE.getCode()));
        List<MemberCard> cards = appMemberCardService.listUsableByUserId(userId);
        // 仅展示与座位门店匹配（或全门店通用）的会员卡
        Long storeId = seat.getStoreItemVo() == null ? null : seat.getStoreItemVo().getId();
        model.addAttribute("cards", cards.stream()
                .filter(c -> c.getStoreId() == null || c.getStoreId().equals(storeId))
                .toList());
        return "app/seat-detail";
    }

    @PostMapping("/app/seat/{id}/book")
    public String book(@PathVariable Long id,
                       @RequestParam String startDate,
                       @RequestParam String endDate,
                       @RequestParam(required = false) Long paymentTypeId,
                       @RequestParam(required = false) Long memberCardId,
                       @RequestParam(required = false) String additionalInfo,
                       RedirectAttributes ra) {
        SeatDetailVo seat = appSeatService.getDetailById(id);
        if (seat == null || seat.getStoreItemVo() == null) {
            ra.addFlashAttribute("error", "座位不存在");
            return "redirect:/app/home";
        }
        Date start;
        Date end;
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            start = fmt.parse(startDate);
            end = fmt.parse(endDate);
        } catch (ParseException e) {
            ra.addFlashAttribute("error", "时间格式不正确");
            return "redirect:/app/seat/" + id;
        }
        if (!end.after(start)) {
            ra.addFlashAttribute("error", "结束时间必须晚于开始时间");
            return "redirect:/app/seat/" + id;
        }

        Long userId = LoginUserHolder.getLoginUser().getUserId();
        BookingOrder order = new BookingOrder();
        order.setUserId(userId);
        order.setStoreId(seat.getStoreItemVo().getId());
        order.setSeatId(id);
        order.setStartDate(start);
        order.setEndDate(end);
        order.setPaymentTypeId(paymentTypeId);
        order.setMemberCardId(memberCardId);
        order.setSourceType(BookingSourceType.NEW);
        order.setBookingType((long) Math.ceil((end.getTime() - start.getTime()) / 3600000.0) + "小时");
        order.setTotalAmount(seat.getPrice() == null ? null
                : seat.getPrice().multiply(BigDecimal.valueOf(
                        Math.ceil((end.getTime() - start.getTime()) / 3600000.0))));
        order.setAdditionalInfo(additionalInfo);
        UserInfo user = appUserInfoService.getById(userId);
        if (user != null) {
            order.setName(user.getNickname());
            order.setPhone(user.getPhone());
        }
        try {
            appBookingOrderService.createOrUpdateOrder(order);
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/app/seat/" + id;
        }
        ra.addFlashAttribute("msg", "预订成功，可在「我的订单」中查看");
        return "redirect:/app/orders";
    }
}
