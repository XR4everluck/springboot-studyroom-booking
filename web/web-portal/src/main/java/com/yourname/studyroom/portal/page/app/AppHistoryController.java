package com.yourname.studyroom.portal.page.app;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.login.LoginUserHolder;
import com.yourname.studyroom.web.app.service.BrowsingHistoryService;
import com.yourname.studyroom.web.app.vo.history.HistoryItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 用户端浏览历史
 */
@Controller
public class AppHistoryController {

    private final BrowsingHistoryService appBrowsingHistoryService;

    public AppHistoryController(BrowsingHistoryService appBrowsingHistoryService) {
        this.appBrowsingHistoryService = appBrowsingHistoryService;
    }

    @GetMapping("/app/history")
    public String list(@RequestParam(defaultValue = "1") long current,
                       @RequestParam(defaultValue = "10") long size,
                       Model model) {
        Long userId = LoginUserHolder.getLoginUser().getUserId();
        Page<HistoryItemVo> page = new Page<>(current, size);
        model.addAttribute("page", appBrowsingHistoryService.pageItemByUserId(page, userId));
        return "app/history";
    }
}
