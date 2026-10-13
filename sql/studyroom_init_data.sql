-- =============================================================
-- 共享自习室预订系统 - 测试初始化数据
-- 数据库：studyroom_booking
-- 依赖：先执行 studyroom_booking.sql（含字典表与公共初始化数据）
-- 说明：本脚本使用显式主键，便于关联表引用；重复执行前请先清空相关表。
-- =============================================================

USE `studyroom_booking`;

-- ------------------------------------------------------------
-- 一、后台管理账号
--   登录密码采用 DigestUtils.md5Hex(明文) 存储
--   admin / 123456   →   e10adc3949ba59abbe56e057f20f883e
-- ------------------------------------------------------------
UPDATE `system_user`
SET `password` = 'e10adc3949ba59abbe56e057f20f883e'
WHERE `username` = 'admin';

INSERT INTO `system_user` (`id`, `username`, `password`, `name`, `type`, `phone`, `post_id`, `status`) VALUES
(2, 'manager', 'e10adc3949ba59abbe56e057f20f883e', '门店管理员', 1, '13800000002', 2, 1)
ON DUPLICATE KEY UPDATE `password` = VALUES(`password`);

-- ------------------------------------------------------------
-- 二、C端测试用户（移动端以手机号+短信验证码登录，首次登录会自动建号；此处预置便于关联历史/会员卡）
-- ------------------------------------------------------------
INSERT INTO `user_info` (`id`, `phone`, `nickname`, `status`) VALUES
(1, '13900000001', '测试用户-001', 1),
(2, '13900000002', '测试用户-002', 1),
(3, '13900000003', '测试用户-003', 1)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`);

-- ------------------------------------------------------------
-- 三、自习室门店（3 家，均已上架）
-- ------------------------------------------------------------
INSERT INTO `study_store`
(`id`, `name`, `introduction`, `district_id`, `district_name`, `city_id`, `city_name`,
 `province_id`, `province_name`, `address_detail`, `latitude`, `longitude`, `phone`,
 `business_hours`, `seat_count`, `is_release`) VALUES
(1, '静悦自习室（南山店）', '地铁口的沉浸式静音自习空间，配备独立台灯与储物柜。',
    1, '南山区', 1, '深圳市', 1, '广东省', '科技园南区高新南一道 100 号', '22.540000', '113.930000', '0755-88880001',
    '08:00-22:00', 8, 1),
(2, '星辰自习室（福田店）', '24 小时门禁的通宵自习馆，适合备考冲刺人群。',
    2, '福田区', 1, '深圳市', 1, '广东省', '福田CBD 福华三路 88 号', '22.550000', '114.050000', '0755-88880002',
    '00:00-23:59', 7, 1),
(3, '沉浸自习馆（南山旗舰店）', '高端包间自习馆，独立隔间与免费茶水。',
    1, '南山区', 1, '深圳市', 1, '广东省', '深圳湾科技生态园 5 栋', '22.520000', '113.940000', '0755-88880003',
    '07:00-23:00', 5, 1)
ON DUPLICATE KEY UPDATE `is_release` = VALUES(`is_release`);

-- ------------------------------------------------------------
-- 四、座位（20 个，覆盖 3 家门店）
--   门店1：座位 1-8   门店2：座位 9-15   门店3：座位 16-20
-- ------------------------------------------------------------
INSERT INTO `seat` (`id`, `seat_code`, `price`, `store_id`, `seat_type`, `has_power`, `has_light`, `is_quiet`, `is_release`) VALUES
(1,  'A-01', 6.00,  1, '普通座',   1, 1, 0, 1),
(2,  'A-02', 6.00,  1, '普通座',   1, 1, 0, 1),
(3,  'A-03', 8.00,  1, '靠窗座',   1, 1, 0, 1),
(4,  'A-04', 8.00,  1, '靠窗座',   1, 1, 1, 1),
(5,  'B-01', 10.00, 1, '沉浸式卡座', 1, 1, 1, 1),
(6,  'B-02', 10.00, 1, '沉浸式卡座', 1, 1, 1, 1),
(7,  'B-03', 15.00, 1, '包间',     1, 1, 1, 1),
(8,  'B-04', 15.00, 1, '包间',     1, 1, 1, 1),
(9,  'C-01', 5.00,  2, '普通座',   1, 1, 0, 1),
(10, 'C-02', 5.00,  2, '普通座',   1, 1, 0, 1),
(11, 'C-03', 7.00,  2, '靠窗座',   1, 1, 0, 1),
(12, 'C-04', 7.00,  2, '靠窗座',   1, 1, 1, 1),
(13, 'D-01', 9.00,  2, '沉浸式卡座', 1, 1, 1, 1),
(14, 'D-02', 12.00, 2, '包间',     1, 1, 1, 1),
(15, 'D-03', 12.00, 2, '包间',     1, 1, 1, 1),
(16, 'E-01', 12.00, 3, '沉浸式卡座', 1, 1, 1, 1),
(17, 'E-02', 12.00, 3, '沉浸式卡座', 1, 1, 1, 1),
(18, 'E-03', 18.00, 3, '包间',     1, 1, 1, 1),
(19, 'E-04', 18.00, 3, '包间',     1, 1, 1, 1),
(20, 'E-05', 20.00, 3, '包间',     1, 1, 1, 1)
ON DUPLICATE KEY UPDATE `is_release` = VALUES(`is_release`);

-- ------------------------------------------------------------
-- 五、门店与设施 / 标签关联
--   设施字典 type=1（门店）：id 7 自习区 / 8 休息区 / 9 免费停车 / 10 24小时门禁
--   标签字典 type=1（门店）：id 1 静音区 / 2 靠窗 / 3 24小时 / 4 独立隔间 / 5 近地铁 / 6 免费停车
-- ------------------------------------------------------------
INSERT INTO `store_facility` (`store_id`, `facility_id`) VALUES
(1, 7), (1, 8), (1, 9),
(2, 7), (2, 8), (2, 10),
(3, 7), (3, 8), (3, 9), (3, 10);

INSERT INTO `store_label` (`store_id`, `label_id`) VALUES
(1, 1), (1, 2), (1, 5),
(2, 1), (2, 3), (2, 5),
(3, 1), (3, 4), (3, 6);

-- ------------------------------------------------------------
-- 六、座位与设施 / 标签关联
--   设施字典 type=2（座位）：id 1 插座 / 2 台灯 / 3 储物柜 / 4 独立WiFi / 5 免费茶水 / 6 打印机
--   标签字典 type=2（座位）：id 7 静音区 / 8 靠窗 / 9 独立台灯 / 10 包间隔间
-- ------------------------------------------------------------
INSERT INTO `seat_facility` (`seat_id`, `facility_id`)
SELECT s.id, f.facility_id
FROM `seat` s
JOIN (SELECT 1 AS facility_id UNION SELECT 2 UNION SELECT 4) f
WHERE s.id BETWEEN 1 AND 20;

INSERT INTO `seat_label` (`seat_id`, `label_id`)
SELECT s.id,
       CASE
           WHEN s.seat_type = '包间' THEN 10
           WHEN s.seat_type = '靠窗座' THEN 8
           WHEN s.is_quiet = 1 THEN 7
           ELSE 9
       END
FROM `seat` s
WHERE s.id BETWEEN 1 AND 20;

-- ------------------------------------------------------------
-- 七、座位与支付方式 / 预订时长关联（每个座位支持微信/支付宝/前台现金，小时与天套餐）
--   支付方式：id 1 微信 / 2 支付宝 / 3 前台现金；预订时长：id 1 小时 / 2 4小时 / 3 8小时 / 4 天 / 5 7天
-- ------------------------------------------------------------
INSERT INTO `seat_payment_type` (`seat_id`, `payment_type_id`)
SELECT s.id, p.payment_type_id
FROM `seat` s
JOIN (SELECT 1 AS payment_type_id UNION SELECT 2 UNION SELECT 3) p
WHERE s.id BETWEEN 1 AND 20;

INSERT INTO `seat_booking_term` (`seat_id`, `booking_term_id`)
SELECT s.id, t.booking_term_id
FROM `seat` s
JOIN (SELECT 1 AS booking_term_id UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) t
WHERE s.id BETWEEN 1 AND 20;

-- ------------------------------------------------------------
-- 八、图片占位（门店 item_type=1，座位 item_type=2）
--   url 指向 common 模块内置的演示图（classpath:/static/img/studyroom），
--   两端 Spring 应用都能以相对路径直接访问；后台上传的真实图片走 /upload/**。
-- ------------------------------------------------------------
INSERT INTO `graph_info` (`item_type`, `item_id`, `name`, `url`) VALUES
(1, 1, '门店封面', '/img/studyroom/store-1.svg'),
(1, 2, '门店封面', '/img/studyroom/store-2.svg'),
(1, 3, '门店封面', '/img/studyroom/store-3.svg');

INSERT INTO `graph_info` (`item_type`, `item_id`, `name`, `url`)
SELECT 2, s.id, '座位实拍', CONCAT('/img/studyroom/seat-', s.id, '.svg')
FROM `seat` s
WHERE s.id BETWEEN 1 AND 20
  AND NOT EXISTS (SELECT 1 FROM `graph_info` g WHERE g.item_type = 2 AND g.item_id = s.id);

-- ------------------------------------------------------------
-- 九、浏览历史（驱动首页热门门店 / 热门座位缓存冷启动）
--   门店1 与座位 1/2/5、门店2 与座位 9/13、门店3 与座位 18 制造差异化热度
-- ------------------------------------------------------------
INSERT INTO `browsing_history` (`user_id`, `seat_id`) VALUES
(1, 1), (2, 1), (3, 1), (1, 1),
(2, 2), (3, 2),
(1, 5), (2, 5), (3, 5),
(1, 9), (2, 9), (3, 9),
(1, 13), (2, 13),
(1, 18), (2, 18), (3, 18), (1, 18), (2, 20), (3, 20);
