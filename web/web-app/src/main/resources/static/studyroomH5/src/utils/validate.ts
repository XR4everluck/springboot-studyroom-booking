/**
 * @param {string} path
 * @returns {Boolean}
 */
export function isExternal(path: string) {
  return /^(https?:|mailto:|tel:)/.test(path);
}

/**
 * @description 账号密码体系的校验规则单一来源
 * 与后端 LoginServiceImpl 中的正则保持一致，避免前后端两处漂移
 */
// 登录账号：4-20 位字母、数字或下划线
export const USERNAME_PATTERN = /^[a-zA-Z0-9_]{4,20}$/;
// 登录密码：8-20 位，且同时包含字母和数字
export const PASSWORD_PATTERN = /^(?=.*[A-Za-z])(?=.*\d).{8,20}$/;
// 手机号（选填，作为订单联系方式）
export const PHONE_PATTERN = /^1[3-9]\d{9}$/;

export const USERNAME_RULE_MESSAGE = "账号需为4-20位字母、数字或下划线";
export const PASSWORD_RULE_MESSAGE = "密码需为8-20位，且同时包含字母和数字";

/**
 * @description 计算密码强度，用于注册/改密时的友好提示
 * @param {string} password 明文密码
 * @returns {number} 0 空 / 1 弱 / 2 中 / 3 强
 */
export function passwordStrength(password: string): number {
  if (!password) {
    return 0;
  }
  // 不满足基础规则时一律视为弱
  if (!PASSWORD_PATTERN.test(password)) {
    return 1;
  }
  let variety = 0;
  if (/[a-z]/.test(password)) variety += 1;
  if (/[A-Z]/.test(password)) variety += 1;
  if (/\d/.test(password)) variety += 1;
  if (/[^A-Za-z0-9]/.test(password)) variety += 1;
  // 字符种类越多、长度越长，强度越高
  if (variety >= 3 || password.length >= 12) {
    return 3;
  }
  if (variety >= 2 && password.length >= 8) {
    return 2;
  }
  return 1;
}

/**
 * @description 密码强度对应的文案与样式类名
 */
export function passwordStrengthMeta(password: string) {
  switch (passwordStrength(password)) {
    case 3:
      return { text: "强", level: 3, className: "is-strong" };
    case 2:
      return { text: "中", level: 2, className: "is-medium" };
    case 1:
      return { text: "弱", level: 1, className: "is-weak" };
    default:
      return { text: "", level: 0, className: "" };
  }
}
