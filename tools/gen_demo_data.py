# -*- coding: utf-8 -*-
"""
生成 sql/studyroom_demo_data.sql —— 演示数据重置脚本（可重复执行）
策略：TRUNCATE 业务表 -> 灌入精心设计的演示数据（覆盖全部业务状态）
保留：区域字典、设施/标签/时长/支付字典、system_post/system_user
"""
import os

OUT = os.path.join(os.path.dirname(__file__), "..", "sql", "studyroom_demo_data.sql")

PWD = "pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM="

# ---------- 门店 ----------
# (id, name, intro, province, city, district, address, lat, lng, phone, hours, seat_plan)
# seat_plan: (普通座, 靠窗座, 沉浸式卡座, 包间)
STORES = [
    (1, "静悦自习室（南山店）", "地铁口的沉浸式静音自习空间，配备独立台灯与储物柜，扫码即入座。",
     "广东省", "深圳市", "南山区", "科技园南区高新南一道 100 号", 22.540000, 113.930000, "0755-88880001", "08:00-22:00", (6, 4, 4, 2)),
    (2, "星辰自习室（福田店）", "24 小时门禁的通宵自习馆，工位宽敞、独立新风，适合备考冲刺人群。",
     "广东省", "深圳市", "福田区", "福田CBD 福华三路 88 号", 22.550000, 114.050000, "0755-88880002", "00:00-23:59", (5, 4, 3, 2)),
    (3, "沉浸自习馆（南山旗舰店）", "高端包间自习馆，独立隔间、免费茶水与打印机，考研考公首选。",
     "广东省", "深圳市", "南山区", "深圳湾科技生态园 5 栋", 22.520000, 113.940000, "0755-88880003", "07:00-23:00", (4, 3, 2, 1)),
    (4, "静悦自习室（武汉·光谷店）", "光谷软件园旁的高性价比自习空间，插座、台灯、储物柜一应俱全。",
     "湖北省", "武汉市", "洪山区", "光谷软件园 A9 栋 2 层", 30.507600, 114.408500, "027-87888001", "08:00-22:30", (6, 4, 4, 2)),
    (5, "星辰自习室（武汉·街道口店）", "街道口商圈地铁上盖，靠窗景观位与静音舱组合，学习氛围浓厚。",
     "湖北省", "武汉市", "洪山区", "街道口珞瑜路 100 号 3 层", 30.523900, 114.349400, "027-87888002", "07:30-23:00", (5, 4, 3, 2)),
    (6, "沉浸自习馆（武汉·江汉路旗舰馆）", "江汉路步行街旁的旗舰自习馆，独立包间隔音出色，免费咖啡茶水。",
     "湖北省", "武汉市", "江汉区", "江汉路步行街世纪大厦 12 层", 30.581000, 114.289400, "027-87888003", "00:00-23:59", (5, 3, 2, 2)),
    (7, "半亩书园（武汉·昙华林店）", "昙华林历史街区里的文艺自习空间，木质书桌配绿植角落，安静治愈。",
     "湖北省", "武汉市", "武昌区", "昙华林 101 号 2 层", 30.544800, 114.308500, "027-87888004", "09:00-21:30", (4, 3, 2, 1)),
    (8, "云栖书屋（武汉·光谷天地店）", "光谷天地商圈的轻自习书屋，可短时自助，适合碎片时间高效学习。",
     "湖北省", "武汉市", "洪山区", "光谷天地 F2 栋 1 层", 30.493000, 114.424500, "027-87888005", "08:30-22:00", (4, 3, 2, 1)),
]

SEAT_PRICE = {"普通座": 6.00, "靠窗座": 8.00, "沉浸式卡座": 10.00, "包间": 15.00}
SEAT_ZONE = {"普通座": "A", "靠窗座": "B", "沉浸式卡座": "C", "包间": "D"}
ZONE_COUNTER = {}

