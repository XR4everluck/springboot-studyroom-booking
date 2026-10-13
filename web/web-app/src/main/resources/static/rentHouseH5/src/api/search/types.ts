import type { ReqPage } from "@/api/types";
import type { SearchOrderType } from "@/enums/constEnums";
import type { AppointmentStatus } from "@/enums/constEnums";
import type { AgreementSource, AgreementStatus } from "@/enums/constEnums";

export interface RoomListQueryInterface extends ReqPage {
  // 省份id
  provinceId: number | string;
  // 城市id
  cityId: number | string;
  // 区域id
  districtId: number | string;
  //   最小单价
  minPrice: number | string;
  //   最大单价
  maxPrice: number | string;
  //   支付方式
  paymentTypeId: number | string;
  //   	价格排序方式,可用值:desc,asc
  orderType: SearchOrderType | "";
}
// 省市区
export interface RegionInterface {
  id: number | string;
  name: string;
  isCheck?: boolean;
  children?: RegionInterface[];
}
export interface LabelValueInterface<T = number | string> {
  label: string;
  value: T;
  isCheck?: boolean;
}
// 支付方式信息列表
export interface PaymentInfoInterface {
  id: number;
  name: string;
  payMonthCount: string;
  additionalInfo: string;
  isCheck?: boolean;
}
// 座位的信息
export interface RoomInterface {
  id: number | string;
  seatCode: number | string;
  rent: number | string;
  storeId: number | string;
  isRelease: string | number;
  leaseEndDate: string;
  checkInStatus: string;
  studyStore: ApartmentInterface;
  // 座位实时状态：0-空闲 1-已预订 2-使用中
  realtimeStatus?: number;
  // 座位实时状态名称
  realtimeStatusName?: string;
  // 	属性信息列表
  attrValueVoList?: any[];
  // 	自习室门店配套信息
  seatFacilityList?: FacilityInfoInterface[];
  // 自习室门店标签信息
  storeLabelList?: LabelInfoInterface[];
  // 	支付方式列表
  paymentTypeList?: PaymentInfoInterface[];
  // 可选租期列表
  leaseTermList?: TermInfoInterface[];
  // 图片信息
  graphVoList?: {
    url: string;
    name: number;
  }[];
}
export interface ApartmentInterface {
  id: number | string;
  name: string;
  introduction: string;
  districtId: number | string;
  districtName: string;
  cityId: number | string;
  cityName: string;
  provinceId: number | string;
  provinceName: string;
  addressDetail: string;
  latitude: number | string;
  longitude: number | string;
  phone: string;
  businessHours?: string;
  seatCount?: number;
  isRelease: string | number;
  minPrice?: number | string;
  // 	自习室门店配套id
  seatFacilityIds?: number[];
  // 	自习室门店配套信息
  seatFacilityList?: FacilityInfoInterface[];
  // 自习室门店标签信息
  storeLabelList?: LabelInfoInterface[];
  // 	自习室门店杂费值信息
  feeValueVoList?: FeeInfoInterface["feeValueList"];
  // 图片信息
  graphVoList?: {
    url: string;
    name: number;
  }[];
}
// 标签信息
export interface LabelInfoInterface {
  id: number | string;
  type: string;
  name: string;
}
// 配套信息
export interface FacilityInfoInterface {
  id: number | string;
  type: string;
  name: string;
  icon: string | null;
}
// 租期信息列表
export interface TermInfoInterface {
  id: number;
  unit: string;
  monthCount: string;
}
// 座位 详情信息
export interface RoomDetailInterface {
  id: number | string;
  seatCode: number | string;
  price: number | string;
  storeId: number | string;
  seatType?: string;
  hasPower?: boolean;
  hasLight?: boolean;
  isQuiet?: boolean;
  isRelease: string | number;
  leaseEndDate: string;
  checkInStatus: string;
  storeItemVo: ApartmentInterface;
  // 座位实时状态：0-空闲 1-已预订 2-使用中
  realtimeStatus?: number;
  realtimeStatusName?: string;
  // 	属性信息列表
  attrValueVoList?: AttrValueInfoInterface[];
  // 	自习室门店配套信息
  seatFacilityList?: FacilityInfoInterface[];
  // 自习室门店标签信息
  storeLabelList?: LabelInfoInterface[];
  // 	支付方式列表
  paymentTypeList?: PaymentInfoInterface[];
  // 可选租期列表
  leaseTermList?: TermInfoInterface[];
  // 	自习室门店杂费值信息
  feeValueVoList?: FeeValueInfoInterface[];
  // 图片信息
  graphVoList?: {
    url: string;
    name: number;
  }[];
}
// 杂费值信息
export interface FeeInfoInterface {
  id: number | string;
  name: string;
  feeValueList: FeeValueInfoInterface[];
}
export interface FeeValueInfoInterface {
  id: number | string;
  name: string;
  unit: string;
  feeKeyId: number | string;
  feeKeyName?: number | string;
}
// 座位基本信息
export interface AttrValueInfoInterface {
  id: number | string;
  name: string;
  attrKeyId: number | string;
  attrKeyName?: string;
}
// 预约请求接口
export interface AppointmentQueryInterface {
  id?: number | string;
  // 用户姓名
  name: string;
  // phone	用户手机号码
  phone: string;
  // storeId	自习室门店id
  storeId: number | string;
  // appointmentTime	预约时间
  appointmentTime: string;
  // 	备注信息
  additionalInfo: string;
  // 预约状态
  appointmentStatus?: AppointmentStatus;
}
export interface AppointmentInfoInterface extends AppointmentQueryInterface {
  storeItemVo: ApartmentInterface;
}
// 预约列表
export interface AppointmentItemInterface {
  id: number | string;
  storeName: string;
  graphVoList: {
    url: string;
    name: number;
  }[];
  appointmentTime: string;
  appointmentStatus: AppointmentStatus;
}
// 订单列表
export interface AgreementItemInterface {
  id: number | string;
  seatGraphVoList: {
    url: string;
    name: number;
  }[];
  storeName: string;
  seatCode: number | string;
  leaseStatus: number | string;
  leaseStartDate: string;
  leaseEndDate: string;
  rent: number | string;
  sourceType: AgreementSource;
}
// 订单请求接口
export interface AgreementQueryInterface {
  id?: number | string;
  // 	预订人姓名
  name: string;
  // phone	预订人手机号码
  phone: string;
  // identificationNumber	预订人身份证号码
  identificationNumber: string;
  // storeId	预订自习室门店id
  storeId: number | string;
  // seatId	预订座位id
  seatId: number | string;
  // startDate	预订开始日期
  startDate: string;
  // endDate	预订结束日期
  endDate: string;
  // leaseTermId	预订时长id
  leaseTermId: number | string;
  // totalAmount	总价（元）
  totalAmount: number | string;
  // paymentTypeId	支付类型id
  paymentTypeId: number | string;
  // memberCardId	抵扣所用会员卡id（可空）
  memberCardId?: number | string;
  // status	订单状态,可用值:1,2,3,4,5,6
  status: AgreementStatus;
  // sourceType	订单来源,可用值:1,2
  sourceType: AgreementSource;
  // additionalInfo	备注信息
  additionalInfo: string;
}
// 订单详情
export interface AgreementDetailInterface extends AgreementQueryInterface {
  storeName: string;
  seatCode: string;
  paymentTypeName: string;
  leaseTermMonthCount: number;
  leaseTermUnit: string;
  storeGraphVoList: {
    url: string;
    name: number;
  }[];
  seatGraphVoList: {
    url: string;
    name: number;
  }[];
}
// 浏览历史
export interface HistoryItemInterface {
  id: number | string;
  userId: number | string;
  roomId: number | string;
  browseTime: string;
  seatCode: string;
  rent: number | string;
  seatGraphVoList: {
    url: string;
    name: number;
  }[];
  storeName: string;
  provinceName: string;
  cityName: string;
  districtName: string;
}
// 会员卡（次卡/小时卡/月卡）
export interface MemberCardInterface {
  id?: number | string;
  cardNo?: string;
  userId?: number | string;
  storeId?: number | string | null;
  // 1次卡 2小时卡 3月卡
  cardType: number;
  totalQuantity?: number;
  remainingQuantity?: number;
  validUntil?: string;
  status?: number;
}
