-- =============================================================
-- 共享自习室预订系统 - 核心业务表 DDL（阶段二：数据模型层改造）
-- 数据库：studyroom_booking  (原 lease)
-- 说明：由原公寓项目的 apartment_info / room_info / lease_agreement /
--       view_appointment 改造为 study_store / seat / booking_order /
--       visit_appointment；字段类型与索引结构保持与原项目一致，仅替换业务语义。
-- =============================================================

CREATE DATABASE IF NOT EXISTS `studyroom_booking`
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `studyroom_booking`;

-- -------------------------------------------------------------
-- 自习室门店信息表（原 apartment_info）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `study_store`;
CREATE TABLE `study_store` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`           VARCHAR(100) NOT NULL COMMENT '自习室门店名称',
    `introduction`   VARCHAR(255)          DEFAULT NULL COMMENT '自习室门店介绍',
    `district_id`    BIGINT                DEFAULT NULL COMMENT '所处区域id',
    `district_name`  VARCHAR(50)           DEFAULT NULL COMMENT '所处区域名称',
    `city_id`        BIGINT                DEFAULT NULL COMMENT '所处城市id',
    `city_name`      VARCHAR(50)           DEFAULT NULL COMMENT '所处城市名称',
    `province_id`    BIGINT                DEFAULT NULL COMMENT '所处省份id',
    `province_name`  VARCHAR(50)           DEFAULT NULL COMMENT '所处省份名称',
    `address_detail` VARCHAR(255)          DEFAULT NULL COMMENT '详细地址',
    `latitude`       VARCHAR(30)           DEFAULT NULL COMMENT '纬度',
    `longitude`      VARCHAR(30)           DEFAULT NULL COMMENT '经度',
    `phone`          VARCHAR(20)           DEFAULT NULL COMMENT '自习室门店前台电话',
    `business_hours` VARCHAR(50)           DEFAULT NULL COMMENT '营业时间，如 08:00-22:00',
    `seat_count`     INT                   DEFAULT 0 COMMENT '座位总数',
    `is_release`     TINYINT               DEFAULT 0 COMMENT '是否发布 0未发布 1已发布',
    `create_time`    DATETIME              DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    DATETIME              DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`     TINYINT      NOT NULL DEFAULT 0 COMMENT '逻辑删除 0未删除 1已删除',
    PRIMARY KEY (`id`),
    KEY `idx_store_city` (`city_id`),
    KEY `idx_store_district` (`district_id`),
    KEY `idx_store_province` (`province_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '自习室门店信息表';

-- -------------------------------------------------------------
-- 座位信息表（原 room_info）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `seat`;
CREATE TABLE `seat` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `seat_code`   VARCHAR(50)   NOT NULL COMMENT '座位编号（原 room_number）',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '价格（原 rent）',
    `store_id`    BIGINT        NOT NULL COMMENT '所属自习室门店id（原 apartment_id）',
    `seat_type`   VARCHAR(20)            DEFAULT NULL COMMENT '座位类型：普通座/靠窗座/包间/沉浸式卡座',
    `has_power`   TINYINT(1)             DEFAULT 1 COMMENT '是否有插座 0否 1是',
    `has_light`   TINYINT(1)             DEFAULT 1 COMMENT '是否有台灯 0否 1是',
    `is_quiet`    TINYINT(1)             DEFAULT 0 COMMENT '是否静音区 0否 1是',
    `is_release`  TINYINT                DEFAULT 0 COMMENT '是否发布 0未发布 1已发布',
    `create_time` DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_seat_store` (`store_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位信息表';

-- -------------------------------------------------------------
-- 预订订单表（原 lease_agreement）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `booking_order`;
CREATE TABLE `booking_order` (
    `id`                     BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`               VARCHAR(64)            DEFAULT NULL COMMENT '订单编号（新增）',
    `user_id`                BIGINT                 DEFAULT NULL COMMENT '预订用户id（新增）',
    `phone`                  VARCHAR(20)   NOT NULL COMMENT '联系人手机号码',
    `name`                   VARCHAR(50)   NOT NULL COMMENT '联系人姓名',
    `identification_number`  VARCHAR(30)            DEFAULT NULL COMMENT '联系人身份证号码',
    `store_id`               BIGINT        NOT NULL COMMENT '自习室门店id（原 apartment_id）',
    `seat_id`                BIGINT        NOT NULL COMMENT '座位id（原 room_id）',
    `start_date`             DATE          NOT NULL COMMENT '预订开始日期（原 lease_start_date）',
    `end_date`               DATE          NOT NULL COMMENT '预订结束日期（原 lease_end_date）',
    `booking_type`           VARCHAR(20)            DEFAULT NULL COMMENT '预订类型：小时卡/日卡/月卡（新增）',
    `check_in_time`          DATETIME               DEFAULT NULL COMMENT '签到时间（新增）',
    `check_out_time`         DATETIME               DEFAULT NULL COMMENT '签退时间（新增）',
    `lease_term_id`          BIGINT                 DEFAULT NULL COMMENT '租期id',
    `total_amount`           DECIMAL(10,2) NOT NULL COMMENT '订单总金额（原 rent，已删除 deposit 押金列）',
    `payment_type_id`        BIGINT                 DEFAULT NULL COMMENT '支付类型id',
    `member_card_id`         BIGINT                 DEFAULT NULL COMMENT '抵扣所用会员卡id（新增，可空）',
    `status`                 TINYINT       NOT NULL DEFAULT 1 COMMENT '预订状态 BookingStatus: 1待确认 2已预订 3已取消 4已到期 5签退待确认 6已完成 7续期待确认 8使用中',
    `source_type`            TINYINT                DEFAULT NULL COMMENT '订单来源 LeaseSourceType',
    `additional_info`        VARCHAR(255)           DEFAULT NULL COMMENT '备注信息',
    `create_time`            DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`            DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`             TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_order_store` (`store_id`),
    KEY `idx_order_seat` (`seat_id`),
    KEY `idx_order_user` (`user_id`),
    KEY `idx_order_status` (`status`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '预订订单表';

-- -------------------------------------------------------------
-- 到店体验预约表（原 view_appointment）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `visit_appointment`;
CREATE TABLE `visit_appointment` (
    `id`                 BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`            BIGINT      NOT NULL COMMENT '用户id',
    `name`               VARCHAR(50) NOT NULL COMMENT '用户姓名',
    `phone`              VARCHAR(20) NOT NULL COMMENT '用户手机号码',
    `store_id`           BIGINT      NOT NULL COMMENT '自习室门店id（原 apartment_id）',
    `appointment_time`   DATETIME    NOT NULL COMMENT '预约时间',
    `additional_info`    VARCHAR(255)         DEFAULT NULL COMMENT '备注信息',
    `appointment_status` TINYINT     NOT NULL DEFAULT 0 COMMENT '预约状态 AppointmentStatus',
    `create_time`        DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`         TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_visit_store` (`store_id`),
    KEY `idx_visit_user` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '到店体验预约表';

-- -------------------------------------------------------------
-- 会员卡表（阶段六新增，自习室专属扩展：次卡/小时卡/月卡）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `member_card`;
CREATE TABLE `member_card` (
    `id`                 BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `card_no`            VARCHAR(64)            DEFAULT NULL COMMENT '会员卡号',
    `user_id`            BIGINT        NOT NULL COMMENT '持卡用户id',
    `store_id`           BIGINT                 DEFAULT NULL COMMENT '归属自习室门店id，NULL 表示通用卡',
    `card_type`          TINYINT       NOT NULL COMMENT '卡类型 CardType: 1次卡 2小时卡 3月卡',
    `total_quantity`     INT           NOT NULL DEFAULT 0 COMMENT '总额度（次卡=次数，小时卡=小时数，月卡=有效天数）',
    `remaining_quantity` INT           NOT NULL DEFAULT 0 COMMENT '剩余额度（月卡表示剩余有效天数字节，签到签退时按预订时长扣减）',
    `valid_until`        DATETIME               DEFAULT NULL COMMENT '有效期至（月卡/小时卡到期时间，次卡可为空）',
    `status`             TINYINT       NOT NULL DEFAULT 1 COMMENT '状态 0停用 1正常',
    `create_time`        DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`         TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_card_user` (`user_id`),
    KEY `idx_card_store` (`store_id`),
    KEY `idx_card_type` (`card_type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '会员卡表';

-- -------------------------------------------------------------
-- 会员卡充值记录表（阶段六新增）
-- -------------------------------------------------------------
DROP TABLE IF EXISTS `card_recharge_record`;
CREATE TABLE `card_recharge_record` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `member_card_id`  BIGINT        NOT NULL COMMENT '会员卡id',
    `user_id`         BIGINT        NOT NULL COMMENT '用户id',
    `recharge_type`   TINYINT       NOT NULL COMMENT '充值对应卡类型 CardType: 1次卡 2小时卡 3月卡',
    `recharge_quantity` INT         NOT NULL DEFAULT 0 COMMENT '本次充值额度',
    `amount`          DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '本次充值金额（元）',
    `additional_info` VARCHAR(255)           DEFAULT NULL COMMENT '备注信息',
    `create_time`     DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_recharge_card` (`member_card_id`),
    KEY `idx_recharge_user` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '会员卡充值记录表';

-- =============================================================
-- 字典表结构与初始化数据（自习室相关内容）
-- type: 1=门店(APARTMENT) 2=座位/房间(ROOM)  —— 沿用 ItemType 编码
-- =============================================================

-- 座位设施标签（原 facility_info，实体改名 SeatFacility，表名不变）
DROP TABLE IF EXISTS `facility_info`;
CREATE TABLE `facility_info` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        TINYINT     NOT NULL COMMENT '设施所属对象类型',
    `name`        VARCHAR(50) NOT NULL COMMENT '名称',
    `icon`        VARCHAR(255)         DEFAULT NULL COMMENT '图标',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位设施标签信息表';

INSERT INTO `facility_info` (`type`, `name`, `icon`) VALUES
(2, '插座', NULL),
(2, '台灯', NULL),
(2, '储物柜', NULL),
(2, '独立WiFi', NULL),
(2, '免费茶水', NULL),
(2, '打印机', NULL);

-- 门店标签（原 label_info，实体改名 StoreLabel，表名不变）
DROP TABLE IF EXISTS `label_info`;
CREATE TABLE `label_info` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        TINYINT     NOT NULL COMMENT '类型',
    `name`        VARCHAR(50) NOT NULL COMMENT '标签名称',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '门店标签信息表';

INSERT INTO `label_info` (`type`, `name`) VALUES
(1, '静音区'),
(1, '靠窗'),
(1, '24小时'),
(1, '独立隔间'),
(1, '近地铁'),
(1, '免费停车');
