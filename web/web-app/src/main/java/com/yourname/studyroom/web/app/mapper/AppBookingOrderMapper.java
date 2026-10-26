package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.web.app.vo.order.OrderItemVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
* @author liubo
* @description 针对表【booking_order(预订订单信息表)】的数据库操作Mapper
* @createDate 2023-07-26 11:12:39
* @Entity com.yourname.studyroom.model.entity.BookingOrder
*/
public interface AppBookingOrderMapper extends BaseMapper<BookingOrder> {

    List<OrderItemVo> listItemByUserId(Long userId);

    /**
     * 统计同一座位在指定时段内的有效预订冲突数量
     *
     * @param seatId    座位id
     * @param startDate 预订开始时间
     * @param endDate   预订结束时间
     * @param excludeId 需要排除的订单id（修改场景传当前订单id，新建传 null）
     * @return 冲突订单数量
     */
    int countTimeConflict(@Param("seatId") Long seatId,
                          @Param("startDate") Date startDate,
                          @Param("endDate") Date endDate,
                          @Param("excludeId") Long excludeId);
}
