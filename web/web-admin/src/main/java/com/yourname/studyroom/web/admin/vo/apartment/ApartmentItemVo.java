package com.yourname.studyroom.web.admin.vo.apartment;

import com.yourname.studyroom.model.entity.StudyStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "后台管理系统自习室门店列表实体")
public class ApartmentItemVo extends StudyStore {

    @Schema(description = "座位总数")
    private Long totalSeatCount;

    @Schema(description = "空闲座位数")
    private Long freeSeatCount;

}