# ---------- 用户 ----------
USERS = [
    (1, "demo001", "13900000001", "陈晓晓"),
    (2, "demo002", "13900000002", "林之然"),
    (3, "demo003", "13900000003", "苏晚晴"),
    (4, "student001", "13900000004", "周子墨"),
    (5, "student002", "13900000005", "何雨桐"),
    (6, "student003", "13900000006", "郑安然"),
    (7, "student004", "13900000007", "罗一鸣"),
    (8, "worker001", "13900000008", "高子期"),
    (9, "worker002", "13900000009", "方梓涵"),
    (10, "teacher001", "13900000010", "唐书白"),
]

# ---------- 会员卡 ----------
# (id, user_id, store_id, card_type 1次/2小时/3月, total, amount, card_no后缀)
CARDS = [
    (1, 1, 1, 1, 10, 100.00), (2, 2, 1, 2, 50, 300.00), (3, 4, 2, 3, 1, 299.00),
    (4, 5, 2, 1, 20, 190.00), (5, 6, 4, 2, 30, 180.00), (6, 7, 4, 3, 1, 299.00),
]
CARD_NAME = {1: "次卡", 2: "小时卡", 3: "月卡"}

# ---------- 时长 ----------
TERMS = {1: (1, "1小时", 1), 2: (4, "4小时", 4), 3: (8, "8小时", 8),
         4: (1, "1天", 8), 5: (7, "7天", 56), 6: (30, "30天", 240)}  # (month_count, 名称, 计费小时)

# ---------- 订单 ----------
# (状态, 用户, 门店, 序号, 时长term, 支付类型, member_card_id|None, 创建偏移天, 入住偏移天|None, 离店偏移天|None, 备注)
# start_date = 创建日之后：已开始订单 start=create_date；未来订单 start=+1
ORDERS = [
    # 已完成（近7日流水，喂给看板趋势）
    (6, 1, 1, 1, 1, 1, None, -6, -6, -6, ""),
    (6, 2, 1, 5, 2, 2, None, -6, -5, -5, ""),
    (6, 4, 2, 1, 3, 1, None, -5, -4, -4, ""),
    (6, 5, 2, 5, 1, 1, None, -5, -4, -4, ""),
    (6, 1, 1, 7, 3, 4, 1, -4, -3, -3, "会员卡抵扣"),
    (6, 6, 4, 1, 1, 1, None, -3, -3, -3, ""),
    (6, 7, 4, 5, 2, 2, None, -2, -2, -2, ""),
    (6, 8, 5, 1, 4, 1, None, -2, -1, 0, ""),
    (6, 9, 5, 5, 1, 3, None, -1, -1, -1, ""),
    (6, 10, 3, 1, 2, 4, None, 0, 0, 0, "今日已完成"),
    (6, 2, 1, 2, 1, 1, None, 0, 0, 0, ""),
    (6, 4, 2, 2, 1, 1, None, 0, 0, 0, ""),
    # 使用中（今天可演示 签到/续期/签退）
    (8, 1, 1, 3, 1, 1, None, 0, 0, None, "演示主订单：可续期/签退"),
    (8, 3, 1, 8, 2, 2, None, 0, 0, None, ""),
    (8, 5, 2, 9, 3, 1, None, 0, 0, None, ""),
    # 续期待确认
    (7, 2, 1, 6, 1, 1, 2, -1, -1, None, "续期申请，后台可审批"),
    (7, 7, 4, 6, 2, 2, 6, -1, 0, None, "续期申请，后台可审批"),
    # 签退待确认
    (5, 4, 2, 6, 3, 1, None, 0, 0, None, "提前签退待确认"),
    (5, 6, 4, 2, 1, 1, None, 0, 0, None, "提前签退待确认"),
    # 待确认（明天开始）
    (1, 1, 1, 4, 5, 2, None, 0, 1, None, "周卡预订"),
    (1, 3, 3, 2, 3, 1, None, 0, 1, None, ""),
    (1, 8, 5, 2, 6, 2, None, 0, 1, None, "月卡预订"),
    # 已预订
    (2, 2, 1, 9, 3, 4, 2, -1, 1, None, "会员卡预订"),
    (2, 5, 2, 4, 1, 1, None, 0, 1, None, ""),
    (2, 9, 5, 6, 2, 2, None, -1, 1, None, ""),
    # 已取消
    (3, 3, 1, 1, 1, 1, None, -4, -3, None, "用户主动取消"),
    (3, 6, 4, 3, 2, 1, None, -4, -3, None, "行程冲突取消"),
    (3, 10, 3, 3, 1, 3, None, -4, -3, None, ""),
    # 已到期
    (4, 7, 4, 1, 1, 1, None, -8, -7, None, "未到店核销，自动到期"),
    (4, 8, 5, 3, 1, 1, None, -8, -7, None, "未到店核销，自动到期"),
]

