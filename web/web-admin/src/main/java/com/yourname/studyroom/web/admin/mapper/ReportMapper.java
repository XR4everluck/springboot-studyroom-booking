package com.yourname.studyroom.web.admin.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 运营报表聚合查询（报表中心与演示大屏共用）
 */
public interface ReportMapper {

    /** 按日汇总营收与订单量（不含已取消），from 为 yyyy-MM-dd */
    @Select("select date_format(create_time, '%Y-%m-%d') d, count(*) cnt, " +
            "ifnull(sum(total_amount), 0) revenue " +
            "from booking_order " +
            "where is_deleted = 0 and status != 3 and create_time >= #{from} " +
            "group by date_format(create_time, '%Y-%m-%d') order by d")
    List<Map<String, Object>> selectDailyRevenue(@Param("from") String from);

    /** 按日汇总体验预约数 */
    @Select("select date_format(appointment_time, '%Y-%m-%d') d, count(*) cnt " +
            "from visit_appointment " +
            "where is_deleted = 0 and appointment_time >= #{from} " +
            "group by date_format(appointment_time, '%Y-%m-%d') order by d")
    List<Map<String, Object>> selectDailyAppointments(@Param("from") String from);

    /** 门店营收排行（营收、订单量，不含已取消） */
    @Select("select ai.id storeId, ai.name storeName, count(*) orderCount, " +
            "ifnull(sum(bo.total_amount), 0) revenue " +
            "from booking_order bo " +
            "join study_store ai on ai.id = bo.store_id and ai.is_deleted = 0 " +
            "where bo.is_deleted = 0 and bo.status != 3 and bo.create_time >= #{from} " +
            "group by ai.id, ai.name order by revenue desc")
    List<Map<String, Object>> selectStoreRevenue(@Param("from") String from);

    /** 会员卡汇总：有效卡数与充值总额 */
    @Select("select " +
            "(select count(*) from member_card where is_deleted = 0 and status = 1) cardCount, " +
            "(select ifnull(sum(amount), 0) from card_recharge_record where is_deleted = 0) rechargeAmount, " +
            "(select count(*) from user_info where is_deleted = 0) userCount")
    Map<String, Object> selectMemberSummary();

    /** 订单流水（CSV 导出数据源，含门店/座位名） */
    @Select("select bo.order_no orderNo, ai.name storeName, ri.seat_code seatCode, " +
            "bo.name userName, bo.phone, bo.status status, " +
            "bo.start_date startDate, bo.end_date endDate, bo.total_amount totalAmount, " +
            "bo.create_time createTime " +
            "from booking_order bo " +
            "left join study_store ai on ai.id = bo.store_id and ai.is_deleted = 0 " +
            "left join seat ri on ri.id = bo.seat_id and ri.is_deleted = 0 " +
            "where bo.is_deleted = 0 and bo.create_time >= #{from} " +
            "order by bo.create_time desc")
    List<Map<String, Object>> selectOrderFlow(@Param("from") String from);
}
