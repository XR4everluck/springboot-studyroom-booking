package com.yourname.studyroom.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import com.yourname.studyroom.model.enums.BookingSourceType;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Schema(description = "预订订单信息表")
@TableName(value = "booking_order")
@Data
public class BookingOrder extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "订单编号")
    @TableField(value = "order_no")
    private String orderNo;

    @Schema(description = "预订用户id")
    @TableField(value = "user_id")
    private Long userId;

    @Schema(description = "联系人手机号码")
    @TableField(value = "phone")
    private String phone;

    @Schema(description = "联系人姓名")
    @TableField(value = "name")
    private String name;

    @Schema(description = "联系人身份证号码")
    @TableField(value = "identification_number")
    private String identificationNumber;

    @Schema(description = "自习室门店id")
    @TableField(value = "store_id")
    private Long storeId;

    @Schema(description = "座位id")
    @TableField(value = "seat_id")
    private Long seatId;

    @Schema(description = "预订开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "start_date")
    private Date startDate;

    @Schema(description = "预订结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "end_date")
    private Date endDate;

    @Schema(description = "预订类型：小时卡/日卡/月卡")
    @TableField(value = "booking_type")
    private String bookingType;

    @Schema(description = "签到时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "check_in_time")
    private Date checkInTime;

    @Schema(description = "签退时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "check_out_time")
    private Date checkOutTime;

    @Schema(description = "预订时长id")
    @TableField(value = "booking_term_id")
    private Long bookingTermId;

    @Schema(description = "订单总金额（元）")
    @TableField(value = "total_amount")
    private BigDecimal totalAmount;

    @Schema(description = "支付类型id")
    @TableField(value = "payment_type_id")
    private Long paymentTypeId;

    @Schema(description = "抵扣所用会员卡id")
    @TableField(value = "member_card_id")
    private Long memberCardId;

    @Schema(description = "预订状态")
    @TableField(value = "status")
    private BookingStatus status;

    @Schema(description = "订单来源")
    @TableField(value = "source_type")
    private BookingSourceType sourceType;

    @Schema(description = "备注信息")
    @TableField(value = "additional_info")
    private String additionalInfo;

}
