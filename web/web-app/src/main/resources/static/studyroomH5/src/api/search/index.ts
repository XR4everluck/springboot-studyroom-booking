import http from "@/utils/http";
import type {
  OrderDetailInterface,
  OrderItemInterface,
  OrderQueryInterface,
  StoreInterface,
  AppointmentInfoInterface,
  AppointmentItemInterface,
  AppointmentQueryInterface,
  HistoryItemInterface,
  MemberCardInterface,
  PaymentInfoInterface,
  RegionInterface,
  SeatDetailInterface,
  SeatInterface,
  SeatListQueryInterface,
  TermInfoInterface
} from "@/api/search/types";
import type { PageRes, ReqPage } from "@/api/types";
import type { OrderStatus } from "@/enums/constEnums";
/**
 * @description 分页查询座位列表
 * @param params
 */
export function getSeatList(params: Partial<SeatListQueryInterface>) {
  return http.get<PageRes<SeatInterface>>(`/app/seat/pageItem`, params);
}
/**
 * @description 查询省份列表
 */
export function getProvinceList() {
  return http.get<RegionInterface[]>(`/app/region/province/list`);
}
/**
 * @description 查询城市列表
 * @param provinceId
 */
export function getCityList(provinceId: number | string) {
  return http.get<RegionInterface[]>(
    `/app/region/city/listByProvinceId?id=${provinceId}`
  );
}
/**
 * @description 查询区域列表
 * @param cityId
 */
export function getDistrictList(cityId: number | string) {
  return http.get<RegionInterface[]>(
    `/app/region/district/listByCityId?id=${cityId}`
  );
}
/**
 * @description 获取支付方式列表
 */
export function getPaymentTypeList() {
  return http.get<PaymentInfoInterface[]>(`/app/payment/list`);
}
/**
 * @description 根据id获取座位的详细信息
 * @param id
 */
export function getSeatDetailById(id: number | string) {
  return http.get<SeatDetailInterface>(`/app/seat/getDetailById?id=${id}`);
}

/**
 * @description 根据id获取自习室门店信息
 */
export function getStoreDetailById(id: number | string) {
  return http.get<StoreInterface>(`/app/store/getDetailById?id=${id}`);
}

/**
 * @description 根据自习室门店id分页查询座位列表
 * @param params
 */
export function getSeatListByStoreId(
  params: ReqPage & { id: number | string }
) {
  return http.get<PageRes<SeatInterface>>(
    `/app/seat/pageItemByStoreId`,
    params
  );
}

/**
 * @description 保存或更新预约
 */
export function saveOrUpdateAppointment(params: AppointmentQueryInterface) {
  return http.post(`/app/visit/saveOrUpdate`, params);
}

/**
 * @description 根据id查询预约详情信息
 * @param id
 */
export function getAppointmentDetailById(id: number | string) {
  return http.get<AppointmentInfoInterface>(
    `/app/visit/getDetailById?id=${id}`
  );
}

/**
 * @description 查询我的预约列表
 */
export function getMyAppointmentList() {
  return http.get<AppointmentItemInterface[]>(`/app/visit/listItem`);
}

/**
 * @description 获取个人订单基本信息列表
 */
export function getMyOrderList() {
  return http.get<OrderItemInterface[]>(`/app/order/listItem`);
}

/**
 * @description 根据id获取订单详细信息
 */
export function getOrderDetailById(id: number | string) {
  return http.get<OrderDetailInterface>(
    `/app/order/getDetailById?id=${id}`
  );
}

/**
 * @description 根据id更新订单状态（用于提前签退等状态流转）
 * @param params
 */
export function updateOrderStatusById(params: {
  id: number | string;
  status: OrderStatus;
}) {
  return http.post(`/app/order/updateStatusById`, undefined, {
    params
  });
}

/**
 * @description 保存新订单
 */
export function saveOrUpdateOrder(
  params: Partial<OrderQueryInterface>
) {
  return http.post(`/app/order/saveOrUpdate`, params);
}

/**
 * @description 订单签到：已预订 → 使用中
 * @param id
 */
export function checkInOrder(id: number | string) {
  return http.post(`/app/order/checkIn?id=${id}`);
}

/**
 * @description 订单签退：使用中 → 已完成
 * @param id
 */
export function checkOutOrder(id: number | string) {
  return http.post(`/app/order/checkOut?id=${id}`);
}

/**
 * @description 获取我的可用会员卡列表
 */
export function getMyMemberCards() {
  return http.get<MemberCardInterface[]>(`/app/card/listMine`);
}

/**
 * @description 根据座位id获取可选获取租期列表
 * @param seatId
 */
export function getTermListBySeatId(seatId: number | string) {
  return http.get<TermInfoInterface[]>(`/app/term/listBySeatId?id=${seatId}`);
}
/**
 * @description 根据座位id获取可选支付方式列表
 * @param seatId
 */
export function getPaymentListBySeatId(seatId: number | string) {
  return http.get<PaymentInfoInterface[]>(
    `/app/payment/listBySeatId?id=${seatId}`
  );
}

/**
 * @description 获取浏览历史
 * @param params
 */
export function getHistoryList(params: ReqPage) {
  return http.get<PageRes<HistoryItemInterface>>(
    `/app/history/pageItem`,
    params
  );
}
