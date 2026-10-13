package com.yourname.studyroom.web.admin.controller.apartment;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.StoreLabel;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.web.admin.service.StoreLabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "标签管理")
@RestController
@RequestMapping("/admin/label")
public class LabelController {

    @Autowired
    StoreLabelService storeLabelService;

    /**
     * （根据类型）查询标签列表
     *
     * @param type
     * @return Result<List < StoreLabel>>
     */
    @Operation(summary = "（根据类型）查询标签列表")
    @GetMapping("list")
    public Result<List<StoreLabel>> labelList(@RequestParam(required = false) ItemType type) {
        System.out.println(type);
        LambdaQueryWrapper<StoreLabel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type != null, StoreLabel::getType, type);
        List<StoreLabel> labelInfos = storeLabelService.list(queryWrapper);
        return Result.ok(labelInfos);
    }

    /**
     * 新增或修改标签信息
     *
     * @param storeLabel
     * @return Result
     */
    @Operation(summary = "新增或修改标签信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdateLabel(@RequestBody StoreLabel storeLabel) {
        storeLabelService.saveOrUpdate(storeLabel);
        return Result.ok();
    }

    /**
     * 根据id删除标签信息
     *
     * @param id
     * @return Result
     */
    @Operation(summary = "根据id删除标签信息")
    @DeleteMapping("deleteById")
    public Result deleteLabelById(@RequestParam Long id) {
        storeLabelService.removeById(id);
        return Result.ok();
    }
}