# ---------- 到店体验预约 ----------
# (用户, 门店, 时间偏移天, 具体时刻, 状态, 备注)
APPTS = [
    (1, 1, 0, "15:00", 1, "想实地看看靠窗座位的环境"),
    (2, 3, 0, "10:30", 1, "第一次来，请老师介绍下包间"),
    (4, 4, -3, "14:00", 3, "已体验，环境满意"),
    (5, 5, -5, "16:30", 3, "已体验，办理了会员卡"),
    (6, 6, -2, "11:00", 2, "临时有事取消"),
    (7, 7, -7, "09:30", 3, "已体验"),
]


def q(v):
    if v is None:
        return "NULL"
    if isinstance(v, (int, float)):
        return str(v)
    return "'" + str(v).replace("'", "''") + "'"


def main():
    L = []
    A = L.append
    A("-- =============================================================")
    A("-- 上岸自习室预订系统 · 演示数据重置脚本（studyroom_demo_data.sql）")
    A("-- 用途：对外演示前一键重置并灌入完整演示数据（可重复执行）")
    A("-- 执行：mysql -uroot -p --default-character-set=utf8mb4 < sql/studyroom_demo_data.sql")
    A("-- 说明：仅重置业务数据表；区域/设施/标签/时长/支付字典与后台账号保留")
    A("-- 演示账号：H5 demo001 / 123456   后台 admin / 123456")
    A("-- =============================================================")
    A("USE studyroom_booking;")
    A("SET NAMES utf8mb4;")
    A("")
    A("-- ---------- 1. 重置业务数据表 ----------")
    A("SET FOREIGN_KEY_CHECKS = 0;")
    for t in ["booking_order", "visit_appointment", "browsing_history", "member_card",
              "card_recharge_record", "seat_booking_term", "seat_payment_type", "seat_facility",
              "seat_label", "seat_attr_value", "seat", "store_facility", "store_label",
              "store_fee_value", "graph_info", "study_store", "user_info"]:
        A(f"TRUNCATE TABLE `{t}`;")
    A("SET FOREIGN_KEY_CHECKS = 1;")
    A("")

    A("-- ---------- 2. 补充武汉市/区区域数据（已存在则跳过） ----------")
    A("INSERT INTO province_info (name) SELECT '湖北省' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM province_info WHERE name='湖北省');")
    A("SET @hb_id := (SELECT id FROM province_info WHERE name='湖北省' LIMIT 1);")
    A("INSERT INTO city_info (province_id, name) SELECT @hb_id, '武汉市' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM city_info WHERE name='武汉市');")
    A("SET @wh_id := (SELECT id FROM city_info WHERE name='武汉市' LIMIT 1);")
    A("INSERT INTO district_info (city_id, name) SELECT @wh_id, t.name FROM (SELECT '洪山区' name UNION SELECT '武昌区' UNION SELECT '江汉区') t WHERE NOT EXISTS (SELECT 1 FROM district_info WHERE city_id=@wh_id AND name=t.name);")
    A("SET @hs_id := (SELECT id FROM district_info WHERE city_id=@wh_id AND name='洪山区' LIMIT 1);")
    A("SET @wc_id := (SELECT id FROM district_info WHERE city_id=@wh_id AND name='武昌区' LIMIT 1);")
    A("SET @jh_id := (SELECT id FROM district_info WHERE city_id=@wh_id AND name='江汉区' LIMIT 1);")
    A("SET @sz_id := 1; SET @ns_id := 1; SET @ft_id := 2;")
    A("")

    A("-- ---------- 3. 门店（8 家） ----------")
    city_map = {"深圳市": "@sz_id", "武汉市": "@wh_id"}
    dist_map = {"南山区": "@ns_id", "福田区": "@ft_id", "洪山区": "@hs_id", "武昌区": "@wc_id", "江汉区": "@jh_id"}
    A("INSERT INTO `study_store` (`id`,`name`,`introduction`,`district_id`,`district_name`,`city_id`,`city_name`,`province_id`,`province_name`,`address_detail`,`latitude`,`longitude`,`phone`,`business_hours`,`seat_count`,`is_release`) VALUES")
    store_rows = []
    for s in STORES:
        sid, name, intro, prov, city, dist, addr, lat, lng, phone, hours, plan = s
        n = sum(plan)
        store_rows.append(
            f"({sid},{q(name)},{q(intro)},{dist_map[dist]},{q(dist)},{city_map[city]},{q(city)},"
            f"(SELECT id FROM province_info WHERE name={q(prov)}),{q(prov)},{q(addr)},{lat},{lng},{q(phone)},{q(hours)},{n},1)")
    A(",\n".join(store_rows) + ";")
    A("")

    A("-- ---------- 4. 座位 ----------")
    seats = []  # (id, code, price, store_id, type, quiet)
    sid = 0
    for st in STORES:
        store_id = st[0]
        plan = st[-1]
        for seat_type, cnt in zip(("普通座", "靠窗座", "沉浸式卡座", "包间"), plan):
            for i in range(1, cnt + 1):
                sid += 1
                ZONE_COUNTER[seat_type] = ZONE_COUNTER.get(seat_type, 0) + 1
                code = f"{SEAT_ZONE[seat_type]}-{i:02d}"
                quiet = 1 if seat_type in ("沉浸式卡座", "包间") else 0
                seats.append((sid, code, SEAT_PRICE[seat_type], store_id, seat_type, quiet))
    A("INSERT INTO `seat` (`id`,`seat_code`,`price`,`store_id`,`seat_type`,`has_power`,`has_light`,`is_quiet`,`is_release`) VALUES")
    rows = [f"({i},{q(c)},{p},{sto},{q(t)},1,1,{qt},1)" for i, c, p, sto, t, qt in seats]
    for i in range(0, len(rows), 25):
        A(",\n".join(rows[i:i + 25]) + (";" if i + 25 >= len(rows) else ","))
    A("")
    A("-- 座位-时长（全量支持 6 档时长）")
    A("INSERT INTO seat_booking_term (seat_id, booking_term_id) SELECT s.id, t.id FROM seat s JOIN booking_term t;")
    A("-- 座位-支付方式（全量支持 4 种支付）")
    A("INSERT INTO seat_payment_type (seat_id, payment_type_id) SELECT s.id, p.id FROM seat s JOIN payment_type p;")
    A("-- 座位-设施（每座 3 项，循环轮转保证多样性）")
    A("INSERT INTO seat_facility (seat_id, facility_id)")
    A("SELECT s.id, ((s.id + k.offset) % 6) + 1 FROM seat s JOIN (SELECT 0 offset UNION SELECT 2 UNION SELECT 4) k;")
    A("-- 座位-标签（按座位类型匹配）")
    A("INSERT INTO seat_label (seat_id, label_id)")
    A("SELECT s.id, CASE s.seat_type WHEN '普通座' THEN 9 WHEN '靠窗座' THEN 8 WHEN '沉浸式卡座' THEN 7 ELSE 10 END FROM seat s;")
    A("")

    A("-- ---------- 5. 门店设施/标签关联（确定性轮转，保证多样性） ----------")
    A("INSERT INTO store_facility (store_id, facility_id) SELECT s.id, f.id FROM study_store s JOIN facility_info f WHERE f.type = 2 AND ((s.id + f.id) % 5) < 3;")
    A("INSERT INTO store_label (store_id, label_id) SELECT s.id, l.id FROM study_store s JOIN label_info l WHERE l.type = 1 AND ((s.id * 2 + l.id) % 4) < 2;")
    A("")
    A("-- ---------- 6. 门店/座位图片 ----------")
    A("INSERT INTO graph_info (item_type, item_id, name, url) VALUES")
    covers = ",\n".join(f"(1, {s[0]}, '门店封面', '/img/studyroom/store-{s[0]}.svg')" for s in STORES)
    photos = ",\n".join(f"(2, {i}, '座位实拍', '/img/studyroom/seat-{(i - 1) % 20 + 1}.svg')" for i, *_ in seats)
    A(covers + ",\n" + photos + ";")
    A("")

    A("-- ---------- 7. 用户（10 人，密码统一 123456） ----------")
    A("INSERT INTO `user_info` (`id`,`username`,`phone`,`password`,`nickname`,`status`) VALUES")
    A(",\n".join(f"({i},{q(u)},{q(p)},{q(PWD)},{q(n)},1)" for i, u, p, n in USERS) + ";")
    A("")

    A("-- ---------- 8. 会员卡与充值记录 ----------")
    A("INSERT INTO `member_card` (`id`,`card_no`,`user_id`,`store_id`,`card_type`,`total_quantity`,`remaining_quantity`,`valid_until`,`status`) VALUES")
    A(",\n".join(
        f"({cid},{q('MC' + str(20260000 + cid * 137))},{u},{sto},{ct},{tot},{tot},DATE_ADD(CURDATE(), INTERVAL 180 DAY),1)"
        for cid, u, sto, ct, tot, _ in CARDS) + ";")
    A("INSERT INTO `card_recharge_record` (`member_card_id`,`user_id`,`recharge_type`,`recharge_quantity`,`amount`,`additional_info`) VALUES")
    A(",\n".join(
        f"({cid},{u},1,{tot},{amt},{q(CARD_NAME[ct] + '首次开通')})"
        for cid, u, _, ct, tot, amt in CARDS) + ";")
    A("")

    # 订单工具
    def pick_seat(store_id, nth):
        ss = [x for x in seats if x[3] == store_id]
        return ss[(nth - 1) % len(ss)]

    A("-- ---------- 9. 订单（30 笔，覆盖 8 种状态，时间随执行日自动推算） ----------")
    A("INSERT INTO `booking_order` (`user_id`,`phone`,`name`,`store_id`,`seat_id`,`start_date`,`end_date`,`booking_type`,`check_in_time`,`check_out_time`,`booking_term_id`,`total_amount`,`payment_type_id`,`member_card_id`,`status`,`source_type`,`additional_info`,`create_time`,`update_time`) VALUES")
    orows = []
    for (status, uid, store_id, nth, term_id, pay_id, mc, c_off, s_off, co_off, note) in ORDERS:
        seat = pick_seat(store_id, nth)
        hours = TERMS[term_id][2]
        amount = round(SEAT_PRICE[seat[4]] * hours, 2)
        # end_date = start + duration_days
        days = {1: 1, 2: 1, 3: 1, 4: 1, 5: 7, 6: 30}[term_id]
        start_expr = f"CURDATE() + INTERVAL {s_off} DAY" if s_off else "CURDATE()"
        end_expr = f"CURDATE() + INTERVAL {s_off + days} DAY"
        ci = f"CONCAT(CURDATE() + INTERVAL {s_off} DAY, ' 09:00:00')" if s_off is not None and status in (5, 7, 8, 6) else "NULL"
        if status == 6 and s_off is not None:
            ci = f"CONCAT(CURDATE() + INTERVAL {s_off} DAY, ' 09:00:00')"
            co = f"CONCAT(CURDATE() + INTERVAL {co_off} DAY, ' 17:30:00')"
        elif status in (5, 7, 8):
            co = "NULL"
        else:
            co = "NULL"
        create_t = f"CONCAT(CURDATE() + INTERVAL {c_off} DAY, ' 10:00:00')" if c_off else "NOW()"
        source = 2 if status == 7 else 1
        orows.append(
            f"({uid},{q(USERS[uid-1][2])},{q(USERS[uid-1][3])},{store_id},{seat[0]},{start_expr},{end_expr},"
            f"{q(TERMS[term_id][1])},{ci},{co},{term_id},{amount},{pay_id},{mc if mc else 'NULL'},{status},{source},{q(note)},{create_t},NOW())")
    A(",\n".join(orows) + ";")
    A("-- 统一生成规范订单号：SO + 日期 + 6位序号")
    A("UPDATE booking_order SET order_no = CONCAT('SO', DATE_FORMAT(create_time, '%Y%m%d'), LPAD(id, 6, '0')) WHERE order_no IS NULL OR order_no = '';")
    A("")

    A("-- ---------- 10. 到店体验预约 ----------")
    A("INSERT INTO `visit_appointment` (`user_id`,`name`,`phone`,`store_id`,`appointment_time`,`additional_info`,`appointment_status`,`create_time`) VALUES")
    arows = []
    for (uid, store_id, off, hm, status, note) in APPTS:
        t = f"TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL {-off} DAY), '{hm}:00')" if off < 0 else f"TIMESTAMP(CURDATE(), '{hm}:00')"
        ct = f"DATE_SUB(NOW(), INTERVAL {abs(off) + 1} DAY)" if off <= 0 else "NOW()"
        arows.append(f"({uid},{q(USERS[uid-1][3])},{q(USERS[uid-1][2])},{store_id},{t},{q(note)},{status},{ct})")
    A(",\n".join(arows) + ";")
    A("")

    A("-- ---------- 11. 浏览历史（近 7 日 40+ 条） ----------")
    A("INSERT INTO browsing_history (user_id, seat_id, browse_time)")
    A("SELECT u.id, s.id, NOW() - INTERVAL ((u.id * 13 + s.id * 7) % 160) HOUR")
    A("FROM user_info u JOIN seat s WHERE (u.id * 31 + s.id) % 25 = 0;")
    A("")

    A("-- ---------- 12. 自检输出 ----------")
    A("SELECT '门店' AS item, COUNT(*) AS cnt FROM study_store")
    A("UNION ALL SELECT '座位', COUNT(*) FROM seat")
    A("UNION ALL SELECT '用户', COUNT(*) FROM user_info")
    A("UNION ALL SELECT '订单', COUNT(*) FROM booking_order")
    A("UNION ALL SELECT '预约', COUNT(*) FROM visit_appointment")
    A("UNION ALL SELECT '会员卡', COUNT(*) FROM member_card")
    A("UNION ALL SELECT '浏览记录', COUNT(*) FROM browsing_history;")
    A("SELECT status AS 订单状态, COUNT(*) AS 数量, ROUND(SUM(total_amount),2) AS 金额合计 FROM booking_order GROUP BY status ORDER BY status;")

    out = os.path.normpath(OUT)
    os.makedirs(os.path.dirname(out), exist_ok=True)
    with open(out, "w", encoding="utf-8") as f:
        f.write("\n".join(L) + "\n")
    print("written:", out, len(L), "lines;", len(seats), "seats,", len(ORDERS), "orders")


if __name__ == "__main__":
    main()
