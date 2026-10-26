package com.yourname.studyroom.web.admin.vo.seat;

import lombok.Data;

/**
 * 座位使用热力统计行（运营台热力视图数据源）
 */
@Data
public class SeatUsageStatVo {

    private Long seatId;

    private String seatCode;

    private Long storeId;

    private String storeName;

    /** 累计订单数（不含已取消） */
    private Integer orderCount;

    /** 累计预订小时数 */
    private Integer totalHours;
}
