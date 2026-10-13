/*
 * @Author: cqq 945327638@qq.com
 * @Date: 2023-05-23 10:34:35
 * @LastEditors: cqq 945327638@qq.com
 * @LastEditTime: 2023-07-14 09:12:05
 * @FilePath: \listen-to-books-admin\src\enums\constEnums.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */

export function getLabelByValue(
  arr: {
    label: string
    value: string | number | boolean
  }[],
  value: string | number | boolean,
): string {
  const item = arr.find((item) => item.value === value)
  return item ? item.label : ''
}
// 按钮权限
export const ButtonPermission = {
  // 用户管理
  SysUser: {
    List: 'btn.sysUser.list',
    Add: 'btn.sysUser.add',
    Update: 'btn.sysUser.update',
    Remove: 'btn.sysUser.remove',
    AssignRole: 'btn.sysUser.assignRole',
  },
  // 角色管理
  SysRole: {
    List: 'btn.sysRole.list',
    Add: 'btn.sysRole.add',
    Update: 'btn.sysRole.update',
    Remove: 'btn.sysRole.remove',
    AssignAuth: 'btn.sysRole.assignAuth',
  },
  // 菜单管理
  SysMenu: {
    List: 'btn.sysMenu.list',
    Add: 'btn.sysMenu.add',
    Update: 'btn.sysMenu.update',
    Remove: 'btn.sysMenu.remove',
  },
  // 岗位管理
  SysPost: {
    List: 'btn.sysPost.list',
    Add: 'btn.sysPost.add',
    Update: 'btn.sysPost.update',
    Remove: 'btn.sysPost.remove',
  },
  // 门店管理
  Apartment: {
    //   门店管理
    Apartment: {
      List: 'btn.apartment.list',
      Add: 'btn.apartment.add',
      Update: 'btn.apartment.update',
      Remove: 'btn.apartment.remove',
    },
    //   座位管理
    Room: {
      List: 'btn.room.list',
      Add: 'btn.room.add',
      Update: 'btn.room.update',
      Remove: 'btn.room.remove',
    },
    //   属性管理
    Attribute: {
      //   配套信息管理
      Facility: {
        List: 'btn.facility.list',
        Add: 'btn.facility.add',
        Update: 'btn.facility.update',
        Remove: 'btn.facility.remove',
      },
      //   标签信息管理
      Label: {
        List: 'btn.label.list',
        Add: 'btn.label.add',
        Update: 'btn.label.update',
        Remove: 'btn.label.remove',
      },
      //   座位 信息管理
      RoomInfo: {
        List: 'btn.seat.list',
        Add: 'btn.seat.add',
        Update: 'btn.seat.update',
        Remove: 'btn.seat.remove',
        AddNewItem: 'btn.seat.AddNewItem',
      },
      //   杂费信息管理
      Fee: {
        List: 'btn.fee.list',
        Add: 'btn.fee.add',
        Update: 'btn.fee.update',
        Remove: 'btn.fee.remove',
        AddNewItem: 'btn.fee.AddNewItem',
      },
      //   租期管理
      Term: {
        List: 'btn.agreement.list',
        Add: 'btn.agreement.add',
        Update: 'btn.agreement.update',
        Remove: 'btn.agreement.remove',
      },
      //   支付方式管理
      Payment: {
        List: 'btn.payment.list',
        Add: 'btn.payment.add',
        Update: 'btn.payment.update',
        Remove: 'btn.payment.remove',
      },
    },
  },
  //   预订管理
  Rent: {
    //   到店体验
    Appointment: {
      List: 'btn.appointment.list',
      TakeLook: 'btn.appointment.TakeLook',
    },
    //   订单管理
    Term: {
      List: 'btn.agreement.list',
      Add: 'btn.agreement.add',
      Update: 'btn.agreement.update',
      Remove: 'btn.agreement.remove',
      //   取消订单
      CancelTerm: 'btn.agreement.cancelTerm',
      //   确认签退
      ConfirmReturnRent: 'btn.agreement.confirmReturnRent',
      //   确认续期
      ConfirmRenew: 'btn.agreement.confirmRenew',
    },
  },
  //   用户管理
  User: {
    List: 'btn.user.list',
    Update: 'btn.user.update',
  },
}
// 门店的发布状态
export enum ApartmentReleaseStatus {
  NOT_RELEASED = 0,
  RELEASED = 1,
}
// 门店的发布状态
export const ApartmentReleaseStatusMap = [
  { label: '未发布', value: ApartmentReleaseStatus.NOT_RELEASED },
  { label: '已发布', value: ApartmentReleaseStatus.RELEASED },
]
// 座位的发布状态
export enum RoomReleaseStatus {
  NOT_RELEASED = 0,
  RELEASED = 1,
}
export const RoomReleaseStatusMap = [
  { label: '未发布', value: RoomReleaseStatus.NOT_RELEASED },
  { label: '已发布', value: RoomReleaseStatus.RELEASED },
]
// 座位的占用状态
export const RoomCheckInStatus = {
  NOT_CHECK_IN: false,
  CHECK_IN: true,
} as const
export const RoomCheckInStatusMap = [
  { label: '未占用', value: RoomCheckInStatus.NOT_CHECK_IN },
  { label: '已占用', value: RoomCheckInStatus.CHECK_IN },
]
//type: '门店' | '座位'
export enum BuildingType {
  APARTMENT = 1,
  ROOM = 2,
}
export const BuildingTypeTypeMap = [
  { label: '门店', value: BuildingType.APARTMENT },
  { label: '座位', value: BuildingType.ROOM },
]
// 预约状态,可用值:1,2,3,4
export enum AppointmentStatus {
  WAITING = 1,
  CANCELLED = 2,
  VIEWED = 3,
  EXPIRED = 4,
}
export const AppointmentStatusMap = [
  { label: '待体验', value: AppointmentStatus.WAITING },
  { label: '已取消', value: AppointmentStatus.CANCELLED },
  { label: '已体验', value: AppointmentStatus.VIEWED },
  { label: '已过期', value: AppointmentStatus.EXPIRED },
]
// 	订单状态,可用值:1,2,3,4,5,6,7
export enum AgreementStatus {
  WAITING = 1,
  SIGNED = 2,
  CANCELLED = 3,
  EXPIRED = 4,
  TO_BE_CONFIRMED = 5,
  CONFIRMED = 6,
  RENEW_TO_BE_CONFIRMED = 7,
  IN_USE = 8,
}
export const AgreementStatusMap = [
  { label: '待确认', value: AgreementStatus.WAITING },
  { label: '已预订', value: AgreementStatus.SIGNED },
  { label: '已取消', value: AgreementStatus.CANCELLED },
  { label: '已到期', value: AgreementStatus.EXPIRED },
  { label: '签退待确认', value: AgreementStatus.TO_BE_CONFIRMED },
  { label: '已完成', value: AgreementStatus.CONFIRMED },
  { label: '续期待确认', value: AgreementStatus.RENEW_TO_BE_CONFIRMED },
  { label: '使用中', value: AgreementStatus.IN_USE },
]
// 订单来源,可用值:1,2
export enum AgreementSource {
  NEW = 1,
  RENEW = 2,
}
export const AgreementSourceMap = [
  { label: '新建', value: AgreementSource.NEW },
  { label: '续期', value: AgreementSource.RENEW },
]
//	用户账号状态 "0": "禁用","1": "正常"
export enum UserStatus {
  DISABLED = 0,
  NORMAL = 1,
}
export const UserStatusMap = [
  { label: '禁用', value: UserStatus.DISABLED },
  { label: '正常', value: UserStatus.NORMAL },
]
// 系统用户类型 "0": "管理员","1": "普通用户"
export enum SystemUserType {
  ADMIN = 0,
  NORMAL = 1,
}
export const SystemUserTypeMap = [
  { label: '管理员', value: SystemUserType.ADMIN },
  { label: '普通用户', value: SystemUserType.NORMAL },
]
// 系统用户状态  "0": "禁用","1": "正常"
export enum SystemUserStatus {
  DISABLED = 0,
  NORMAL = 1,
}
export const SystemUserStatusMap = [
  { label: '禁用', value: SystemUserStatus.DISABLED },
  { label: '正常', value: SystemUserStatus.NORMAL },
]
