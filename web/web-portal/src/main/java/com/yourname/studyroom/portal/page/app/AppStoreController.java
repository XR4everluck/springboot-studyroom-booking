package com.yourname.studyroom.portal.page.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.web.app.service.SeatService;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import com.yourname.studyroom.web.app.vo.seat.SeatItemVo;
import com.yourname.studyroom.web.app.vo.store.StoreDetailVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户端门店详情：门店信息 + 座位分页列表
 */
@Controller
public class AppStoreController {

    private final StudyStoreService appStudyStoreService;
    private final SeatService appSeatService;

    public AppStoreController(StudyStoreService appStudyStoreService, SeatService appSeatService) {
        this.appStudyStoreService = appStudyStoreService;
        this.appSeatService = appSeatService;
    }

    @GetMapping("/app/store/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(defaultValue = "1") long current,
                         @RequestParam(defaultValue = "8") long size,
                         Model model) {
        StoreDetailVo store = appStudyStoreService.getDetailById(id);
        if (store == null) {
            return "redirect:/app/home";
        }
        Page<SeatItemVo> page = new Page<>(current, size);
        model.addAttribute("store", store);
        model.addAttribute("page", appSeatService.pageItemByStoreId(page, id));
        return "app/store-detail";
    }
}
