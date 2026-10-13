package com.yourname.studyroom.common.result;

import lombok.Getter;

/**
 * 统一返回结果状态信息类
 */
@Getter
public enum ResultCodeEnum {

    SUCCESS(200, "成功"),
    FAIL(201, "失败"),
    PARAM_ERROR(202, "参数不正确"),
    SERVICE_ERROR(203, "服务异常"),
    DATA_ERROR(204, "数据异常"),
    ILLEGAL_REQUEST(205, "非法请求"),
    REPEAT_SUBMIT(206, "重复提交"),
    DELETE_ERROR(207, "请先删除子集"),

    ADMIN_ACCOUNT_EXIST_ERROR(301, "账号已存在"),
    ADMIN_CAPTCHA_CODE_ERROR(302, "验证码错误"),
    ADMIN_CAPTCHA_CODE_EXPIRED(303, "验证码已过期"),
    ADMIN_CAPTCHA_CODE_NOT_FOUND(304, "未输入验证码"),


    ADMIN_LOGIN_AUTH(305, "未登陆"),
    ADMIN_ACCOUNT_NOT_EXIST_ERROR(306, "账号不存在"),
    ADMIN_ACCOUNT_ERROR(307, "用户名或密码错误"),
    ADMIN_ACCOUNT_DISABLED_ERROR(308, "该用户已被禁用"),
    ADMIN_ACCESS_FORBIDDEN(309, "无访问权限"),

    ADMIN_APARTMENT_DELETE_ERROR(310,"请先删除房间"),

    // 自习室预订相关
    SEAT_TIME_CONFLICT(401, "该座位所选时段已被预订，请选择其他时段"),
    STORE_BUSINESS_HOURS_CONFIG_ERROR(402, "门店营业时间配置异常"),
    BOOKING_NOT_IN_BUSINESS_HOURS(403, "预订时间超出门店营业时间，请重新选择"),
    APPOINTMENT_NOT_IN_BUSINESS_HOURS(404, "预约时间需在门店营业时间范围内"),
    BOOKING_ORDER_NOT_EXIST(405, "预订订单不存在"),
    BOOKING_STATUS_NOT_ALLOW_CHECK_IN(406, "当前订单状态不可签到，仅已预订订单可签到"),
    BOOKING_STATUS_NOT_ALLOW_CHECK_OUT(407, "当前订单状态不可签退，仅使用中订单可签退"),
    MEMBER_CARD_NOT_ENOUGH(408, "会员卡余次/时长不足，无法抵扣本次预订"),

    APP_LOGIN_AUTH(501, "未登陆"),
    APP_LOGIN_PHONE_EMPTY(502, "手机号码为空"),
    APP_LOGIN_CODE_EMPTY(503, "验证码为空"),
    APP_SEND_SMS_TOO_OFTEN(504, "验证法发送过于频繁"),
    APP_LOGIN_CODE_EXPIRED(505, "验证码已过期"),
    APP_LOGIN_CODE_ERROR(506, "验证码错误"),
    APP_ACCOUNT_DISABLED_ERROR(507, "该用户已被禁用"),


    TOKEN_EXPIRED(601, "token过期"),
    TOKEN_INVALID(602, "token非法");


    private final Integer code;

    private final String message;

    ResultCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
