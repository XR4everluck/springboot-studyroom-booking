package com.yourname.studyroom.web.app.controller.leasaterm;

import com.yourname.studyroom.common.result.Result;
import com.yourname.studyroom.model.entity.LeaseTerm;
import com.yourname.studyroom.web.app.mapper.LeaseTermMapper;
import com.yourname.studyroom.web.app.service.LeaseTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/app/term/")
@Tag(name = "租期信息")
public class LeaseTermController {

    @Autowired
    LeaseTermService leaseTermService;

    /**
     * 根据座位id获取可选租期列表
     *
     * @param id 座位id
     * @return 包含可选租期列表的响应结果
     */
    @GetMapping("listByRoomId")
    @Operation(summary = "根据座位id获取可选获取租期列表")
    public Result<List<LeaseTerm>> list(@RequestParam Long id) {
        List<LeaseTerm> result = leaseTermService.listByRoomId(id);
        return Result.ok(result);
    }
}
