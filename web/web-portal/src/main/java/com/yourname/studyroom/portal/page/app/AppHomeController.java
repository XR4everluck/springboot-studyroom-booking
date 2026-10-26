package com.yourname.studyroom.portal.page.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.web.admin.vo.store.StoreItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户端首页：门店分页列表（复用后台分页查询）与热门门店（带 Redis 缓存）
 */
@Controller
public class AppHomeController {

    private final com.yourname.studyroom.web.admin.service.StudyStoreService adminStudyStoreService;
    private final com.yourname.studyroom.web.app.service.StudyStoreService appStudyStoreService;

    public AppHomeController(com.yourname.studyroom.web.admin.service.StudyStoreService adminStudyStoreService,
                             com.yourname.studyroom.web.app.service.StudyStoreService appStudyStoreService) {
        this.adminStudyStoreService = adminStudyStoreService;
        this.appStudyStoreService = appStudyStoreService;
    }

    @GetMapping("/app/home")
    public String home(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "6") long size,
                       Model model) {
        Page<StoreItemVo> page = new Page<>(current, size);
        model.addAttribute("page", adminStudyStoreService.pageItem(page,
                new com.yourname.studyroom.web.admin.vo.store.StoreQueryVo()));
        List<com.yourname.studyroom.web.app.vo.store.StoreItemVo> hotList =
                appStudyStoreService.getHotList(4);
        model.addAttribute("hotStores", hotList);
        return "app/home";
    }
}
