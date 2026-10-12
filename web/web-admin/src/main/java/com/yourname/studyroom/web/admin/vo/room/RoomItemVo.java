package com.yourname.studyroom.web.admin.vo.room;

import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.entity.Seat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


@Data
@Schema(description = "房间信息")
public class RoomItemVo extends Seat {

    @Schema(description = "租约结束日期")
    private Date leaseEndDate;

    @Schema(description = "当前入住状态")
    private Boolean isCheckIn;

    @Schema(description = "所属自习室门店信息")
    private StudyStore apartmentInfo;

}
