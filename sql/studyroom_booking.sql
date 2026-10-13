-- =============================================================
-- 共享自习室预订系统 - 完整业务表 DDL
-- 数据库：studyroom_booking
-- 约定：所有表统一继承 id / create_time / update_time / is_deleted 四个公共列，
--       与 model 层 BaseEntity 一致；业务列名与实体 @TableName/@TableField 严格一一对应。
-- =============================================================

CREATE DATABASE IF NOT EXISTS `studyroom_booking`
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `studyroom_booking`;

-- =============================================================
-- 一、核心业务表
-- =============================================================

-- 自习室门店信息表
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

-- 座位信息表
DROP TABLE IF EXISTS `seat`;
CREATE TABLE `seat` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `seat_code`   VARCHAR(50)   NOT NULL COMMENT '座位编号',
    `price`       DECIMAL(10,2) NOT NULL COMMENT '座位单价（元/小时）',
    `store_id`    BIGINT        NOT NULL COMMENT '所属自习室门店id',
    `seat_type`   VARCHAR(20)            DEFAULT NULL COMMENT '座位类型：普通座/靠窗座/包间/沉浸式卡座',
    `has_power`   TINYINT(1)             DEFAULT 1 COMMENT '是否有插座 0否 1是',
    `has_light`   TINYINT(1)             DEFAULT 1 COMMENT '是否有台灯 0否 1是',
    `is_quiet`    TINYINT(1)             DEFAULT 0 COMMENT '是否静音区 0否 1是',
    `is_release`  TINYINT                DEFAULT 0 COMMENT '是否发布 0未发布 1已发布',
    `create_time` DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_seat_store` (`store_id`),
    KEY `idx_seat_code` (`seat_code`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位信息表';

-- 预订时长（时段套餐）表
DROP TABLE IF EXISTS `booking_term`;
CREATE TABLE `booking_term` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `month_count` INT         NOT NULL COMMENT '时长数值',
    `unit`        VARCHAR(10) NOT NULL COMMENT '时长单位：小时/天/月',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '预订时长表';

-- 预订订单表
DROP TABLE IF EXISTS `booking_order`;
CREATE TABLE `booking_order` (
    `id`                    BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `order_no`              VARCHAR(64)            DEFAULT NULL COMMENT '订单编号',
    `user_id`               BIGINT                 DEFAULT NULL COMMENT '预订用户id',
    `phone`                 VARCHAR(20)   NOT NULL COMMENT '联系人手机号码',
    `name`                  VARCHAR(50)   NOT NULL COMMENT '联系人姓名',
    `identification_number` VARCHAR(30)            DEFAULT NULL COMMENT '联系人身份证号码',
    `store_id`              BIGINT        NOT NULL COMMENT '自习室门店id',
    `seat_id`               BIGINT        NOT NULL COMMENT '座位id',
    `start_date`            DATE          NOT NULL COMMENT '预订开始日期',
    `end_date`              DATE          NOT NULL COMMENT '预订结束日期',
    `booking_type`          VARCHAR(20)            DEFAULT NULL COMMENT '预订类型：小时卡/日卡/月卡',
    `check_in_time`         DATETIME               DEFAULT NULL COMMENT '签到时间',
    `check_out_time`        DATETIME               DEFAULT NULL COMMENT '签退时间',
    `booking_term_id`       BIGINT                 DEFAULT NULL COMMENT '预订时长id',
    `total_amount`          DECIMAL(10,2) NOT NULL COMMENT '订单总金额（元）',
    `payment_type_id`       BIGINT                 DEFAULT NULL COMMENT '支付方式id',
    `member_card_id`        BIGINT                 DEFAULT NULL COMMENT '抵扣所用会员卡id，可为空',
    `status`                TINYINT       NOT NULL DEFAULT 1 COMMENT '预订状态 BookingStatus：1待确认 2已预订 3已取消 4已到期 5签退待确认 6已完成 7续期待确认 8使用中',
    `source_type`           TINYINT                DEFAULT NULL COMMENT '订单来源 BookingSourceType',
    `additional_info`       VARCHAR(255)           DEFAULT NULL COMMENT '备注信息',
    `create_time`           DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`           DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`            TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_order_store` (`store_id`),
    KEY `idx_order_seat` (`seat_id`),
    KEY `idx_order_user` (`user_id`),
    KEY `idx_order_status` (`status`),
    KEY `idx_order_seat_time` (`seat_id`, `start_date`, `end_date`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '预订订单表';

-- 到店体验预约表
DROP TABLE IF EXISTS `visit_appointment`;
CREATE TABLE `visit_appointment` (
    `id`                 BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`            BIGINT      NOT NULL COMMENT '用户id',
    `name`               VARCHAR(50) NOT NULL COMMENT '用户姓名',
    `phone`              VARCHAR(20) NOT NULL COMMENT '用户手机号码',
    `store_id`           BIGINT      NOT NULL COMMENT '自习室门店id',
    `appointment_time`   DATETIME    NOT NULL COMMENT '预约到店时间',
    `additional_info`    VARCHAR(255)         DEFAULT NULL COMMENT '备注信息',
    `appointment_status` TINYINT     NOT NULL DEFAULT 0 COMMENT '预约状态 AppointmentStatus：1待体验 2已改约 3已体验 4已失约 5已关闭',
    `create_time`        DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`         TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_visit_store` (`store_id`),
    KEY `idx_visit_user` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '到店体验预约表';

-- 会员卡表（自习室专属扩展：次卡/小时卡/月卡）
DROP TABLE IF EXISTS `member_card`;
CREATE TABLE `member_card` (
    `id`                 BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `card_no`            VARCHAR(64)      DEFAULT NULL COMMENT '会员卡号',
    `user_id`            BIGINT  NOT NULL COMMENT '持卡用户id',
    `store_id`           BIGINT           DEFAULT NULL COMMENT '归属自习室门店id，NULL 表示通用卡',
    `card_type`          TINYINT NOT NULL COMMENT '卡类型 CardType：1次卡 2小时卡 3月卡',
    `total_quantity`     INT     NOT NULL DEFAULT 0 COMMENT '总额度（次卡=次数，小时卡=小时数，月卡=有效天数）',
    `remaining_quantity` INT     NOT NULL DEFAULT 0 COMMENT '剩余额度，签到签退时按预订时长扣减',
    `valid_until`        DATETIME         DEFAULT NULL COMMENT '有效期至（月卡/小时卡到期时间，次卡可为空）',
    `status`             TINYINT NOT NULL DEFAULT 1 COMMENT '状态 0停用 1正常',
    `create_time`        DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`        DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`         TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_card_user` (`user_id`),
    KEY `idx_card_store` (`store_id`),
    KEY `idx_card_type` (`card_type`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '会员卡表';

-- 会员卡充值记录表
DROP TABLE IF EXISTS `card_recharge_record`;
CREATE TABLE `card_recharge_record` (
    `id`                BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `member_card_id`    BIGINT        NOT NULL COMMENT '会员卡id',
    `user_id`           BIGINT        NOT NULL COMMENT '用户id',
    `recharge_type`     TINYINT       NOT NULL COMMENT '充值对应卡类型 CardType：1次卡 2小时卡 3月卡',
    `recharge_quantity` INT           NOT NULL DEFAULT 0 COMMENT '本次充值额度',
    `amount`            DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '本次充值金额（元）',
    `additional_info`   VARCHAR(255)           DEFAULT NULL COMMENT '备注信息',
    `create_time`       DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`        TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_recharge_card` (`member_card_id`),
    KEY `idx_recharge_user` (`user_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '会员卡充值记录表';

-- 座位浏览历史表
DROP TABLE IF EXISTS `browsing_history`;
CREATE TABLE `browsing_history` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id`     BIGINT  NOT NULL COMMENT '用户id',
    `seat_id`     BIGINT  NOT NULL COMMENT '座位id',
    `browse_time` DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '浏览时间',
    `create_time` DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_history_user_time` (`user_id`, `browse_time`),
    KEY `idx_history_seat` (`seat_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位浏览历史表';

-- 图片信息表（门店与座位共用，item_type 区分归属）
DROP TABLE IF EXISTS `graph_info`;
CREATE TABLE `graph_info` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `item_type`   TINYINT     NOT NULL COMMENT '图片所属对象类型 ItemType：1自习室门店 2座位',
    `item_id`     BIGINT      NOT NULL COMMENT '所属对象id',
    `name`        VARCHAR(50)          DEFAULT NULL COMMENT '图片名称',
    `url`         VARCHAR(255)         DEFAULT NULL COMMENT '图片地址',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_graph_item` (`item_type`, `item_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '图片信息表';

-- =============================================================
-- 二、字典表
-- =============================================================

-- 设施标签字典表
DROP TABLE IF EXISTS `facility_info`;
CREATE TABLE `facility_info` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        TINYINT     NOT NULL COMMENT '设施所属对象类型 ItemType：1自习室门店 2座位',
    `name`        VARCHAR(50) NOT NULL COMMENT '设施名称',
    `icon`        VARCHAR(255)         DEFAULT NULL COMMENT '图标',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '设施标签字典表';

INSERT INTO `facility_info` (`type`, `name`) VALUES
(2, '插座'), (2, '台灯'), (2, '储物柜'), (2, '独立WiFi'), (2, '免费茶水'), (2, '打印机'),
(1, '自习区'), (1, '休息区'), (1, '免费停车'), (1, '24小时门禁');

-- 门店标签字典表
DROP TABLE IF EXISTS `label_info`;
CREATE TABLE `label_info` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `type`        TINYINT     NOT NULL COMMENT '标签所属对象类型 ItemType：1自习室门店 2座位',
    `name`        VARCHAR(50) NOT NULL COMMENT '标签名称',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '标签字典表';

INSERT INTO `label_info` (`type`, `name`) VALUES
(1, '静音区'), (1, '靠窗'), (1, '24小时'), (1, '独立隔间'), (1, '近地铁'), (1, '免费停车'),
(2, '静音区'), (2, '靠窗'), (2, '独立台灯'), (2, '包间隔间');

-- 基本属性键表 / 值表
DROP TABLE IF EXISTS `attr_key`;
CREATE TABLE `attr_key` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(50) NOT NULL COMMENT '属性名称，如座位朝向',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位基本属性键表';

DROP TABLE IF EXISTS `attr_value`;
CREATE TABLE `attr_value` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `attr_key_id` BIGINT      NOT NULL COMMENT '所属属性键id',
    `name`        VARCHAR(50) NOT NULL COMMENT '属性值名称',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_attr_value_key` (`attr_key_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位基本属性值表';

-- 杂费键表 / 值表
DROP TABLE IF EXISTS `fee_key`;
CREATE TABLE `fee_key` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(50) NOT NULL COMMENT '杂费名称，如打印费',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '杂费键表';

DROP TABLE IF EXISTS `fee_value`;
CREATE TABLE `fee_value` (
    `id`          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `fee_key_id`  BIGINT        NOT NULL COMMENT '所属杂费键id',
    `name`        VARCHAR(50)   NOT NULL COMMENT '杂费名称',
    `unit`        VARCHAR(20)            DEFAULT NULL COMMENT '计费单位，如 次/张',
    `create_time` DATETIME               DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME               DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_fee_value_key` (`fee_key_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '杂费值表';

-- 支付方式表
DROP TABLE IF EXISTS `payment_type`;
CREATE TABLE `payment_type` (
    `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`            VARCHAR(50) NOT NULL COMMENT '支付方式名称',
    `pay_month_count` VARCHAR(50)          DEFAULT NULL COMMENT '支持预订的时长数，如 1,3,6',
    `additional_info` VARCHAR(255)         DEFAULT NULL COMMENT '备注信息',
    `create_time`     DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '支付方式表';

-- 行政区划表
DROP TABLE IF EXISTS `province_info`;
CREATE TABLE `province_info` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `name`        VARCHAR(50) NOT NULL COMMENT '省份名称',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '省份信息表';

DROP TABLE IF EXISTS `city_info`;
CREATE TABLE `city_info` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `province_id` BIGINT      NOT NULL COMMENT '所属省份id',
    `name`        VARCHAR(50) NOT NULL COMMENT '城市名称',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_city_province` (`province_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '城市信息表';

DROP TABLE IF EXISTS `district_info`;
CREATE TABLE `district_info` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `city_id`     BIGINT      NOT NULL COMMENT '所属城市id',
    `name`        VARCHAR(50) NOT NULL COMMENT '区县名称',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_district_city` (`city_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '区县信息表';

-- =============================================================
-- 三、关联表
-- =============================================================

DROP TABLE IF EXISTS `store_facility`;
CREATE TABLE `store_facility` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `store_id`    BIGINT  NOT NULL COMMENT '自习室门店id',
    `facility_id` BIGINT  NOT NULL COMMENT '设施字典id',
    `create_time` DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_store_facility` (`store_id`, `facility_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '自习室门店与设施关联表';

DROP TABLE IF EXISTS `store_label`;
CREATE TABLE `store_label` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `store_id`    BIGINT  NOT NULL COMMENT '自习室门店id',
    `label_id`    BIGINT  NOT NULL COMMENT '标签字典id',
    `create_time` DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_store_label` (`store_id`, `label_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '自习室门店与标签关联表';

DROP TABLE IF EXISTS `store_fee_value`;
CREATE TABLE `store_fee_value` (
    `id`           BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `store_id`     BIGINT  NOT NULL COMMENT '自习室门店id',
    `fee_value_id` BIGINT  NOT NULL COMMENT '杂费值id',
    `create_time`  DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`   TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_store_fee` (`store_id`, `fee_value_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '自习室门店与杂费关联表';

DROP TABLE IF EXISTS `seat_facility`;
CREATE TABLE `seat_facility` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `seat_id`     BIGINT  NOT NULL COMMENT '座位id',
    `facility_id` BIGINT  NOT NULL COMMENT '设施字典id',
    `create_time` DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_seat_facility` (`seat_id`, `facility_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位与设施关联表';

DROP TABLE IF EXISTS `seat_label`;
CREATE TABLE `seat_label` (
    `id`          BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `seat_id`     BIGINT  NOT NULL COMMENT '座位id',
    `label_id`    BIGINT  NOT NULL COMMENT '标签字典id',
    `create_time` DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_seat_label` (`seat_id`, `label_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位与标签关联表';

DROP TABLE IF EXISTS `seat_attr_value`;
CREATE TABLE `seat_attr_value` (
    `id`            BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `seat_id`       BIGINT  NOT NULL COMMENT '座位id',
    `attr_value_id` BIGINT  NOT NULL COMMENT '属性值id',
    `create_time`   DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`    TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_seat_attr` (`seat_id`, `attr_value_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位与基本属性关联表';

DROP TABLE IF EXISTS `seat_payment_type`;
CREATE TABLE `seat_payment_type` (
    `id`              BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `seat_id`         BIGINT  NOT NULL COMMENT '座位id',
    `payment_type_id` BIGINT  NOT NULL COMMENT '支付方式id',
    `create_time`     DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_seat_payment` (`seat_id`, `payment_type_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位与支付方式关联表';

DROP TABLE IF EXISTS `seat_booking_term`;
CREATE TABLE `seat_booking_term` (
    `id`              BIGINT  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `seat_id`         BIGINT  NOT NULL COMMENT '座位id',
    `booking_term_id` BIGINT  NOT NULL COMMENT '预订时长id',
    `create_time`     DATETIME         DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME         DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_seat_term` (`seat_id`, `booking_term_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '座位与预订时长关联表';

-- =============================================================
-- 四、系统与用户表
-- =============================================================

DROP TABLE IF EXISTS `system_post`;
CREATE TABLE `system_post` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `code`        VARCHAR(50) NOT NULL COMMENT '岗位编码',
    `name`        VARCHAR(50) NOT NULL COMMENT '岗位名称',
    `description` VARCHAR(255)         DEFAULT NULL COMMENT '岗位描述',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 0停用 1正常',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '系统岗位表';

DROP TABLE IF EXISTS `system_user`;
CREATE TABLE `system_user` (
    `id`              BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username`        VARCHAR(50) NOT NULL COMMENT '登录账号',
    `password`        VARCHAR(100)         DEFAULT NULL COMMENT '登录密码（加密存储）',
    `name`            VARCHAR(50)          DEFAULT NULL COMMENT '姓名',
    `type`            TINYINT     NOT NULL DEFAULT 0 COMMENT '用户类型',
    `phone`           VARCHAR(20)          DEFAULT NULL COMMENT '手机号码',
    `avatar_url`      VARCHAR(255)         DEFAULT NULL COMMENT '头像地址',
    `additional_info` VARCHAR(255)         DEFAULT NULL COMMENT '备注信息',
    `post_id`         BIGINT               DEFAULT NULL COMMENT '岗位id',
    `status`          TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
    `create_time`     DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`      TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_system_user_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT '系统用户表';

DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `phone`       VARCHAR(20) NOT NULL COMMENT '手机号码（登录账号）',
    `password`    VARCHAR(100)         DEFAULT NULL COMMENT '登录密码（加密存储）',
    `avatar_url`  VARCHAR(255)         DEFAULT NULL COMMENT '头像地址',
    `nickname`    VARCHAR(50)          DEFAULT NULL COMMENT '昵称',
    `status`      TINYINT     NOT NULL DEFAULT 1 COMMENT '状态 0禁用 1正常',
    `create_time` DATETIME             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `is_deleted`  TINYINT     NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_phone` (`phone`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT 'C端用户表';

-- =============================================================
-- 五、初始化数据
-- =============================================================

INSERT INTO `booking_term` (`month_count`, `unit`) VALUES
(1, '小时'), (4, '小时'), (8, '小时'), (1, '天'), (7, '天'), (30, '天');

INSERT INTO `payment_type` (`name`, `pay_month_count`, `additional_info`) VALUES
('微信支付', '1,4,8,30', '在线支付'),
('支付宝', '1,4,8,30', '在线支付'),
('前台现金', '1,4,8,30', '到店支付'),
('会员卡抵扣', '1,4,8,30', '需持有次卡/小时卡/月卡');

INSERT INTO `province_info` (`id`, `name`) VALUES (1, '广东省');
INSERT INTO `city_info` (`id`, `province_id`, `name`) VALUES (1, 1, '深圳市');
INSERT INTO `district_info` (`id`, `city_id`, `name`) VALUES (1, 1, '南山区'), (2, 1, '福田区');

INSERT INTO `system_post` (`id`, `code`, `name`, `description`, `status`) VALUES
(1, 'admin', '超级管理员', '拥有全部权限', 1),
(2, 'store_manager', '门店管理员', '管理本店座位与订单', 1);

-- 后台登录账号：admin / 123456（password 需按项目加密方式写入实际密文）
INSERT INTO `system_user` (`username`, `name`, `type`, `phone`, `post_id`, `status`) VALUES
('admin', '系统管理员', 1, '13800000000', 1, 1);
