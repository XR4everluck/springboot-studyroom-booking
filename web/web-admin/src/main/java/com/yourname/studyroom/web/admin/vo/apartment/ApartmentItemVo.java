package com.yourname.studyroom.web.admin.vo.apartment;

import com.yourname.studyroom.model.entity.StudyStore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(description = "后台管理系统自习室门店列表实体")
public class ApartmentItemVo extends StudyStore {

    @Schema(description = "房间总数")
    private Long totalRoomCount;

    @Schema(description = "空闲房间数")
    private Long freeRoomCount;

}
