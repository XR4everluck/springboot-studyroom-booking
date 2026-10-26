package com.yourname.studyroom.portal.demo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 演示数据重置（原 tools/gen_demo_data.py 的 Java 实现）：
 * 仅在启动参数携带 --demo.reset=true 时执行，TRUNCATE 业务表后灌入覆盖全部业务状态的演示数据，
 * 区域/设施/标签/时长/支付字典与后台账号保留；日期一律随执行日推算。
 * 演示账号：用户端 demo001 / 123456，后台 admin / 123456。
 */
@Component
@ConditionalOnProperty(name = "demo.reset", havingValue = "true")
public class DemoDataRunner implements ApplicationRunner {

    private static final String PWD =
            "pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=";

    /** 门店：id, 名称, 简介, 省, 市, 区, 地址, 纬度, 经度, 电话, 营业时间, 座位方案(普通/靠窗/卡座/包间) */
    private static final Object[][] STORES = {
            {1, "静悦自习室（南山店）", "地铁口的沉浸式静音自习空间，配备独立台灯与储物柜，扫码即入座。",
                    "广东省", "深圳市", "南山区", "科技园南区高新南一道 100 号", "22.540000", "113.930000", "0755-88880001", "08:00-22:00", new int[]{6, 4, 4, 2}},
            {2, "星辰自习室（福田店）", "24 小时门禁的通宵自习馆，工位宽敞、独立新风，适合备考冲刺人群。",
                    "广东省", "深圳市", "福田区", "福田CBD 福华三路 88 号", "22.550000", "114.050000", "0755-88880002", "00:00-23:59", new int[]{5, 4, 3, 2}},
            {3, "沉浸自习馆（南山旗舰店）", "高端包间自习馆，独立隔间、免费茶水与打印机，考研考公首选。",
                    "广东省", "深圳市", "南山区", "深圳湾科技生态园 5 栋", "22.520000", "113.940000", "0755-88880003", "07:00-23:00", new int[]{4, 3, 2, 1}},
            {4, "静悦自习室（武汉·光谷店）", "光谷软件园旁的高性价比自习空间，插座、台灯、储物柜一应俱全。",
                    "湖北省", "武汉市", "洪山区", "光谷软件园 A9 栋 2 层", "30.507600", "114.408500", "027-87888001", "08:00-22:30", new int[]{6, 4, 4, 2}},
            {5, "星辰自习室（武汉·街道口店）", "街道口商圈地铁上盖，靠窗景观位与静音舱组合，学习氛围浓厚。",
                    "湖北省", "武汉市", "洪山区", "街道口珞瑜路 100 号 3 层", "30.523900", "114.349400", "027-87888002", "07:30-23:00", new int[]{5, 4, 3, 2}},
            {6, "沉浸自习馆（武汉·江汉路旗舰馆）", "江汉路步行街旁的旗舰自习馆，独立包间隔音出色，免费咖啡茶水。",
                    "湖北省", "武汉市", "江汉区", "江汉路步行街世纪大厦 12 层", "30.581000", "114.289400", "027-87888003", "00:00-23:59", new int[]{5, 3, 2, 2}},
            {7, "半亩书园（武汉·昙华林店）", "昙华林历史街区里的文艺自习空间，木质书桌配绿植角落，安静治愈。",
                    "湖北省", "武汉市", "武昌区", "昙华林 101 号 2 层", "30.544800", "114.308500", "027-87888004", "09:00-21:30", new int[]{4, 3, 2, 1}},
            {8, "云栖书屋（武汉·光谷天地店）", "光谷天地商圈的轻自习书屋，可短时自助，适合碎片时间高效学习。",
                    "湖北省", "武汉市", "洪山区", "光谷天地 F2 栋 1 层", "30.493000", "114.424500", "027-87888005", "08:30-22:00", new int[]{4, 3, 2, 1}},
    };

    private static final Map<String, Double> SEAT_PRICE = Map.of(
            "普通座", 6.00, "靠窗座", 8.00, "沉浸式卡座", 10.00, "包间", 15.00);
    private static final Map<String, String> SEAT_ZONE = Map.of(
            "普通座", "A", "靠窗座", "B", "沉浸式卡座", "C", "包间", "D");
    private static final String[] SEAT_TYPES = {"普通座", "靠窗座", "沉浸式卡座", "包间"};

