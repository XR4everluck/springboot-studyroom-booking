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
  // 昵称，选填，1-12个字符，不传时后端默认使用登录账号
  nickname?: string;
}

// 修改密码
export interface changePasswordQueryInterface {
  // 原密码
  oldPassword: string;
  // 新密码，8-20位且包含字母和数字
  newPassword: string;
  // 确认新密码
  confirmPassword: string;
}

// 用户信息
export interface UserInfoInterface {
  // 头像
  avatarUrl: string;
  // 用户名
  nickname: string;
  // 是否需强制改密 1是（管理员重置密码后）
  mustChangePwd?: number;
}
// 用户state
export interface UserStateInterface {
  // 用户信息
  userInfo: UserInfoInterface | null;
  // token
  token: string | null;
}
