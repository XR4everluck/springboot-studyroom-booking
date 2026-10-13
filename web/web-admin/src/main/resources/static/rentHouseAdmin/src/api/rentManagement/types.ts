import {
  AgreementSource,
  AgreementStatus,
  AppointmentStatus,
  RoomReleaseStatus,
} from '@/enums/constEnums'
import {
  ApartmentInterface,
  TermInfoInterface,
} from '@/api/apartmentManagement/types'
import { ReqPage } from '@/api/types'
//#region <到店体验>
// 预约信息
export interface AppointmentInfoInterface {
  id: number | string
  userId: number | string
  name: string
  phone: string
  storeId: number | string
  appointmentTime: string
  additionalInfo: string
  appointmentStatus: AppointmentStatus
  studyStore: Omit<ApartmentInterface, 'totalSeatCount' | 'freeSeatCount'>
}
export interface AppointmentInfoListQueryInterface extends ReqPage {
  // 省份id
  provinceId?: number | string
  // 城市id
  cityId?: number | string
  // 区域id
  districtId?: number | string
  // 门店id
  storeId?: number | string
  // 预约用户姓名
  name?: string
  // 预约用户手机号码
  phone?: string
}
//#endregion
//#region <订单管理>
export interface AgreementInfoListQueryInterface extends ReqPage {
  // 省份id
  provinceId?: number | string
  // 城市id
  cityId?: number | string
  // 区域id
  districtId?: number | string
  // 门店id
  storeId?: number | string
  // 座位 号
  seatCode?: number | string
  // 预约用户姓名
  name?: string
  // 预约用户手机号码
  phone?: string
}
// 订单信息
export interface AgreementInfoInterface extends AgreementInterface {
  studyStore: Omit<ApartmentInterface, 'totalSeatCount' | 'freeSeatCount'>
  seat: RoomInfoInterface
  paymentTypeInfo: PaymentTypeInterface
  leaseTerm: LeaseTermInterface
}
export interface AgreementInterface {
  id: number | string
  phone: string
  name: string
  identificationNumber: string
  storeId: number | string
  seatId: number | string
  startDate: string
  endDate: string
  leaseTermId: number | string
  totalAmount: number | string
  paymentTypeId: number | string
  status: AgreementStatus
  sourceType: AgreementSource
  additionalInfo: string
}
export interface RoomInfoInterface {
  id: number | string
  seatCode: number | string
  price: number | string
  storeId: number | string
  isRelease: RoomReleaseStatus
}
export interface PaymentTypeInterface {
  id: number | string
  name: string
  payMonthCount: number | string
  additionalInfo: string
}
export type LeaseTermInterface = TermInfoInterface
//#endregion
