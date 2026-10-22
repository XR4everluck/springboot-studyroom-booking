-- =============================================================
-- 上岸自习室预订系统 · 演示数据重置脚本（studyroom_demo_data.sql）
-- 用途：对外演示前一键重置并灌入完整演示数据（可重复执行）
-- 执行：mysql -uroot -p --default-character-set=utf8mb4 < sql/studyroom_demo_data.sql
-- 说明：仅重置业务数据表；区域/设施/标签/时长/支付字典与后台账号保留
-- 演示账号：H5 demo001 / 123456   后台 admin / 123456
-- =============================================================
USE studyroom_booking;
SET NAMES utf8mb4;

-- ---------- 1. 重置业务数据表 ----------
SET FOREIGN_KEY_CHECKS = 0;
TRUNCATE TABLE `booking_order`;
TRUNCATE TABLE `visit_appointment`;
TRUNCATE TABLE `browsing_history`;
TRUNCATE TABLE `member_card`;
TRUNCATE TABLE `card_recharge_record`;
TRUNCATE TABLE `seat_booking_term`;
TRUNCATE TABLE `seat_payment_type`;
TRUNCATE TABLE `seat_facility`;
TRUNCATE TABLE `seat_label`;
TRUNCATE TABLE `seat_attr_value`;
TRUNCATE TABLE `seat`;
TRUNCATE TABLE `store_facility`;
TRUNCATE TABLE `store_label`;
TRUNCATE TABLE `store_fee_value`;
TRUNCATE TABLE `graph_info`;
TRUNCATE TABLE `study_store`;
TRUNCATE TABLE `user_info`;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------- 2. 补充武汉市/区区域数据（已存在则跳过） ----------
INSERT INTO province_info (name) SELECT '湖北省' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM province_info WHERE name='湖北省');
SET @hb_id := (SELECT id FROM province_info WHERE name='湖北省' LIMIT 1);
INSERT INTO city_info (province_id, name) SELECT @hb_id, '武汉市' FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM city_info WHERE name='武汉市');
SET @wh_id := (SELECT id FROM city_info WHERE name='武汉市' LIMIT 1);
INSERT INTO district_info (city_id, name) SELECT @wh_id, t.name FROM (SELECT '洪山区' name UNION SELECT '武昌区' UNION SELECT '江汉区') t WHERE NOT EXISTS (SELECT 1 FROM district_info WHERE city_id=@wh_id AND name=t.name);
SET @hs_id := (SELECT id FROM district_info WHERE city_id=@wh_id AND name='洪山区' LIMIT 1);
SET @wc_id := (SELECT id FROM district_info WHERE city_id=@wh_id AND name='武昌区' LIMIT 1);
SET @jh_id := (SELECT id FROM district_info WHERE city_id=@wh_id AND name='江汉区' LIMIT 1);
SET @sz_id := 1; SET @ns_id := 1; SET @ft_id := 2;

-- ---------- 3. 门店（8 家） ----------
INSERT INTO `study_store` (`id`,`name`,`introduction`,`district_id`,`district_name`,`city_id`,`city_name`,`province_id`,`province_name`,`address_detail`,`latitude`,`longitude`,`phone`,`business_hours`,`seat_count`,`is_release`) VALUES
(1,'静悦自习室（南山店）','地铁口的沉浸式静音自习空间，配备独立台灯与储物柜，扫码即入座。',@ns_id,'南山区',@sz_id,'深圳市',(SELECT id FROM province_info WHERE name='广东省'),'广东省','科技园南区高新南一道 100 号',22.54,113.93,'0755-88880001','08:00-22:00',16,1),
(2,'星辰自习室（福田店）','24 小时门禁的通宵自习馆，工位宽敞、独立新风，适合备考冲刺人群。',@ft_id,'福田区',@sz_id,'深圳市',(SELECT id FROM province_info WHERE name='广东省'),'广东省','福田CBD 福华三路 88 号',22.55,114.05,'0755-88880002','00:00-23:59',14,1),
(3,'沉浸自习馆（南山旗舰店）','高端包间自习馆，独立隔间、免费茶水与打印机，考研考公首选。',@ns_id,'南山区',@sz_id,'深圳市',(SELECT id FROM province_info WHERE name='广东省'),'广东省','深圳湾科技生态园 5 栋',22.52,113.94,'0755-88880003','07:00-23:00',10,1),
(4,'静悦自习室（武汉·光谷店）','光谷软件园旁的高性价比自习空间，插座、台灯、储物柜一应俱全。',@hs_id,'洪山区',@wh_id,'武汉市',(SELECT id FROM province_info WHERE name='湖北省'),'湖北省','光谷软件园 A9 栋 2 层',30.5076,114.4085,'027-87888001','08:00-22:30',16,1),
(5,'星辰自习室（武汉·街道口店）','街道口商圈地铁上盖，靠窗景观位与静音舱组合，学习氛围浓厚。',@hs_id,'洪山区',@wh_id,'武汉市',(SELECT id FROM province_info WHERE name='湖北省'),'湖北省','街道口珞瑜路 100 号 3 层',30.5239,114.3494,'027-87888002','07:30-23:00',14,1),
(6,'沉浸自习馆（武汉·江汉路旗舰馆）','江汉路步行街旁的旗舰自习馆，独立包间隔音出色，免费咖啡茶水。',@jh_id,'江汉区',@wh_id,'武汉市',(SELECT id FROM province_info WHERE name='湖北省'),'湖北省','江汉路步行街世纪大厦 12 层',30.581,114.2894,'027-87888003','00:00-23:59',12,1),
(7,'半亩书园（武汉·昙华林店）','昙华林历史街区里的文艺自习空间，木质书桌配绿植角落，安静治愈。',@wc_id,'武昌区',@wh_id,'武汉市',(SELECT id FROM province_info WHERE name='湖北省'),'湖北省','昙华林 101 号 2 层',30.5448,114.3085,'027-87888004','09:00-21:30',10,1),
(8,'云栖书屋（武汉·光谷天地店）','光谷天地商圈的轻自习书屋，可短时自助，适合碎片时间高效学习。',@hs_id,'洪山区',@wh_id,'武汉市',(SELECT id FROM province_info WHERE name='湖北省'),'湖北省','光谷天地 F2 栋 1 层',30.493,114.4245,'027-87888005','08:30-22:00',10,1);

