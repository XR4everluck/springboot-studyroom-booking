package com.yourname.studyroom.web.admin.controller.apartment;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.SeatFacility;
import com.yourname.studyroom.model.enums.ItemType;
import com.yourname.studyroom.web.admin.service.SeatFacilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "配套管理")
@RestController
@RequestMapping("/admin/facility")
public class FacilityController {

    @Autowired
    SeatFacilityService seatFacilityService;

    /**
     * [根据类型]查询配套信息列表
     *
     * @param type
     * @return Result<List<SeatFacility>>
     */
    @Operation(summary = "[根据类型]查询配套信息列表")
    @GetMapping("list")
    public Result<List<SeatFacility>> listFacility(@RequestParam(required = false) ItemType type) {
        LambdaQueryWrapper<SeatFacility> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type != null, SeatFacility::getType, type);
        List<SeatFacility> facilityInfos = seatFacilityService.list(queryWrapper);
        return Result.ok(facilityInfos);
    }

    /**
     * 新增或修改配套信息
     *
     * @param seatFacility
     * @return Result
     */
    @Operation(summary = "新增或修改配套信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody SeatFacility seatFacility) {
        seatFacilityService.saveOrUpdate(seatFacility);
        return Result.ok();
    }

    /**
     * 根据id删除配套信息
     *
     * @param id
     * @return Result
     */
    @Operation(summary = "根据id删除配套信息")
    @DeleteMapping("deleteById")
    public Result removeFacilityById(@RequestParam Long id) {
        seatFacilityService.removeById(id);
        return Result.ok();
    }

}