    /** 用户：id, 账号, 手机号, 昵称 */
    private static final Object[][] USERS = {
            {1, "demo001", "13900000001", "陈晓晓"}, {2, "demo002", "13900000002", "林之然"},
            {3, "demo003", "13900000003", "苏晚晴"}, {4, "student001", "13900000004", "周子墨"},
            {5, "student002", "13900000005", "何雨桐"}, {6, "student003", "13900000006", "郑安然"},
            {7, "student004", "13900000007", "罗一鸣"}, {8, "worker001", "13900000008", "高子期"},
            {9, "worker002", "13900000009", "方梓涵"}, {10, "teacher001", "13900000010", "唐书白"},
    };

    /** 会员卡：id, 用户, 门店, 卡类型(1次/2小时/3月), 总额度, 金额 */
    private static final Object[][] CARDS = {
            {1, 1, 1, 1, 10, 100.00}, {2, 2, 1, 2, 50, 300.00}, {3, 4, 2, 3, 1, 299.00},
            {4, 5, 2, 1, 20, 190.00}, {5, 6, 4, 2, 30, 180.00}, {6, 7, 4, 3, 1, 299.00},
    };
    private static final Map<Integer, String> CARD_NAME = Map.of(1, "次卡", 2, "小时卡", 3, "月卡");

    /** 时长：termId -> (名称, 计费小时, 天数) */
    private static final Object[][] TERMS = {
            {1, "1小时", 1, 1}, {2, "4小时", 4, 1}, {3, "8小时", 8, 1},
            {4, "1天", 8, 1}, {5, "7天", 56, 7}, {6, "30天", 240, 30},
    };

    /** 订单：状态, 用户, 门店, 座位序号, 时长term, 支付类型, 会员卡(可空), 创建偏移天, 开始偏移天, 签退偏移天(可空), 备注 */
    private static final Object[][] ORDERS = {
            {6, 1, 1, 1, 1, 1, null, -6, -6, -6, ""},
            {6, 2, 1, 5, 2, 2, null, -6, -5, -5, ""},
            {6, 4, 2, 1, 3, 1, null, -5, -4, -4, ""},
            {6, 5, 2, 5, 1, 1, null, -5, -4, -4, ""},
            {6, 1, 1, 7, 3, 4, 1, -4, -3, -3, "会员卡抵扣"},
            {6, 6, 4, 1, 1, 1, null, -3, -3, -3, ""},
            {6, 7, 4, 5, 2, 2, null, -2, -2, -2, ""},
            {6, 8, 5, 1, 4, 1, null, -2, -1, 0, ""},
            {6, 9, 5, 5, 1, 3, null, -1, -1, -1, ""},
            {6, 10, 3, 1, 2, 4, null, 0, 0, 0, "今日已完成"},
            {6, 2, 1, 2, 1, 1, null, 0, 0, 0, ""},
            {6, 4, 2, 2, 1, 1, null, 0, 0, 0, ""},
            {8, 1, 1, 3, 1, 1, null, 0, 0, null, "演示主订单：可续期/签退"},
            {8, 3, 1, 8, 2, 2, null, 0, 0, null, ""},
            {8, 5, 2, 9, 3, 1, null, 0, 0, null, ""},
            {7, 2, 1, 6, 1, 1, 2, -1, -1, null, "续期申请，后台可审批"},
            {7, 7, 4, 6, 2, 2, 6, -1, 0, null, "续期申请，后台可审批"},
            {5, 4, 2, 6, 3, 1, null, 0, 0, null, "提前签退待确认"},
            {5, 6, 4, 2, 1, 1, null, 0, 0, null, "提前签退待确认"},
            {1, 1, 1, 4, 5, 2, null, 0, 1, null, "周卡预订"},
            {1, 3, 3, 2, 3, 1, null, 0, 1, null, ""},
            {1, 8, 5, 2, 6, 2, null, 0, 1, null, "月卡预订"},
            {2, 2, 1, 9, 3, 4, 2, -1, 1, null, "会员卡预订"},
            {2, 5, 2, 4, 1, 1, null, 0, 1, null, ""},
            {2, 9, 5, 6, 2, 2, null, -1, 1, null, ""},
            {3, 3, 1, 1, 1, 1, null, -4, -3, null, "用户主动取消"},
            {3, 6, 4, 3, 2, 1, null, -4, -3, null, "行程冲突取消"},
            {3, 10, 3, 3, 1, 3, null, -4, -3, null, ""},
            {4, 7, 4, 1, 1, 1, null, -8, -7, null, "未到店核销，自动到期"},
            {4, 8, 5, 3, 1, 1, null, -8, -7, null, "未到店核销，自动到期"},
    };

