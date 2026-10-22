package com.yourname.studyroom.web.app.vo.order;

import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.web.app.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "预订订单详细信息")
public class OrderDetailVo extends BookingOrder {

    @Schema(description = "自习室门店名称")
    private String storeName;

    @Schema(description = "自习室门店图片列表")
    private List<GraphVo> storeGraphVoList;

    @Schema(description = "座位号")
    private String seatCode;

    @Schema(description = "座位图片列表")
    private List<GraphVo> seatGraphVoList;

    @Schema(description = "支付方式")
    private String paymentTypeName;

    @Schema(description = "时长月数")
    private Integer bookingTermMonthCount;

    @Schema(description = "时长单位")
    private String bookingTermUnit;

}