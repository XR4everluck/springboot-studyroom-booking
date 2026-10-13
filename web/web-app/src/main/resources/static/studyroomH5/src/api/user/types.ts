// 登录
export interface loginQueryInterface {
  // 登录账号
  username: string;
  // 登录密码
  password: string;
}

// 注册
export interface registerQueryInterface {
  // 登录账号，4-20位字母、数字或下划线
  username: string;
  // 登录密码，8-20位且包含字母和数字
  password: string;
  // 昵称，1-12个字符
  nickname: string;
  // 手机号（选填，作为订单联系方式）
  phone?: string;
}

// 用户信息
export interface UserInfoInterface {
  // 头像
  avatarUrl: string;
  // 用户名
  nickname: string;
}
// 用户state
export interface UserStateInterface {
  // 用户信息
  userInfo: UserInfoInterface | null;
  // token
  token: string | null;
}