-- ---------- 4. 座位 ----------
INSERT INTO `seat` (`id`,`seat_code`,`price`,`store_id`,`seat_type`,`has_power`,`has_light`,`is_quiet`,`is_release`) VALUES
(1,'A-01',6.0,1,'普通座',1,1,0,1),
(2,'A-02',6.0,1,'普通座',1,1,0,1),
(3,'A-03',6.0,1,'普通座',1,1,0,1),
(4,'A-04',6.0,1,'普通座',1,1,0,1),
(5,'A-05',6.0,1,'普通座',1,1,0,1),
(6,'A-06',6.0,1,'普通座',1,1,0,1),
(7,'B-01',8.0,1,'靠窗座',1,1,0,1),
(8,'B-02',8.0,1,'靠窗座',1,1,0,1),
(9,'B-03',8.0,1,'靠窗座',1,1,0,1),
(10,'B-04',8.0,1,'靠窗座',1,1,0,1),
(11,'C-01',10.0,1,'沉浸式卡座',1,1,1,1),
(12,'C-02',10.0,1,'沉浸式卡座',1,1,1,1),
(13,'C-03',10.0,1,'沉浸式卡座',1,1,1,1),
(14,'C-04',10.0,1,'沉浸式卡座',1,1,1,1),
(15,'D-01',15.0,1,'包间',1,1,1,1),
(16,'D-02',15.0,1,'包间',1,1,1,1),
(17,'A-01',6.0,2,'普通座',1,1,0,1),
(18,'A-02',6.0,2,'普通座',1,1,0,1),
(19,'A-03',6.0,2,'普通座',1,1,0,1),
(20,'A-04',6.0,2,'普通座',1,1,0,1),
(21,'A-05',6.0,2,'普通座',1,1,0,1),
(22,'B-01',8.0,2,'靠窗座',1,1,0,1),
(23,'B-02',8.0,2,'靠窗座',1,1,0,1),
(24,'B-03',8.0,2,'靠窗座',1,1,0,1),
(25,'B-04',8.0,2,'靠窗座',1,1,0,1),
(26,'C-01',10.0,2,'沉浸式卡座',1,1,1,1),
(27,'C-02',10.0,2,'沉浸式卡座',1,1,1,1),
(28,'C-03',10.0,2,'沉浸式卡座',1,1,1,1),
(29,'D-01',15.0,2,'包间',1,1,1,1),
(30,'D-02',15.0,2,'包间',1,1,1,1),
(31,'A-01',6.0,3,'普通座',1,1,0,1),
(32,'A-02',6.0,3,'普通座',1,1,0,1),
(33,'A-03',6.0,3,'普通座',1,1,0,1),
(34,'A-04',6.0,3,'普通座',1,1,0,1),
(35,'B-01',8.0,3,'靠窗座',1,1,0,1),
(36,'B-02',8.0,3,'靠窗座',1,1,0,1),
(37,'B-03',8.0,3,'靠窗座',1,1,0,1),
(38,'C-01',10.0,3,'沉浸式卡座',1,1,1,1),
(39,'C-02',10.0,3,'沉浸式卡座',1,1,1,1),
(40,'D-01',15.0,3,'包间',1,1,1,1),
(41,'A-01',6.0,4,'普通座',1,1,0,1),
(42,'A-02',6.0,4,'普通座',1,1,0,1),
(43,'A-03',6.0,4,'普通座',1,1,0,1),
(44,'A-04',6.0,4,'普通座',1,1,0,1),
(45,'A-05',6.0,4,'普通座',1,1,0,1),
(46,'A-06',6.0,4,'普通座',1,1,0,1),
(47,'B-01',8.0,4,'靠窗座',1,1,0,1),
(48,'B-02',8.0,4,'靠窗座',1,1,0,1),
(49,'B-03',8.0,4,'靠窗座',1,1,0,1),
(50,'B-04',8.0,4,'靠窗座',1,1,0,1),
(51,'C-01',10.0,4,'沉浸式卡座',1,1,1,1),
(52,'C-02',10.0,4,'沉浸式卡座',1,1,1,1),
(53,'C-03',10.0,4,'沉浸式卡座',1,1,1,1),
(54,'C-04',10.0,4,'沉浸式卡座',1,1,1,1),
(55,'D-01',15.0,4,'包间',1,1,1,1),
(56,'D-02',15.0,4,'包间',1,1,1,1),
(57,'A-01',6.0,5,'普通座',1,1,0,1),
(58,'A-02',6.0,5,'普通座',1,1,0,1),
(59,'A-03',6.0,5,'普通座',1,1,0,1),
(60,'A-04',6.0,5,'普通座',1,1,0,1),
(61,'A-05',6.0,5,'普通座',1,1,0,1),
(62,'B-01',8.0,5,'靠窗座',1,1,0,1),
(63,'B-02',8.0,5,'靠窗座',1,1,0,1),
(64,'B-03',8.0,5,'靠窗座',1,1,0,1),
(65,'B-04',8.0,5,'靠窗座',1,1,0,1),
(66,'C-01',10.0,5,'沉浸式卡座',1,1,1,1),
(67,'C-02',10.0,5,'沉浸式卡座',1,1,1,1),
(68,'C-03',10.0,5,'沉浸式卡座',1,1,1,1),
(69,'D-01',15.0,5,'包间',1,1,1,1),
(70,'D-02',15.0,5,'包间',1,1,1,1),
(71,'A-01',6.0,6,'普通座',1,1,0,1),
(72,'A-02',6.0,6,'普通座',1,1,0,1),
(73,'A-03',6.0,6,'普通座',1,1,0,1),
(74,'A-04',6.0,6,'普通座',1,1,0,1),
(75,'A-05',6.0,6,'普通座',1,1,0,1),
(76,'B-01',8.0,6,'靠窗座',1,1,0,1),
(77,'B-02',8.0,6,'靠窗座',1,1,0,1),
(78,'B-03',8.0,6,'靠窗座',1,1,0,1),
(79,'C-01',10.0,6,'沉浸式卡座',1,1,1,1),
(80,'C-02',10.0,6,'沉浸式卡座',1,1,1,1),
(81,'D-01',15.0,6,'包间',1,1,1,1),
(82,'D-02',15.0,6,'包间',1,1,1,1),
(83,'A-01',6.0,7,'普通座',1,1,0,1),
(84,'A-02',6.0,7,'普通座',1,1,0,1),
(85,'A-03',6.0,7,'普通座',1,1,0,1),
(86,'A-04',6.0,7,'普通座',1,1,0,1),
(87,'B-01',8.0,7,'靠窗座',1,1,0,1),
(88,'B-02',8.0,7,'靠窗座',1,1,0,1),
(89,'B-03',8.0,7,'靠窗座',1,1,0,1),
(90,'C-01',10.0,7,'沉浸式卡座',1,1,1,1),
(91,'C-02',10.0,7,'沉浸式卡座',1,1,1,1),
(92,'D-01',15.0,7,'包间',1,1,1,1),
(93,'A-01',6.0,8,'普通座',1,1,0,1),
(94,'A-02',6.0,8,'普通座',1,1,0,1),
(95,'A-03',6.0,8,'普通座',1,1,0,1),
(96,'A-04',6.0,8,'普通座',1,1,0,1),
(97,'B-01',8.0,8,'靠窗座',1,1,0,1),
(98,'B-02',8.0,8,'靠窗座',1,1,0,1),
(99,'B-03',8.0,8,'靠窗座',1,1,0,1),
(100,'C-01',10.0,8,'沉浸式卡座',1,1,1,1),
(101,'C-02',10.0,8,'沉浸式卡座',1,1,1,1),
(102,'D-01',15.0,8,'包间',1,1,1,1);

