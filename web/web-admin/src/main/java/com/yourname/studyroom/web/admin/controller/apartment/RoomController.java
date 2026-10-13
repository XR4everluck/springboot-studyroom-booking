package com.yourname.studyroom.web.admin.controller.apartment;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.Seat;
import com.yourname.studyroom.model.enums.ReleaseStatus;
import com.yourname.studyroom.web.admin.service.SeatService;
import com.yourname.studyroom.web.admin.vo.room.RoomDetailVo;
import com.yourname.studyroom.web.admin.vo.room.RoomItemVo;
import com.yourname.studyroom.web.admin.vo.room.RoomQueryVo;
import com.yourname.studyroom.web.admin.vo.room.RoomSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "房间信息管理")
@RestController
@RequestMapping("/admin/room")
public class RoomController {

    @Autowired
    SeatService seatService;

    /**
     * 保存或更新房间信息
     *
     * @param roomSubmitVo 房间信息对象
     * @return 操作结果
     */
    @Operation(summary = "保存或更新房间信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody RoomSubmitVo roomSubmitVo) {
        seatService.saveOrUpdateRoom(roomSubmitVo);
        return Result.ok();
    }

    /**
     * 根据条件分页查询房间列表
     *
     * @param current 当前页码
     * @param size    每页显示的条数
     * @param queryVo 查询条件对象
     * @return 包含房间列表的Result对象
     */
    @Operation(summary = "根据条件分页查询房间列表")
    @GetMapping("pageItem")
    public Result<IPage<RoomItemVo>> pageItem(@RequestParam long current, @RequestParam long size, RoomQueryVo queryVo) {
        IPage<RoomItemVo> page = new Page<>(current, size);
        IPage<RoomItemVo> result = seatService.pageRoomItemByQuery(page, queryVo);
        return Result.ok(result);
    }

    /**
     * 根据id获取房间详细信息
     *
     * @param id 房间ID
     * @return 包含房间详细信息的Result对象
     */
    @Operation(summary = "根据id获取房间详细信息")
    @GetMapping("getDetailById")
    public Result<RoomDetailVo> getDetailById(@RequestParam Long id) {
        RoomDetailVo seat = seatService.getRoomDetailById(id);
        return Result.ok(seat);
    }

    /**
     * 根据id删除房间信息
     *
     * @param id 房间ID
     * @return 删除操作的结果，成功返回Result.ok()
     */
    @Operation(summary = "根据id删除房间信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        seatService.removeRoomById(id);
        return Result.ok();
    }

    /**
     * 根据id修改房间发布状态
     *
     * @param id 房间的ID
     * @param status 房间的发布状态，可以是发布或未发布
     * @return 修改操作的结果，成功返回Result.ok()
     */
    @Operation(summary = "根据id修改房间发布状态")
    @PostMapping("updateReleaseStatusById")
    public Result updateReleaseStatusById(Long id, ReleaseStatus status) {
        LambdaUpdateWrapper<Seat> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Seat::getId, id);
        updateWrapper.set(Seat::getIsRelease, status);
        seatService.update(updateWrapper);
        return Result.ok();
    }

    /**
     * 根据自习室门店id查询房间列表
     *
     * @param id 自习室门店的ID
     * @return 包含查询到的房间列表的Result对象
     */
    @GetMapping("listBasicByApartmentId")
    @Operation(summary = "根据自习室门店id查询房间列表")
    public Result<List<Seat>> listBasicByApartmentId(Long id) {
        LambdaQueryWrapper<Seat> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Seat::getStoreId, id);
        queryWrapper.eq(Seat::getIsRelease, ReleaseStatus.RELEASED);
        List<Seat> seatList = seatService.list(queryWrapper);
        return Result.ok(seatList);
    }

}


















