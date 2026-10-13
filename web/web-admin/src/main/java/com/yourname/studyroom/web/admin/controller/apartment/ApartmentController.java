package com.yourname.studyroom.web.admin.controller.apartment;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.enums.ReleaseStatus;
import com.yourname.studyroom.web.admin.service.StudyStoreService;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentDetailVo;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentItemVo;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentQueryVo;
import com.yourname.studyroom.web.admin.vo.apartment.ApartmentSubmitVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@Tag(name = "自习室门店信息管理")
@RestController
@RequestMapping("/admin/apartment")
public class ApartmentController {

    @Autowired
    StudyStoreService studyStoreService;
    /**
     * 保存或更新自习室门店信息
     * <p/>
     * 根据前端传递的自习室门店提交信息（ApartmentSubmitVo），调用自习室门店信息服务（StudyStoreService）中的保存或更新方法，
     * 更新或新增自习室门店信息。操作成功后，返回一个成功的Result对象。
     *
     * @param apartmentSubmitVo 自习室门店提交信息对象，包含自习室门店的详细信息
     * @return Result 包含操作结果的Result对象，操作成功则返回状态码为200的Result对象
     */
    @Operation(summary = "保存或更新自习室门店信息")
    @PostMapping("saveOrUpdate")
    public Result saveOrUpdate(@RequestBody ApartmentSubmitVo apartmentSubmitVo) {
        studyStoreService.saveOrUpdateApartment(apartmentSubmitVo);
        return Result.ok();
    }

    /**
     * 根据条件分页查询自习室门店列表
     *
     * @param current 当前页码
     * @param size    每页显示的记录数
     * @param queryVo 查询条件对象
     * @return 包含分页信息的自习室门店列表
     */
    @Operation(summary = "根据条件分页查询自习室门店列表")
    @GetMapping("pageItem")
    public Result<IPage<ApartmentItemVo>> pageItem(@RequestParam long current, @RequestParam long size, ApartmentQueryVo queryVo) {
        Page<ApartmentItemVo> page = new Page<>(current,size);
        IPage<ApartmentItemVo> result = studyStoreService.pageItem(page,queryVo);
        return Result.ok(result);
    }

    /**
     * 根据ID获取自习室门店详细信息
     *
     * @param id 自习室门店ID
     * @return 包含自习室门店详细信息的Result对象
     */
    @Operation(summary = "根据ID获取自习室门店详细信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo result = studyStoreService.getDetailById(id);
        return Result.ok(result);
    }

    /**
     * 根据id删除自习室门店信息
     *
     * @param id 自习室门店ID
     * @return 删除操作的结果
     */
    @Operation(summary = "根据id删除自习室门店信息")
    @DeleteMapping("removeById")
    public Result removeById(@RequestParam Long id) {
        studyStoreService.removeApartmentById(id);
        return Result.ok();
    }

    /**
     * 根据id修改自习室门店发布状态
     *
     * @param id    自习室门店的ID
     * @param status 自习室门店的发布状态
     * @return 返回操作结果
     */
    @Operation(summary = "根据id修改自习室门店发布状态")
    @PostMapping("updateReleaseStatusById")
    public Result updateReleaseStatusById(@RequestParam Long id, @RequestParam ReleaseStatus status) {
        LambdaUpdateWrapper<StudyStore> studyStoreUpdateWrapper = new LambdaUpdateWrapper<>();
        studyStoreUpdateWrapper.eq(StudyStore::getId,id);
        studyStoreUpdateWrapper.set(StudyStore::getIsRelease,status);
        studyStoreService.update(studyStoreUpdateWrapper);
        return Result.ok();
    }

    /**
     * 根据区县id查询自习室门店信息列表
     *
     * @param id 区县ID
     * @return 包含自习室门店信息列表的Result对象
     */
    @Operation(summary = "根据区县id查询自习室门店信息列表")
    @GetMapping("listInfoByDistrictId")
    public Result<List<StudyStore>> listInfoByDistrictId(@RequestParam Long id) {
        LambdaQueryWrapper<StudyStore> studyStoreQueryWrapper = new LambdaQueryWrapper<>();
        studyStoreQueryWrapper.eq(StudyStore::getDistrictId,id);
        List<StudyStore> list = studyStoreService.list(studyStoreQueryWrapper);
        return Result.ok(list);
    }
}