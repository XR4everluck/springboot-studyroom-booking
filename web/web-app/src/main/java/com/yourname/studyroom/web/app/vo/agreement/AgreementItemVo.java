package com.yourname.studyroom.web.app.vo.agreement;

import com.yourname.studyroom.model.enums.LeaseSourceType;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@Schema(description = "预订订单基本信息")
public class AgreementItemVo {

    @Schema(description = "预订订单id")
    private Long id;

    @Schema(description = "座位图片列表")
    private List<GraphVo> seatGraphVoList;

    @Schema(description = "自习室门店名称")
    private String storeName;

    @Schema(description = "座位号")
    private String seatCode;

    @Schema(description = "预订订单状态")
    private BookingStatus leaseStatus;

    @Schema(description = "预订订单开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaseStartDate;

    @Schema(description = "预订订单结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date leaseEndDate;

    @Schema(description = "预订订单来源")
    private LeaseSourceType sourceType;

    @Schema(description = "座位单价")
    private BigDecimal rent;
    
}
