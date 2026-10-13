package com.yourname.studyroom.web.app.controller.apartment;

import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.web.app.service.StudyStoreService;
import com.yourname.studyroom.web.app.vo.apartment.ApartmentDetailVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "自习室门店信息")
@RequestMapping("/app/apartment")
public class ApartmentController {

    @Autowired
    StudyStoreService studyStoreService;

    @Operation(summary = "根据id获取自习室门店信息")
    @GetMapping("getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        ApartmentDetailVo result = studyStoreService.getDetailById(id);
        return Result.ok(result);
    }
}
