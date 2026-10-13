package com.yourname.studyroom.web.admin.mapper;

import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.web.admin.vo.order.OrderQueryVo;
import com.yourname.studyroom.web.admin.vo.order.OrderVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

/**
* @author liubo
* @description 针对表【booking_order(预订订单信息表)】的数据库操作Mapper
* @createDate 2023-07-24 15:48:00
* @Entity com.yourname.studyroom.model.BookingOrder
*/
public interface BookingOrderMapper extends BaseMapper<BookingOrder> {

    IPage<OrderVo> pageOrderByQuery(IPage<OrderVo> page, OrderQueryVo queryVo);

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




