package com.yourname.studyroom.web.app.mapper;

import com.yourname.studyroom.model.entity.Seat;
import com.yourname.studyroom.web.app.vo.seat.SeatItemVo;
import com.yourname.studyroom.web.app.vo.seat.SeatQueryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * 针对表【seat(座位信息表)】的数据库操作Mapper
 */
public interface AppSeatMapper extends BaseMapper<Seat> {

    IPage<SeatItemVo> pageItem(IPage<SeatItemVo> page, SeatQueryVo queryVo);

    BigDecimal selectMinPriceByStoreId(Long id);

    IPage<SeatItemVo> pageItemByStoreId(IPage<SeatItemVo> page, Long id);

    SeatItemVo selectItemById(Long id);

    /**
     * 按浏览量降序返回已上架座位 id（首页热门座位）
     */
    @Select("select ri.id " +
            "from seat ri " +
            "join browsing_history bh on bh.seat_id = ri.id and bh.is_deleted = 0 " +
            "where ri.is_deleted = 0 and ri.is_release = 1 " +
            "group by ri.id " +
            "order by count(bh.id) desc " +
            "limit #{limit}")
    List<Long> selectHotSeatIds(Integer limit);
}