-- 座位-时长（全量支持 6 档时长）
INSERT INTO seat_booking_term (seat_id, booking_term_id) SELECT s.id, t.id FROM seat s JOIN booking_term t;
-- 座位-支付方式（全量支持 4 种支付）
INSERT INTO seat_payment_type (seat_id, payment_type_id) SELECT s.id, p.id FROM seat s JOIN payment_type p;
-- 座位-设施（每座 3 项，循环轮转保证多样性）
INSERT INTO seat_facility (seat_id, facility_id)
SELECT s.id, ((s.id + k.offset) % 6) + 1 FROM seat s JOIN (SELECT 0 offset UNION SELECT 2 UNION SELECT 4) k;
-- 座位-标签（按座位类型匹配）
INSERT INTO seat_label (seat_id, label_id)
SELECT s.id, CASE s.seat_type WHEN '普通座' THEN 9 WHEN '靠窗座' THEN 8 WHEN '沉浸式卡座' THEN 7 ELSE 10 END FROM seat s;

-- ---------- 5. 门店设施/标签关联（确定性轮转，保证多样性） ----------
INSERT INTO store_facility (store_id, facility_id) SELECT s.id, f.id FROM study_store s JOIN facility_info f WHERE f.type = 2 AND ((s.id + f.id) % 5) < 3;
INSERT INTO store_label (store_id, label_id) SELECT s.id, l.id FROM study_store s JOIN label_info l WHERE l.type = 1 AND ((s.id * 2 + l.id) % 4) < 2;

