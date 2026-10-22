package com.yourname.studyroom.web.app.controller.seat;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.web.app.service.SeatService;
import com.yourname.studyroom.web.app.vo.seat.SeatDetailVo;
import com.yourname.studyroom.web.app.vo.seat.SeatItemVo;
import com.yourname.studyroom.web.app.vo.seat.SeatQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "座位信息")
@RestController
@RequestMapping("/app/seat")
public class SeatController {

    @Autowired
    SeatService seatService;

    /**
     * 分页查询座位列表
     *
     * @param current 当前页码
     * @param size    每页显示条数
     * @param queryVo  座位查询条件对象
     * @return 包含分页结果和座位列表的Result对象
     */
    @Operation(summary = "分页查询座位列表")
    @GetMapping("pageItem")
    public Result<IPage<SeatItemVo>> pageItem(@RequestParam long current, @RequestParam long size, SeatQueryVo queryVo) {
        IPage<SeatItemVo> page = new Page<>(current, size);
        IPage<SeatItemVo> result = seatService.pageItem(page,queryVo);
        return Result.ok(result);
    }

    /**
     * 根据ID获取座位的详细信息
     *
     * @param id 座位ID
     * @return 包含座位详细信息的Result对象
     */
    @Operation(summary = "根据id获取座位的详细信息")
    @GetMapping("getDetailById")
    public Result<SeatDetailVo> getDetailById(@RequestParam Long id) {
        SeatDetailVo result = seatService.getDetailById(id);
        return Result.ok(result);
    }

    /**
     * 根据自习室门店ID分页查询座位列表
     *
     * @param current 当前页码
     * @param size    每页显示条数
     * @param id      自习室门店ID
     * @return 包含分页结果和座位列表的Result对象
     */
    @Operation(summary = "根据自习室门店id分页查询座位列表")
    @GetMapping("pageItemByStoreId")
    public Result<IPage<SeatItemVo>> pageItemByStoreId(@RequestParam long current, @RequestParam long size, @RequestParam Long id) {
        // 原先用 new Page<>() 丢掉入参，分页会退化成固定第一页 10 条，
        // 门店下座位超过 10 个时列表会被静默截断
        IPage<SeatItemVo> page = new Page<>(current, size);
        IPage<SeatItemVo> result = seatService.pageItemByStoreId(page,id);
        return Result.ok(result);
    }

    @Operation(summary = "首页热门座位列表")
    @GetMapping("getHotList")
    public Result<List<SeatItemVo>> getHotList(@RequestParam(defaultValue = "6") Integer limit) {
        List<SeatItemVo> result = seatService.getHotList(limit);
        return Result.ok(result);
    }
}
