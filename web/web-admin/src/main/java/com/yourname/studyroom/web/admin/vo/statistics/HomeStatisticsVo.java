package com.yourname.studyroom.web.admin.vo.statistics;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description 首页数据看板统计信息
 */
@Data
public class HomeStatisticsVo {

    /** 今日新增订单数 */
    private Long todayOrders;

    /** 今日营收（不含已取消订单） */
    private BigDecimal todayRevenue;

    /** 今日到店体验预约数 */
    private Long todayAppointments;

    /** 有效会员卡数量 */
    private Long memberCardCount;

    /** 待处理订单数（待确认/签退待确认/续期待确认） */
    private Long pendingOrders;

    /** 近 7 日趋势 */
    private List<TrendPoint> weekTrend;

    @Data
    public static class TrendPoint {

        /** 日期，格式 MM-dd */
        private String date;

        /** 当日订单量 */
        private Long orderCount;

        /** 当日营收 */
        private BigDecimal revenue;
    }
}