    /** 体验预约：用户, 门店, 时间偏移天, 时刻, 状态, 备注 */
    private static final Object[][] APPOINTMENTS = {
            {1, 1, 0, "15:00", 1, "想实地看看靠窗座位的环境"},
            {2, 3, 0, "10:30", 1, "第一次来，请老师介绍下包间"},
            {4, 4, -3, "14:00", 3, "已体验，环境满意"},
            {5, 5, -5, "16:30", 3, "已体验，办理了会员卡"},
            {6, 6, -2, "11:00", 2, "临时有事取消"},
            {7, 7, -7, "09:30", 3, "已体验"},
    };

    private final JdbcTemplate jdbcTemplate;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    public DemoDataRunner(JdbcTemplate jdbcTemplate,
                          org.springframework.data.redis.core.StringRedisTemplate redisTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        reset();
    }

    void reset() {
        System.out.println("[demo.reset] 开始重置演示数据 ...");
        LocalDate today = LocalDate.now();

        // 1. 重置业务数据表
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (String t : List.of("booking_order", "visit_appointment", "browsing_history", "member_card",
                "card_recharge_record", "seat_booking_term", "seat_payment_type", "seat_facility",
                "seat_label", "seat_attr_value", "seat", "store_facility", "store_label",
                "store_fee_value", "graph_info", "study_store", "user_info")) {
            jdbcTemplate.execute("TRUNCATE TABLE `" + t + "`");
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");

        // 2. 补充武汉市/区区域数据（已存在则跳过），得到区县 id
        jdbcTemplate.update("INSERT INTO province_info (name) SELECT '湖北省' FROM DUAL " +
                "WHERE NOT EXISTS (SELECT 1 FROM province_info WHERE name='湖北省')");
        Long hbId = queryId("SELECT id FROM province_info WHERE name='湖北省' LIMIT 1");
        jdbcTemplate.update("INSERT INTO city_info (province_id, name) SELECT ?, '武汉市' FROM DUAL " +
                "WHERE NOT EXISTS (SELECT 1 FROM city_info WHERE name='武汉市')", hbId);
        Long whId = queryId("SELECT id FROM city_info WHERE name='武汉市' LIMIT 1");
        for (String d : List.of("洪山区", "武昌区", "江汉区")) {
            jdbcTemplate.update("INSERT INTO district_info (city_id, name) SELECT ?, ? FROM DUAL " +
                    "WHERE NOT EXISTS (SELECT 1 FROM district_info WHERE city_id=? AND name=?)", whId, d, whId, d);
        }
        // 2.5 属性字典（幂等：已有同名属性/属性值则跳过，保留用户自建内容）
        String[][] ATTR_DICT = {
                {"采光", "独立窗边,中央照明,角落暗座"},
                {"插座", "每座双插,共享插排,无线充电"},
                {"环境", "静音舱,低噪区,普通区"},
                {"储物", "独立储物柜,开放挂钩"},
        };
        for (String[] kv : ATTR_DICT) {
            jdbcTemplate.update("INSERT INTO attr_key (name) SELECT ? FROM DUAL " +
                    "WHERE NOT EXISTS (SELECT 1 FROM attr_key WHERE name=?)", kv[0], kv[0]);
            for (String v : kv[1].split(",")) {
                jdbcTemplate.update("INSERT INTO attr_value (attr_key_id, name) " +
                        "SELECT k.id, ? FROM attr_key k WHERE k.name=? " +
                        "AND NOT EXISTS (SELECT 1 FROM attr_value av WHERE av.name=? AND av.attr_key_id=k.id)",
                        v, kv[0], v);
            }
        }

        Long hsId = queryId("SELECT id FROM district_info WHERE city_id=? AND name='洪山区' LIMIT 1", whId);
        Long wcId = queryId("SELECT id FROM district_info WHERE city_id=? AND name='武昌区' LIMIT 1", whId);
        Long jhId = queryId("SELECT id FROM district_info WHERE city_id=? AND name='江汉区' LIMIT 1", whId);
        java.util.Map<String, Long> distIds = new java.util.HashMap<>();
        distIds.put("南山区", 1L);
        distIds.put("福田区", 2L);
        distIds.put("洪山区", hsId);
        distIds.put("武昌区", wcId);
        distIds.put("江汉区", jhId);

        // 3. 门店
        Long gdId = queryId("SELECT id FROM province_info WHERE name='广东省' LIMIT 1");
        List<Object[]> storeRows = new ArrayList<>();
        for (Object[] s : STORES) {
            long seatCount = 0;
            for (int n : (int[]) s[11]) {
                seatCount += n;
            }
            Long provinceId = "湖北省".equals(s[3]) ? hbId : gdId;
            storeRows.add(new Object[]{s[0], s[1], s[2], distIds.get(s[5]), s[5],
                    "武汉市".equals(s[4]) ? whId : 1L, s[4], provinceId, s[3], s[6], s[7], s[8], s[9], s[10], seatCount, 1});
        }
        jdbcTemplate.batchUpdate("INSERT INTO study_store (id,name,introduction,district_id,district_name," +
                "city_id,city_name,province_id,province_name,address_detail,latitude,longitude,phone," +
                "business_hours,seat_count,is_release) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", storeRows);

        // 4. 座位（id, code, price, storeId, 类型, 静音）
        List<Object[]> seats = new ArrayList<>();
        int seatId = 0;
        for (Object[] s : STORES) {
            int[] plan = (int[]) s[11];
            for (int t = 0; t < SEAT_TYPES.length; t++) {
                for (int i = 1; i <= plan[t]; i++) {
                    seatId++;
                    String type = SEAT_TYPES[t];
                    seats.add(new Object[]{seatId, SEAT_ZONE.get(type) + String.format("-%02d", i),
                            SEAT_PRICE.get(type), s[0], type,
                            1, 1, "沉浸式卡座".equals(type) || "包间".equals(type) ? 1 : 0, 1});
                }
            }
        }
        jdbcTemplate.batchUpdate("INSERT INTO seat (id,seat_code,price,store_id,seat_type,has_power," +
                "has_light,is_quiet,is_release) VALUES (?,?,?,?,?,?,?,?,?)", seats);
        jdbcTemplate.update("INSERT INTO seat_booking_term (seat_id, booking_term_id) SELECT s.id, t.id FROM seat s JOIN booking_term t");
        jdbcTemplate.update("INSERT INTO seat_payment_type (seat_id, payment_type_id) SELECT s.id, p.id FROM seat s JOIN payment_type p");
        jdbcTemplate.update("INSERT INTO seat_facility (seat_id, facility_id) " +
                "SELECT s.id, ((s.id + k.offset) % 6) + 1 FROM seat s JOIN (SELECT 0 offset UNION SELECT 2 UNION SELECT 4) k");
        jdbcTemplate.update("INSERT INTO seat_label (seat_id, label_id) " +
                "SELECT s.id, CASE s.seat_type WHEN '普通座' THEN 9 WHEN '靠窗座' THEN 8 WHEN '沉浸式卡座' THEN 7 ELSE 10 END FROM seat s");

        // 4.5 座位-属性关联（按座位类型确定性匹配，幂等）
        Long vWindow = queryId("SELECT id FROM attr_value WHERE name='独立窗边' LIMIT 1");
        Long vSocket = queryId("SELECT id FROM attr_value WHERE name='每座双插' LIMIT 1");
        Long vQuiet = queryId("SELECT id FROM attr_value WHERE name='静音舱' LIMIT 1");
        Long vLowNoise = queryId("SELECT id FROM attr_value WHERE name='低噪区' LIMIT 1");
        Long vLocker = queryId("SELECT id FROM attr_value WHERE name='独立储物柜' LIMIT 1");
        jdbcTemplate.update("INSERT INTO seat_attr_value (seat_id, attr_value_id) " +
                "SELECT s.id, ? FROM seat s WHERE s.seat_type = '靠窗座' " +
                "AND NOT EXISTS (SELECT 1 FROM seat_attr_value sav WHERE sav.seat_id = s.id AND sav.attr_value_id = ?)", vWindow, vWindow);
        jdbcTemplate.update("INSERT INTO seat_attr_value (seat_id, attr_value_id) " +
                "SELECT s.id, ? FROM seat s WHERE NOT EXISTS " +
                "(SELECT 1 FROM seat_attr_value sav WHERE sav.seat_id = s.id AND sav.attr_value_id = ?)", vSocket, vSocket);
        jdbcTemplate.update("INSERT INTO seat_attr_value (seat_id, attr_value_id) " +
                "SELECT s.id, ? FROM seat s WHERE s.seat_type IN ('沉浸式卡座', '包间') " +
                "AND NOT EXISTS (SELECT 1 FROM seat_attr_value sav WHERE sav.seat_id = s.id AND sav.attr_value_id = ?)", vQuiet, vQuiet);
        jdbcTemplate.update("INSERT INTO seat_attr_value (seat_id, attr_value_id) " +
                "SELECT s.id, ? FROM seat s WHERE s.seat_type = '普通座' " +
                "AND NOT EXISTS (SELECT 1 FROM seat_attr_value sav WHERE sav.seat_id = s.id AND sav.attr_value_id = ?)", vLowNoise, vLowNoise);
        jdbcTemplate.update("INSERT INTO seat_attr_value (seat_id, attr_value_id) " +
                "SELECT s.id, ? FROM seat s WHERE s.seat_type = '包间' " +
                "AND NOT EXISTS (SELECT 1 FROM seat_attr_value sav WHERE sav.seat_id = s.id AND sav.attr_value_id = ?)", vLocker, vLocker);

        // 5. 门店设施/标签关联（确定性轮转）
        jdbcTemplate.update("INSERT INTO store_facility (store_id, facility_id) " +
                "SELECT s.id, f.id FROM study_store s JOIN facility_info f WHERE f.type = 2 AND ((s.id + f.id) % 5) < 3");
        jdbcTemplate.update("INSERT INTO store_label (store_id, label_id) " +
                "SELECT s.id, l.id FROM study_store s JOIN label_info l WHERE l.type = 1 AND ((s.id * 2 + l.id) % 4) < 2");

        // 6. 门店/座位图片
        List<Object[]> graphs = new ArrayList<>();
        for (Object[] s : STORES) {
            graphs.add(new Object[]{1, s[0], "门店封面", "/img/studyroom/store-" + s[0] + ".svg"});
        }
        for (Object[] seat : seats) {
            long sid = ((Number) seat[0]).longValue();
            graphs.add(new Object[]{2, sid, "座位实拍", "/img/studyroom/seat-" + ((sid - 1) % 20 + 1) + ".svg"});
        }
        jdbcTemplate.batchUpdate("INSERT INTO graph_info (item_type, item_id, name, url) VALUES (?,?,?,?)", graphs);

        // 7. 用户（密码统一 123456）
        List<Object[]> userRows = new ArrayList<>();
        for (Object[] u : USERS) {
            userRows.add(new Object[]{u[0], u[1], u[2], PWD, u[3], 1});
        }
        jdbcTemplate.batchUpdate("INSERT INTO user_info (id,username,phone,password,nickname,status) VALUES (?,?,?,?,?,?)", userRows);

        // 8. 会员卡与充值记录
        List<Object[]> cardRows = new ArrayList<>();
        List<Object[]> rechargeRows = new ArrayList<>();
        for (Object[] c : CARDS) {
            int cid = (int) c[0];
            int uid = (int) c[1];
            int tot = (int) c[4];
            cardRows.add(new Object[]{cid, "MC" + (20260000 + cid * 137), uid, c[2], c[3], tot, tot, 1});
            rechargeRows.add(new Object[]{cid, uid, 1, tot, c[5], CARD_NAME.get((int) c[3]) + "首次开通"});
        }
        jdbcTemplate.batchUpdate("INSERT INTO member_card (id,card_no,user_id,store_id,card_type," +
                "total_quantity,remaining_quantity,status) VALUES (?,?,?,?,?,?,?,?)", cardRows);
        jdbcTemplate.batchUpdate("INSERT INTO card_recharge_record (member_card_id,user_id,recharge_type," +
                "recharge_quantity,amount,additional_info) VALUES (?,?,?,?,?,?)", rechargeRows);

        // 9. 订单（覆盖全部状态，时间随执行日推算）
        List<Object[]> orderRows = new ArrayList<>();
        for (Object[] o : ORDERS) {
            int status = (int) o[0];
            int uid = (int) o[1];
            int storeId = (int) o[2];
            int nth = (int) o[3];
            int termId = (int) o[4];
            int payId = (int) o[5];
            Integer mc = (Integer) o[6];
            int createOff = (int) o[7];
            int startOff = (int) o[8];
            Integer checkOutOff = (Integer) o[9];
            String note = (String) o[10];

            List<Object[]> storeSeats = seats.stream().filter(x -> ((int) x[3]) == storeId).toList();
            Object[] seat = storeSeats.get((nth - 1) % storeSeats.size());
            String seatType = (String) seat[4];
            double price = SEAT_PRICE.get(seatType);
            int hours = (int) TERMS[termId - 1][2];
            int days = (int) TERMS[termId - 1][3];
            double amount = Math.round(price * hours * 100) / 100.0;

            Timestamp create = createOff == 0
                    ? new Timestamp(System.currentTimeMillis())
                    : at(today.plusDays(createOff), "10:00");
            Timestamp start = at(today.plusDays(startOff), "00:00");
            Timestamp end = at(today.plusDays(startOff + days), "00:00");
            Timestamp checkIn = null;
            Timestamp checkOut = null;
            if (status == 6) {
                checkIn = at(today.plusDays(startOff), "09:00");
                checkOut = at(today.plusDays(checkOutOff), "17:30");
            } else if (status == 5 || status == 7 || status == 8) {
                checkIn = at(today.plusDays(startOff), "09:00");
            }
            int source = status == 7 ? 2 : 1;

            orderRows.add(new Object[]{uid, USERS[uid - 1][2], USERS[uid - 1][3], storeId, seat[0],
                    start, end, TERMS[termId - 1][1], checkIn, checkOut, termId, amount,
                    payId, mc, status, source, note, create});
        }
        jdbcTemplate.batchUpdate("INSERT INTO booking_order (user_id,phone,name,store_id,seat_id,start_date," +
                "end_date,booking_type,check_in_time,check_out_time,booking_term_id,total_amount,payment_type_id," +
                "member_card_id,status,source_type,additional_info,create_time) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)", orderRows);
        jdbcTemplate.update("UPDATE booking_order SET order_no = CONCAT('SO', DATE_FORMAT(create_time, '%Y%m%d'), LPAD(id, 6, '0')) WHERE order_no IS NULL OR order_no = ''");

        // 10. 体验预约
        List<Object[]> apptRows = new ArrayList<>();
        for (Object[] a : APPOINTMENTS) {
            int uid = (int) a[0];
            int storeId = (int) a[1];
            int off = (int) a[2];
            String hm = (String) a[3];
            int st = (int) a[4];
            String note = (String) a[5];
            Timestamp time = at(today.plusDays(off), hm);
            Timestamp create = off <= 0
                    ? Timestamp.valueOf(java.time.LocalDateTime.now().minusDays(Math.abs(off) + 1L))
                    : new Timestamp(System.currentTimeMillis());
            apptRows.add(new Object[]{uid, USERS[uid - 1][3], USERS[uid - 1][2], storeId, time, note, st, create});
        }
        jdbcTemplate.batchUpdate("INSERT INTO visit_appointment (user_id,name,phone,store_id,appointment_time," +
                "additional_info,appointment_status,create_time) VALUES (?,?,?,?,?,?,?,?)", apptRows);

        // 11. 浏览历史（近 7 日）
        jdbcTemplate.update("INSERT INTO browsing_history (user_id, seat_id, browse_time) " +
                "SELECT u.id, s.id, NOW() - INTERVAL ((u.id * 13 + s.id * 7) % 160) HOUR " +
                "FROM user_info u JOIN seat s WHERE (u.id * 31 + s.id) % 25 = 0");

        // 12. 自检输出
        List<Map<String, Object>> counts = jdbcTemplate.queryForList(
                "SELECT '门店' AS item, COUNT(*) AS cnt FROM study_store " +
                "UNION ALL SELECT '座位', COUNT(*) FROM seat " +
                "UNION ALL SELECT '用户', COUNT(*) FROM user_info " +
                "UNION ALL SELECT '订单', COUNT(*) FROM booking_order " +
                "UNION ALL SELECT '预约', COUNT(*) FROM visit_appointment " +
                "UNION ALL SELECT '会员卡', COUNT(*) FROM member_card");
        counts.forEach(row -> System.out.println("[demo.reset] " + row.get("item") + " = " + row.get("cnt")));

        // 13. 清理受数据重置影响的缓存：座位详情/座位实时状态/热门榜单
        for (String pattern : List.of("app:seat:*", "app:store:hot:list", "app:seat:hot:list")) {
            java.util.Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                System.out.println("[demo.reset] 已清理缓存 " + pattern + "（" + keys.size() + " 个 key）");
            }
        }

        System.out.println("[demo.reset] 演示数据重置完成。用户端 demo001/123456，后台 admin/123456");
    }

    private Timestamp at(LocalDate date, String hm) {
        String[] parts = hm.split(":");
        return Timestamp.valueOf(date.atTime(LocalTime.of(Integer.parseInt(parts[0]),
                parts.length > 1 ? Integer.parseInt(parts[1]) : 0)));
    }

    private Long queryId(String sql, Object... args) {
        List<Long> ids = jdbcTemplate.queryForList(sql, Long.class, args);
        return ids.isEmpty() ? null : ids.get(0);
    }
}