-- ---------- 6. 门店/座位图片 ----------
INSERT INTO graph_info (item_type, item_id, name, url) VALUES
(1, 1, '门店封面', '/img/studyroom/store-1.svg'),
(1, 2, '门店封面', '/img/studyroom/store-2.svg'),
(1, 3, '门店封面', '/img/studyroom/store-3.svg'),
(1, 4, '门店封面', '/img/studyroom/store-4.svg'),
(1, 5, '门店封面', '/img/studyroom/store-5.svg'),
(1, 6, '门店封面', '/img/studyroom/store-6.svg'),
(1, 7, '门店封面', '/img/studyroom/store-7.svg'),
(1, 8, '门店封面', '/img/studyroom/store-8.svg'),
(2, 1, '座位实拍', '/img/studyroom/seat-1.svg'),
(2, 2, '座位实拍', '/img/studyroom/seat-2.svg'),
(2, 3, '座位实拍', '/img/studyroom/seat-3.svg'),
(2, 4, '座位实拍', '/img/studyroom/seat-4.svg'),
(2, 5, '座位实拍', '/img/studyroom/seat-5.svg'),
(2, 6, '座位实拍', '/img/studyroom/seat-6.svg'),
(2, 7, '座位实拍', '/img/studyroom/seat-7.svg'),
(2, 8, '座位实拍', '/img/studyroom/seat-8.svg'),
(2, 9, '座位实拍', '/img/studyroom/seat-9.svg'),
(2, 10, '座位实拍', '/img/studyroom/seat-10.svg'),
(2, 11, '座位实拍', '/img/studyroom/seat-11.svg'),
(2, 12, '座位实拍', '/img/studyroom/seat-12.svg'),
(2, 13, '座位实拍', '/img/studyroom/seat-13.svg'),
(2, 14, '座位实拍', '/img/studyroom/seat-14.svg'),
(2, 15, '座位实拍', '/img/studyroom/seat-15.svg'),
(2, 16, '座位实拍', '/img/studyroom/seat-16.svg'),
(2, 17, '座位实拍', '/img/studyroom/seat-17.svg'),
(2, 18, '座位实拍', '/img/studyroom/seat-18.svg'),
(2, 19, '座位实拍', '/img/studyroom/seat-19.svg'),
(2, 20, '座位实拍', '/img/studyroom/seat-20.svg'),
(2, 21, '座位实拍', '/img/studyroom/seat-1.svg'),
(2, 22, '座位实拍', '/img/studyroom/seat-2.svg'),
(2, 23, '座位实拍', '/img/studyroom/seat-3.svg'),
(2, 24, '座位实拍', '/img/studyroom/seat-4.svg'),
(2, 25, '座位实拍', '/img/studyroom/seat-5.svg'),
(2, 26, '座位实拍', '/img/studyroom/seat-6.svg'),
(2, 27, '座位实拍', '/img/studyroom/seat-7.svg'),
(2, 28, '座位实拍', '/img/studyroom/seat-8.svg'),
(2, 29, '座位实拍', '/img/studyroom/seat-9.svg'),
(2, 30, '座位实拍', '/img/studyroom/seat-10.svg'),
(2, 31, '座位实拍', '/img/studyroom/seat-11.svg'),
(2, 32, '座位实拍', '/img/studyroom/seat-12.svg'),
(2, 33, '座位实拍', '/img/studyroom/seat-13.svg'),
(2, 34, '座位实拍', '/img/studyroom/seat-14.svg'),
(2, 35, '座位实拍', '/img/studyroom/seat-15.svg'),
(2, 36, '座位实拍', '/img/studyroom/seat-16.svg'),
(2, 37, '座位实拍', '/img/studyroom/seat-17.svg'),
(2, 38, '座位实拍', '/img/studyroom/seat-18.svg'),
(2, 39, '座位实拍', '/img/studyroom/seat-19.svg'),
(2, 40, '座位实拍', '/img/studyroom/seat-20.svg'),
(2, 41, '座位实拍', '/img/studyroom/seat-1.svg'),
(2, 42, '座位实拍', '/img/studyroom/seat-2.svg'),
(2, 43, '座位实拍', '/img/studyroom/seat-3.svg'),
(2, 44, '座位实拍', '/img/studyroom/seat-4.svg'),
(2, 45, '座位实拍', '/img/studyroom/seat-5.svg'),
(2, 46, '座位实拍', '/img/studyroom/seat-6.svg'),
(2, 47, '座位实拍', '/img/studyroom/seat-7.svg'),
(2, 48, '座位实拍', '/img/studyroom/seat-8.svg'),
(2, 49, '座位实拍', '/img/studyroom/seat-9.svg'),
(2, 50, '座位实拍', '/img/studyroom/seat-10.svg'),
(2, 51, '座位实拍', '/img/studyroom/seat-11.svg'),
(2, 52, '座位实拍', '/img/studyroom/seat-12.svg'),
(2, 53, '座位实拍', '/img/studyroom/seat-13.svg'),
(2, 54, '座位实拍', '/img/studyroom/seat-14.svg'),
(2, 55, '座位实拍', '/img/studyroom/seat-15.svg'),
(2, 56, '座位实拍', '/img/studyroom/seat-16.svg'),
(2, 57, '座位实拍', '/img/studyroom/seat-17.svg'),
(2, 58, '座位实拍', '/img/studyroom/seat-18.svg'),
(2, 59, '座位实拍', '/img/studyroom/seat-19.svg'),
(2, 60, '座位实拍', '/img/studyroom/seat-20.svg'),
(2, 61, '座位实拍', '/img/studyroom/seat-1.svg'),
(2, 62, '座位实拍', '/img/studyroom/seat-2.svg'),
(2, 63, '座位实拍', '/img/studyroom/seat-3.svg'),
(2, 64, '座位实拍', '/img/studyroom/seat-4.svg'),
(2, 65, '座位实拍', '/img/studyroom/seat-5.svg'),
(2, 66, '座位实拍', '/img/studyroom/seat-6.svg'),
(2, 67, '座位实拍', '/img/studyroom/seat-7.svg'),
(2, 68, '座位实拍', '/img/studyroom/seat-8.svg'),
(2, 69, '座位实拍', '/img/studyroom/seat-9.svg'),
(2, 70, '座位实拍', '/img/studyroom/seat-10.svg'),
(2, 71, '座位实拍', '/img/studyroom/seat-11.svg'),
(2, 72, '座位实拍', '/img/studyroom/seat-12.svg'),
(2, 73, '座位实拍', '/img/studyroom/seat-13.svg'),
(2, 74, '座位实拍', '/img/studyroom/seat-14.svg'),
(2, 75, '座位实拍', '/img/studyroom/seat-15.svg'),
(2, 76, '座位实拍', '/img/studyroom/seat-16.svg'),
(2, 77, '座位实拍', '/img/studyroom/seat-17.svg'),
(2, 78, '座位实拍', '/img/studyroom/seat-18.svg'),
(2, 79, '座位实拍', '/img/studyroom/seat-19.svg'),
(2, 80, '座位实拍', '/img/studyroom/seat-20.svg'),
(2, 81, '座位实拍', '/img/studyroom/seat-1.svg'),
(2, 82, '座位实拍', '/img/studyroom/seat-2.svg'),
(2, 83, '座位实拍', '/img/studyroom/seat-3.svg'),
(2, 84, '座位实拍', '/img/studyroom/seat-4.svg'),
(2, 85, '座位实拍', '/img/studyroom/seat-5.svg'),
(2, 86, '座位实拍', '/img/studyroom/seat-6.svg'),
(2, 87, '座位实拍', '/img/studyroom/seat-7.svg'),
(2, 88, '座位实拍', '/img/studyroom/seat-8.svg'),
(2, 89, '座位实拍', '/img/studyroom/seat-9.svg'),
(2, 90, '座位实拍', '/img/studyroom/seat-10.svg'),
(2, 91, '座位实拍', '/img/studyroom/seat-11.svg'),
(2, 92, '座位实拍', '/img/studyroom/seat-12.svg'),
(2, 93, '座位实拍', '/img/studyroom/seat-13.svg'),
(2, 94, '座位实拍', '/img/studyroom/seat-14.svg'),
(2, 95, '座位实拍', '/img/studyroom/seat-15.svg'),
(2, 96, '座位实拍', '/img/studyroom/seat-16.svg'),
(2, 97, '座位实拍', '/img/studyroom/seat-17.svg'),
(2, 98, '座位实拍', '/img/studyroom/seat-18.svg'),
(2, 99, '座位实拍', '/img/studyroom/seat-19.svg'),
(2, 100, '座位实拍', '/img/studyroom/seat-20.svg'),
(2, 101, '座位实拍', '/img/studyroom/seat-1.svg'),
(2, 102, '座位实拍', '/img/studyroom/seat-2.svg');

