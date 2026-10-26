package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.exception.BookingException;
import com.yourname.studyroom.model.entity.Seat;
import com.yourname.studyroom.model.enums.ReleaseStatus;
import com.yourname.studyroom.web.admin.service.AttrValueService;
import com.yourname.studyroom.web.admin.service.BookingTermService;
import com.yourname.studyroom.web.admin.service.FacilityInfoService;
import com.yourname.studyroom.web.admin.service.LabelInfoService;
import com.yourname.studyroom.web.admin.service.PaymentTypeService;
import com.yourname.studyroom.web.admin.service.SeatService;
import com.yourname.studyroom.web.admin.service.StudyStoreService;
import com.yourname.studyroom.web.admin.vo.seat.SeatDetailVo;
import com.yourname.studyroom.web.admin.vo.seat.SeatQueryVo;
import com.yourname.studyroom.web.admin.vo.seat.SeatSubmitVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * 管理端座位管理：列表（可按门店过滤）、新增/编辑、发布状态、删除
 */
@Controller
@RequestMapping("/admin/seat")
public class AdminSeatController {

    private final SeatService seatService;
    private final StudyStoreService studyStoreService;
    private final LabelInfoService labelInfoService;
    private final PaymentTypeService paymentTypeService;
    private final BookingTermService bookingTermService;
    private final FacilityInfoService facilityInfoService;
    private final AttrValueService attrValueService;

    public AdminSeatController(SeatService seatService,
                               StudyStoreService studyStoreService,
                               LabelInfoService labelInfoService,
                               PaymentTypeService paymentTypeService,
                               BookingTermService bookingTermService,
                               FacilityInfoService facilityInfoService,
                               AttrValueService attrValueService) {
        this.seatService = seatService;
        this.studyStoreService = studyStoreService;
        this.labelInfoService = labelInfoService;
        this.paymentTypeService = paymentTypeService;
        this.bookingTermService = bookingTermService;
        this.facilityInfoService = facilityInfoService;
        this.attrValueService = attrValueService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long size,
                       @RequestParam(required = false) Long storeId,
                       Model model) {
        SeatQueryVo queryVo = new SeatQueryVo();
        queryVo.setStoreId(storeId);
        model.addAttribute("page", seatService.pageSeatItemByQuery(new Page<>(current, size), queryVo));
        model.addAttribute("storeId", storeId);
        model.addAttribute("stores", studyStoreService.list());
        return "admin/seat-list";
    }

    @GetMapping({"/form", "/form/{id}"})
    public String form(@PathVariable(required = false) Long id, Model model) {
        SeatSubmitVo seat = new SeatSubmitVo();
        List<Long> labelIds = List.of();
        List<Long> paymentTypeIds = List.of();
        List<Long> bookingTermIds = List.of();
        List<Long> facilityIds = List.of();
        List<Long> attrValueIds = List.of();
        if (id != null) {
            SeatDetailVo detail = seatService.getSeatDetailById(id);
            seat.setId(detail.getId());
            seat.setSeatCode(detail.getSeatCode());
            seat.setPrice(detail.getPrice());
            seat.setStoreId(detail.getStoreId());
            seat.setSeatType(detail.getSeatType());
            seat.setHasPower(detail.getHasPower());
            seat.setHasLight(detail.getHasLight());
            seat.setIsQuiet(detail.getIsQuiet());
            seat.setIsRelease(detail.getIsRelease());
            if (detail.getLabelInfoList() != null) {
                labelIds = detail.getLabelInfoList().stream().map(l -> l.getId()).toList();
            }
            if (detail.getPaymentTypeList() != null) {
                paymentTypeIds = detail.getPaymentTypeList().stream().map(p -> p.getId()).toList();
            }
            if (detail.getBookingTermList() != null) {
                bookingTermIds = detail.getBookingTermList().stream().map(t -> t.getId()).toList();
            }
            if (detail.getFacilityInfoList() != null) {
                facilityIds = detail.getFacilityInfoList().stream().map(f -> f.getId()).toList();
            }
            if (detail.getAttrValueVoList() != null) {
                attrValueIds = detail.getAttrValueVoList().stream().map(a -> a.getId()).toList();
            }
        }
        model.addAttribute("seat", seat);
        model.addAttribute("labelIds", labelIds);
        model.addAttribute("paymentTypeIds", paymentTypeIds);
        model.addAttribute("bookingTermIds", bookingTermIds);
        model.addAttribute("facilityIds", facilityIds);
        model.addAttribute("attrValueIds", attrValueIds);
        model.addAttribute("stores", studyStoreService.list());
        model.addAttribute("labels", labelInfoService.list());
        model.addAttribute("paymentTypes", paymentTypeService.list());
        model.addAttribute("bookingTerms", bookingTermService.list());
        model.addAttribute("facilities", facilityInfoService.list());
        model.addAttribute("attrValues", attrValueService.list());
        return "admin/seat-form";
    }

