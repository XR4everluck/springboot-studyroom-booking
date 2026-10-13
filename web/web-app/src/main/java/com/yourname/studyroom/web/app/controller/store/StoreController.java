package com.yourname.studyroom.web.app.controller.store;

import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import com.yourname.studyroom.web.app.vo.store.StoreDetailVo;
import com.yourname.studyroom.web.app.vo.store.StoreItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "自习室门店信息")
@RequestMapping("/app/store")
public class StoreController {

    @Autowired
    StudyStoreService studyStoreService;

    @Operation(summary = "根据id获取自习室门店信息")
    @GetMapping("getDetailById")
    public Result<StoreDetailVo> getDetailById(@RequestParam Long id) {
        StoreDetailVo result = studyStoreService.getDetailById(id);
        return Result.ok(result);
    }

    @Operation(summary = "首页热门门店列表")
    @GetMapping("getHotList")
    public Result<List<StoreItemVo>> getHotList(@RequestParam(defaultValue = "6") Integer limit) {
        List<StoreItemVo> result = studyStoreService.getHotList(limit);
        return Result.ok(result);
    }
}