-- ---------- 7. 用户（10 人，密码统一 123456） ----------
INSERT INTO `user_info` (`id`,`username`,`phone`,`password`,`nickname`,`status`) VALUES
(1,'demo001','13900000001','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','陈晓晓',1),
(2,'demo002','13900000002','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','林之然',1),
(3,'demo003','13900000003','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','苏晚晴',1),
(4,'student001','13900000004','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','周子墨',1),
(5,'student002','13900000005','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','何雨桐',1),
(6,'student003','13900000006','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','郑安然',1),
(7,'student004','13900000007','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','罗一鸣',1),
(8,'worker001','13900000008','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','高子期',1),
(9,'worker002','13900000009','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','方梓涵',1),
(10,'teacher001','13900000010','pbkdf2$100000$sVhfu/ejn14wtOF5Lo7/Aw==$TekJIGspdzLzFJNu35bprwTphtTpOtgQCFsZN5CM+EM=','唐书白',1);

-- ---------- 8. 会员卡与充值记录 ----------
INSERT INTO `member_card` (`id`,`card_no`,`user_id`,`store_id`,`card_type`,`total_quantity`,`remaining_quantity`,`valid_until`,`status`) VALUES
(1,'MC20260137',1,1,1,10,10,DATE_ADD(CURDATE(), INTERVAL 180 DAY),1),
(2,'MC20260274',2,1,2,50,50,DATE_ADD(CURDATE(), INTERVAL 180 DAY),1),
(3,'MC20260411',4,2,3,1,1,DATE_ADD(CURDATE(), INTERVAL 180 DAY),1),
(4,'MC20260548',5,2,1,20,20,DATE_ADD(CURDATE(), INTERVAL 180 DAY),1),
(5,'MC20260685',6,4,2,30,30,DATE_ADD(CURDATE(), INTERVAL 180 DAY),1),
(6,'MC20260822',7,4,3,1,1,DATE_ADD(CURDATE(), INTERVAL 180 DAY),1);
INSERT INTO `card_recharge_record` (`member_card_id`,`user_id`,`recharge_type`,`recharge_quantity`,`amount`,`additional_info`) VALUES
(1,1,1,10,100.0,'次卡首次开通'),
(2,2,1,50,300.0,'小时卡首次开通'),
(3,4,1,1,299.0,'月卡首次开通'),
(4,5,1,20,190.0,'次卡首次开通'),
(5,6,1,30,180.0,'小时卡首次开通'),
(6,7,1,1,299.0,'月卡首次开通');