    @PostMapping("/save")
    public String save(@RequestParam(required = false) Long id,
                       @RequestParam String seatCode,
                       @RequestParam BigDecimal price,
                       @RequestParam Long storeId,
                       @RequestParam(required = false) String seatType,
                       @RequestParam(defaultValue = "false") boolean hasPower,
                       @RequestParam(defaultValue = "false") boolean hasLight,
                       @RequestParam(defaultValue = "false") boolean isQuiet,
                       @RequestParam(required = false) List<Long> labelIds,
                       @RequestParam(required = false) List<Long> paymentTypeIds,
                       @RequestParam(required = false) List<Long> bookingTermIds,
                       @RequestParam(required = false) List<Long> facilityIds,
                       @RequestParam(required = false) List<Long> attrValueIds,
                       RedirectAttributes ra) {
        SeatSubmitVo seat = new SeatSubmitVo();
        seat.setId(id);
        seat.setSeatCode(seatCode);
        seat.setPrice(price);
        seat.setStoreId(storeId);
        seat.setSeatType(seatType);
        seat.setHasPower(hasPower);
        seat.setHasLight(hasLight);
        seat.setIsQuiet(isQuiet);
        seat.setIsRelease(ReleaseStatus.RELEASED);
        seat.setLabelInfoIds(labelIds);
        seat.setPaymentTypeIds(paymentTypeIds);
        seat.setBookingTermIds(bookingTermIds);
        seat.setFacilityInfoIds(facilityIds);
        seat.setAttrValueIds(attrValueIds);
        seatService.saveOrUpdateSeat(seat);
        ra.addFlashAttribute("msg", "座位已保存");
        return "redirect:/admin/seat" + (storeId == null ? "" : "?storeId=" + storeId);
    }

    @PostMapping("/release/{id}")
    public String release(@PathVariable Long id,
                          @RequestParam(required = false) Long storeId,
                          @RequestParam boolean release,
                          RedirectAttributes ra) {
        Seat patch = new Seat();
        patch.setId(id);
        patch.setIsRelease(release ? ReleaseStatus.RELEASED : ReleaseStatus.NOT_RELEASED);
        seatService.updateById(patch);
        ra.addFlashAttribute("msg", release ? "座位已发布" : "座位已下架");
        return "redirect:/admin/seat" + (storeId == null ? "" : "?storeId=" + storeId);
    }

    /**
     * 删除确认页（无 JS 架构下的防误删中转）
     */
    @GetMapping("/delete/{id}/confirm")
    public String deleteConfirm(@PathVariable Long id,
                                @RequestParam(required = false) Long storeId,
                                Model model) {
        Seat seat = seatService.getById(id);
        model.addAttribute("confirmTitle", "删除座位");
        model.addAttribute("confirmMessage", seat == null
                ? "该座位不存在，可能已被删除。"
                : "确认删除座位「" + seat.getSeatCode() + "」？座位的标签、设施、支付方式等关联将一并清除。");
        model.addAttribute("confirmAction", "/admin/seat/delete/" + id + (storeId == null ? "" : "?storeId=" + storeId));
        model.addAttribute("confirmButtonText", "确认删除座位");
        model.addAttribute("cancelUrl", "/admin/seat" + (storeId == null ? "" : "?storeId=" + storeId));
        return "confirm";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         @RequestParam(required = false) Long storeId,
                         RedirectAttributes ra) {
        try {
            seatService.removeSeatById(id);
            ra.addFlashAttribute("msg", "座位已删除");
        } catch (BookingException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/seat" + (storeId == null ? "" : "?storeId=" + storeId);
    }
}
