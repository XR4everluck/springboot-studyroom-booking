import http from "@/utils/http";
import type {
  loginQueryInterface,
  registerQueryInterface,
  changePasswordQueryInterface,
  UserInfoInterface
} from "@/api/user/types";

/**
 * @description 账号密码登录
 * @param params
 */
export function login(params: loginQueryInterface) {
  return http.post<string>(`/app/login`, params);
}

/**
 * @description 账号注册
 * @param params
 */
export function register(params: registerQueryInterface) {
  return http.post<null>(`/app/register`, params);
}

/**
 * @description 修改密码（需登录）
 * @param params
 */
export function changePassword(params: changePasswordQueryInterface) {
  return http.post<null>(`/app/password`, params);
}

/**
 * @description 获取用户信息
 */
export function getUserInfo() {
  return http.get<UserInfoInterface>(`/app/info`);
}