-- ---------- 9. 订单（30 笔，覆盖 8 种状态，时间随执行日自动推算） ----------
INSERT INTO `booking_order` (`user_id`,`phone`,`name`,`store_id`,`seat_id`,`start_date`,`end_date`,`booking_type`,`check_in_time`,`check_out_time`,`booking_term_id`,`total_amount`,`payment_type_id`,`member_card_id`,`status`,`source_type`,`additional_info`,`create_time`,`update_time`) VALUES
(1,'13900000001','陈晓晓',1,1,CURDATE() + INTERVAL -6 DAY,CURDATE() + INTERVAL -5 DAY,'1小时',CONCAT(CURDATE() + INTERVAL -6 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL -6 DAY, ' 17:30:00'),1,6.0,1,NULL,6,1,'',CONCAT(CURDATE() + INTERVAL -6 DAY, ' 10:00:00'),NOW()),
(2,'13900000002','林之然',1,5,CURDATE() + INTERVAL -5 DAY,CURDATE() + INTERVAL -4 DAY,'4小时',CONCAT(CURDATE() + INTERVAL -5 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL -5 DAY, ' 17:30:00'),2,24.0,2,NULL,6,1,'',CONCAT(CURDATE() + INTERVAL -6 DAY, ' 10:00:00'),NOW()),
(4,'13900000004','周子墨',2,17,CURDATE() + INTERVAL -4 DAY,CURDATE() + INTERVAL -3 DAY,'8小时',CONCAT(CURDATE() + INTERVAL -4 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL -4 DAY, ' 17:30:00'),3,48.0,1,NULL,6,1,'',CONCAT(CURDATE() + INTERVAL -5 DAY, ' 10:00:00'),NOW()),
(5,'13900000005','何雨桐',2,21,CURDATE() + INTERVAL -4 DAY,CURDATE() + INTERVAL -3 DAY,'1小时',CONCAT(CURDATE() + INTERVAL -4 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL -4 DAY, ' 17:30:00'),1,6.0,1,NULL,6,1,'',CONCAT(CURDATE() + INTERVAL -5 DAY, ' 10:00:00'),NOW()),
(1,'13900000001','陈晓晓',1,7,CURDATE() + INTERVAL -3 DAY,CURDATE() + INTERVAL -2 DAY,'8小时',CONCAT(CURDATE() + INTERVAL -3 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL -3 DAY, ' 17:30:00'),3,64.0,4,1,6,1,'会员卡抵扣',CONCAT(CURDATE() + INTERVAL -4 DAY, ' 10:00:00'),NOW()),
(6,'13900000006','郑安然',4,41,CURDATE() + INTERVAL -3 DAY,CURDATE() + INTERVAL -2 DAY,'1小时',CONCAT(CURDATE() + INTERVAL -3 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL -3 DAY, ' 17:30:00'),1,6.0,1,NULL,6,1,'',CONCAT(CURDATE() + INTERVAL -3 DAY, ' 10:00:00'),NOW()),
(7,'13900000007','罗一鸣',4,45,CURDATE() + INTERVAL -2 DAY,CURDATE() + INTERVAL -1 DAY,'4小时',CONCAT(CURDATE() + INTERVAL -2 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL -2 DAY, ' 17:30:00'),2,24.0,2,NULL,6,1,'',CONCAT(CURDATE() + INTERVAL -2 DAY, ' 10:00:00'),NOW()),
(8,'13900000008','高子期',5,57,CURDATE() + INTERVAL -1 DAY,CURDATE() + INTERVAL 0 DAY,'1天',CONCAT(CURDATE() + INTERVAL -1 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL 0 DAY, ' 17:30:00'),4,48.0,1,NULL,6,1,'',CONCAT(CURDATE() + INTERVAL -2 DAY, ' 10:00:00'),NOW()),
(9,'13900000009','方梓涵',5,61,CURDATE() + INTERVAL -1 DAY,CURDATE() + INTERVAL 0 DAY,'1小时',CONCAT(CURDATE() + INTERVAL -1 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL -1 DAY, ' 17:30:00'),1,6.0,3,NULL,6,1,'',CONCAT(CURDATE() + INTERVAL -1 DAY, ' 10:00:00'),NOW()),
(10,'13900000010','唐书白',3,31,CURDATE(),CURDATE() + INTERVAL 1 DAY,'4小时',CONCAT(CURDATE() + INTERVAL 0 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL 0 DAY, ' 17:30:00'),2,24.0,4,NULL,6,1,'今日已完成',NOW(),NOW()),
(2,'13900000002','林之然',1,2,CURDATE(),CURDATE() + INTERVAL 1 DAY,'1小时',CONCAT(CURDATE() + INTERVAL 0 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL 0 DAY, ' 17:30:00'),1,6.0,1,NULL,6,1,'',NOW(),NOW()),
(4,'13900000004','周子墨',2,18,CURDATE(),CURDATE() + INTERVAL 1 DAY,'1小时',CONCAT(CURDATE() + INTERVAL 0 DAY, ' 09:00:00'),CONCAT(CURDATE() + INTERVAL 0 DAY, ' 17:30:00'),1,6.0,1,NULL,6,1,'',NOW(),NOW()),
(1,'13900000001','陈晓晓',1,3,CURDATE(),CURDATE() + INTERVAL 1 DAY,'1小时',CONCAT(CURDATE() + INTERVAL 0 DAY, ' 09:00:00'),NULL,1,6.0,1,NULL,8,1,'演示主订单：可续期/签退',NOW(),NOW()),
(3,'13900000003','苏晚晴',1,8,CURDATE(),CURDATE() + INTERVAL 1 DAY,'4小时',CONCAT(CURDATE() + INTERVAL 0 DAY, ' 09:00:00'),NULL,2,32.0,2,NULL,8,1,'',NOW(),NOW()),
(5,'13900000005','何雨桐',2,25,CURDATE(),CURDATE() + INTERVAL 1 DAY,'8小时',CONCAT(CURDATE() + INTERVAL 0 DAY, ' 09:00:00'),NULL,3,64.0,1,NULL,8,1,'',NOW(),NOW()),
(2,'13900000002','林之然',1,6,CURDATE() + INTERVAL -1 DAY,CURDATE() + INTERVAL 0 DAY,'1小时',CONCAT(CURDATE() + INTERVAL -1 DAY, ' 09:00:00'),NULL,1,6.0,1,2,7,2,'续期申请，后台可审批',CONCAT(CURDATE() + INTERVAL -1 DAY, ' 10:00:00'),NOW()),
(7,'13900000007','罗一鸣',4,46,CURDATE(),CURDATE() + INTERVAL 1 DAY,'4小时',CONCAT(CURDATE() + INTERVAL 0 DAY, ' 09:00:00'),NULL,2,24.0,2,6,7,2,'续期申请，后台可审批',CONCAT(CURDATE() + INTERVAL -1 DAY, ' 10:00:00'),NOW()),
(4,'13900000004','周子墨',2,22,CURDATE(),CURDATE() + INTERVAL 1 DAY,'8小时',CONCAT(CURDATE() + INTERVAL 0 DAY, ' 09:00:00'),NULL,3,64.0,1,NULL,5,1,'提前签退待确认',NOW(),NOW()),
(6,'13900000006','郑安然',4,42,CURDATE(),CURDATE() + INTERVAL 1 DAY,'1小时',CONCAT(CURDATE() + INTERVAL 0 DAY, ' 09:00:00'),NULL,1,6.0,1,NULL,5,1,'提前签退待确认',NOW(),NOW()),
(1,'13900000001','陈晓晓',1,4,CURDATE() + INTERVAL 1 DAY,CURDATE() + INTERVAL 8 DAY,'7天',NULL,NULL,5,336.0,2,NULL,1,1,'周卡预订',NOW(),NOW()),
(3,'13900000003','苏晚晴',3,32,CURDATE() + INTERVAL 1 DAY,CURDATE() + INTERVAL 2 DAY,'8小时',NULL,NULL,3,48.0,1,NULL,1,1,'',NOW(),NOW()),
(8,'13900000008','高子期',5,58,CURDATE() + INTERVAL 1 DAY,CURDATE() + INTERVAL 31 DAY,'30天',NULL,NULL,6,1440.0,2,NULL,1,1,'月卡预订',NOW(),NOW()),
(2,'13900000002','林之然',1,9,CURDATE() + INTERVAL 1 DAY,CURDATE() + INTERVAL 2 DAY,'8小时',NULL,NULL,3,64.0,4,2,2,1,'会员卡预订',CONCAT(CURDATE() + INTERVAL -1 DAY, ' 10:00:00'),NOW()),
(5,'13900000005','何雨桐',2,20,CURDATE() + INTERVAL 1 DAY,CURDATE() + INTERVAL 2 DAY,'1小时',NULL,NULL,1,6.0,1,NULL,2,1,'',NOW(),NOW()),
(9,'13900000009','方梓涵',5,62,CURDATE() + INTERVAL 1 DAY,CURDATE() + INTERVAL 2 DAY,'4小时',NULL,NULL,2,32.0,2,NULL,2,1,'',CONCAT(CURDATE() + INTERVAL -1 DAY, ' 10:00:00'),NOW()),
(3,'13900000003','苏晚晴',1,1,CURDATE() + INTERVAL -3 DAY,CURDATE() + INTERVAL -2 DAY,'1小时',NULL,NULL,1,6.0,1,NULL,3,1,'用户主动取消',CONCAT(CURDATE() + INTERVAL -4 DAY, ' 10:00:00'),NOW()),
(6,'13900000006','郑安然',4,43,CURDATE() + INTERVAL -3 DAY,CURDATE() + INTERVAL -2 DAY,'4小时',NULL,NULL,2,24.0,1,NULL,3,1,'行程冲突取消',CONCAT(CURDATE() + INTERVAL -4 DAY, ' 10:00:00'),NOW()),
(10,'13900000010','唐书白',3,33,CURDATE() + INTERVAL -3 DAY,CURDATE() + INTERVAL -2 DAY,'1小时',NULL,NULL,1,6.0,3,NULL,3,1,'',CONCAT(CURDATE() + INTERVAL -4 DAY, ' 10:00:00'),NOW()),
(7,'13900000007','罗一鸣',4,41,CURDATE() + INTERVAL -7 DAY,CURDATE() + INTERVAL -6 DAY,'1小时',NULL,NULL,1,6.0,1,NULL,4,1,'未到店核销，自动到期',CONCAT(CURDATE() + INTERVAL -8 DAY, ' 10:00:00'),NOW()),
(8,'13900000008','高子期',5,59,CURDATE() + INTERVAL -7 DAY,CURDATE() + INTERVAL -6 DAY,'1小时',NULL,NULL,1,6.0,1,NULL,4,1,'未到店核销，自动到期',CONCAT(CURDATE() + INTERVAL -8 DAY, ' 10:00:00'),NOW());
-- 统一生成规范订单号：SO + 日期 + 6位序号
UPDATE booking_order SET order_no = CONCAT('SO', DATE_FORMAT(create_time, '%Y%m%d'), LPAD(id, 6, '0')) WHERE order_no IS NULL OR order_no = '';

