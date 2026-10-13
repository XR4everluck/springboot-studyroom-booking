package com.yourname.studyroom.web.admin.vo.seat;

import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.entity.Seat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;


@Data
@Schema(description = "座位信息")
public class SeatItemVo extends Seat {

    @Schema(description = "预订订单结束日期")
    private Date bookingEndDate;

    @Schema(description = "当前签到状态")
    private Boolean isCheckIn;

    @Schema(description = "所属自习室门店信息")
    private StudyStore studyStore;

}
