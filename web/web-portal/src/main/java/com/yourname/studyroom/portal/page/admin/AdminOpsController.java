package com.yourname.studyroom.portal.page.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yourname.studyroom.common.constant.RedisConstant;
import com.yourname.studyroom.model.entity.BookingOrder;
import com.yourname.studyroom.model.entity.Seat;
import com.yourname.studyroom.model.entity.StudyStore;
import com.yourname.studyroom.model.enums.BookingStatus;
import com.yourname.studyroom.web.admin.mapper.BookingOrderMapper;
import com.yourname.studyroom.web.admin.service.SeatService;
import com.yourname.studyroom.web.admin.service.StudyStoreService;
import com.yourname.studyroom.web.admin.vo.seat.SeatUsageStatVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 运营台：同一张座位网格的两个视图——
 * 实时状态（Redis 缓存的空闲/已预订/使用中）与热力分析（历史订单聚合的座位冷热）。
 */
@Controller
public class AdminOpsController {

    private final StudyStoreService studyStoreService;
    private final SeatService seatService;
    private final BookingOrderMapper bookingOrderMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    public AdminOpsController(StudyStoreService studyStoreService,
                              SeatService seatService,
                              BookingOrderMapper bookingOrderMapper,
                              RedisTemplate<String, Object> redisTemplate) {
        this.studyStoreService = studyStoreService;
        this.seatService = seatService;
        this.bookingOrderMapper = bookingOrderMapper;
        this.redisTemplate = redisTemplate;
    }

    /** 座位单元格（两个视图共用的网格骨架） */
    public static class SeatCell {
        private Long seatId;
        private String seatCode;
        /** 实时状态 code：1 空闲 2 已预订 3 使用中；null 视为空闲 */
        private Integer realtime;
        private int orderCount;
        private int totalHours;
        /** 热力档位 0~5，按门店内相对时长分档 */
        private int heatTier;

        public Long getSeatId() {
            return seatId;
        }

        public String getSeatCode() {
            return seatCode;
        }

        public Integer getRealtime() {
            return realtime;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public int getTotalHours() {
            return totalHours;
        }

        public int getHeatTier() {
            return heatTier;
        }
    }

    /** 门店运营视图 */
    public static class StoreOps {
        private StudyStore store;
        private List<SeatCell> seats = new ArrayList<>();
        private int free;
        private int booked;
        private int inUse;
        private int totalOrders;
        private int totalHours;

        public StudyStore getStore() {
            return store;
        }

        public List<SeatCell> getSeats() {
            return seats;
        }

        public int getFree() {
            return free;
        }

        public int getBooked() {
            return booked;
        }

        public int getInUse() {
            return inUse;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public int getTotalHours() {
            return totalHours;
        }
    }

    @GetMapping("/admin/ops")
    public String ops(@RequestParam(defaultValue = "live") String view, Model model) {
        boolean heatView = "heat".equals(view);

        // 全部门店（已发布），座位按门店聚合
        LambdaQueryWrapper<StudyStore> storeWrapper = new LambdaQueryWrapper<>();
        storeWrapper.eq(StudyStore::getIsRelease, com.yourname.studyroom.model.enums.ReleaseStatus.RELEASED);
        storeWrapper.orderByAsc(StudyStore::getId);
        List<StudyStore> stores = studyStoreService.list(storeWrapper);

        // 座位热力统计（一次聚合查询，按 seatId 索引）
        Map<Long, SeatUsageStatVo> usageBySeat = new HashMap<>();
        for (SeatUsageStatVo stat : bookingOrderMapper.selectSeatUsage()) {
            usageBySeat.put(stat.getSeatId(), stat);
        }

        List<StoreOps> storeOpsList = new ArrayList<>();
        // 实时兜底：Redis 缓存可能因重启/TTL 丢失，从数据库有效订单（已预订/待确认/使用中）推导真实占用
        LambdaQueryWrapper<BookingOrder> activeWrapper = new LambdaQueryWrapper<>();
        activeWrapper.in(BookingOrder::getStatus, BookingStatus.BOOKED, BookingStatus.CONFIRM_PENDING,
                BookingStatus.CHECKOUT_PENDING, BookingStatus.RENEW_PENDING, BookingStatus.IN_USE);
        activeWrapper.select(BookingOrder::getSeatId, BookingOrder::getStatus);
        Map<Long, Integer> dbStatusBySeat = new HashMap<>();
        for (BookingOrder order : bookingOrderMapper.selectList(activeWrapper)) {
            int code = order.getStatus() == BookingStatus.IN_USE ? 3 : 2;
            // 同一座位多条有效订单时，"使用中"优先于"已预订"
            dbStatusBySeat.merge(order.getSeatId(), code, Math::max);
        }

        for (StudyStore store : stores) {
            StoreOps ops = new StoreOps();
            ops.store = store;

            LambdaQueryWrapper<Seat> seatWrapper = new LambdaQueryWrapper<>();
            seatWrapper.eq(Seat::getStoreId, store.getId());
            seatWrapper.orderByAsc(Seat::getSeatCode);
            List<Seat> seats = seatService.list(seatWrapper);

            int maxHours = 0;
            for (Seat seat : seats) {
                SeatUsageStatVo stat = usageBySeat.get(seat.getId());
                if (stat != null) {
                    ops.totalOrders += stat.getOrderCount() == null ? 0 : stat.getOrderCount();
                    ops.totalHours += stat.getTotalHours() == null ? 0 : stat.getTotalHours();
                    maxHours = Math.max(maxHours, stat.getTotalHours() == null ? 0 : stat.getTotalHours());
                }
            }

            for (Seat seat : seats) {
                SeatCell cell = new SeatCell();
                cell.seatId = seat.getId();
                cell.seatCode = seat.getSeatCode();

                SeatUsageStatVo stat = usageBySeat.get(seat.getId());
                cell.orderCount = stat == null || stat.getOrderCount() == null ? 0 : stat.getOrderCount();
                cell.totalHours = stat == null || stat.getTotalHours() == null ? 0 : stat.getTotalHours();
                cell.heatTier = cell.totalHours <= 0 || maxHours <= 0
                        ? 0
                        : (int) Math.min(5, Math.ceil(cell.totalHours * 5.0 / maxHours));

                if (!heatView) {
                    Object v = redisTemplate.opsForValue()
                            .get(RedisConstant.APP_SEAT_STATUS_PREFIX + seat.getId());
                    int code = v instanceof Integer ? (Integer) v
                            : dbStatusBySeat.getOrDefault(seat.getId(), 1);
                    cell.realtime = code;
                    if (code == 2) {
                        ops.booked++;
                    } else if (code == 3) {
                        ops.inUse++;
                    } else {
                        ops.free++;
                    }
                }
                ops.seats.add(cell);
            }
            storeOpsList.add(ops);
        }

        model.addAttribute("heatView", heatView);
        model.addAttribute("storeOpsList", storeOpsList);
        return "admin/ops";
    }
}