-- ---------- 10. 到店体验预约 ----------
INSERT INTO `visit_appointment` (`user_id`,`name`,`phone`,`store_id`,`appointment_time`,`additional_info`,`appointment_status`,`create_time`) VALUES
(1,'陈晓晓','13900000001',1,TIMESTAMP(CURDATE(), '15:00:00'),'想实地看看靠窗座位的环境',1,DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2,'林之然','13900000002',3,TIMESTAMP(CURDATE(), '10:30:00'),'第一次来，请老师介绍下包间',1,DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4,'周子墨','13900000004',4,TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 3 DAY), '14:00:00'),'已体验，环境满意',3,DATE_SUB(NOW(), INTERVAL 4 DAY)),
(5,'何雨桐','13900000005',5,TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 5 DAY), '16:30:00'),'已体验，办理了会员卡',3,DATE_SUB(NOW(), INTERVAL 6 DAY)),
(6,'郑安然','13900000006',6,TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 2 DAY), '11:00:00'),'临时有事取消',2,DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7,'罗一鸣','13900000007',7,TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 7 DAY), '09:30:00'),'已体验',3,DATE_SUB(NOW(), INTERVAL 8 DAY));

-- ---------- 11. 浏览历史（近 7 日 40+ 条） ----------
INSERT INTO browsing_history (user_id, seat_id, browse_time)
SELECT u.id, s.id, NOW() - INTERVAL ((u.id * 13 + s.id * 7) % 160) HOUR
FROM user_info u JOIN seat s WHERE (u.id * 31 + s.id) % 25 = 0;

-- ---------- 12. 自检输出 ----------
SELECT '门店' AS item, COUNT(*) AS cnt FROM study_store
UNION ALL SELECT '座位', COUNT(*) FROM seat
UNION ALL SELECT '用户', COUNT(*) FROM user_info
UNION ALL SELECT '订单', COUNT(*) FROM booking_order
UNION ALL SELECT '预约', COUNT(*) FROM visit_appointment
UNION ALL SELECT '会员卡', COUNT(*) FROM member_card
UNION ALL SELECT '浏览记录', COUNT(*) FROM browsing_history;
SELECT status AS 订单状态, COUNT(*) AS 数量, ROUND(SUM(total_amount),2) AS 金额合计 FROM booking_order GROUP BY status ORDER BY status;
