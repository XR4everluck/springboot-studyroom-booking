import http from '@/utils/http'
import {
  OrderInfoInterface,
  OrderInfoListQueryInterface,
  OrderInterface,
  AppointmentInfoInterface,
  AppointmentInfoListQueryInterface,
  SeatInfoInterface,
} from '@/api/orderManagement/types'
import { PageRes } from '@/api/types'
import { OrderStatus, AppointmentStatus } from '@/enums/constEnums'
//#region <到店体验>
/**
 * @description 分页查询预约信息
 * @param params
 */
export function getAppointmentInfoList(
  params: AppointmentInfoListQueryInterface,
) {
  return http.get<PageRes<AppointmentInfoInterface[]>>(
    `/admin/visit/page`,
    {
      current: params.pageNum,
      size: params.pageSize,
      provinceId: params.provinceId,
      cityId: params.cityId,
      districtId: params.districtId,
      storeId: params.storeId,
      name: params.name,
      phone: params.phone,
    },
  )
}
/**
 * @description 根据id更新预约状态
 * @param id
 * @param status
 */
export function updateAppointmentStatusById(
  id: number | string,
  status: AppointmentStatus,
) {
  return http.post(
    `/admin/visit/updateStatusById?id=${id}&status=${status}`,
  )
}
//#endregion
//#region <预订管理>
/**
 * @description 分页查询订单信息
 * @param params
 */
export function getOrderInfoList(params: OrderInfoListQueryInterface) {
  return http.get<PageRes<OrderInfoInterface[]>>(`/admin/order/page`, {
    current: params.pageNum,
    size: params.pageSize,
    provinceId: params.provinceId,
    cityId: params.cityId,
    districtId: params.districtId,
    storeId: params.storeId,
    seatCode: params.seatCode,
    name: params.name,
    phone: params.phone,
  })
}
/**
 * @description 根据门店id查询座位列表
 * @param storeId
 */
export function getSeatListByStoreId(storeId: number | string) {
  return http.get<SeatInfoInterface[]>(
    `/admin/seat/listBasicByStoreId?id=${storeId}`,
  )
}
/**
 * @description 根据id查询订单信息
 * @param id
 */
export function getOrderInfoById(id: number | string) {
  return http.get<OrderInfoInterface>(`/admin/order?id=${id}`)
}
/**
 * @description 保存或修改订单信息
 * @param data
 */
export function saveOrUpdateOrderInfo(data: OrderInterface) {
  return http.post(`/admin/order/saveOrUpdate`, data)
}
/**
 * @description 订单签到：已预订 → 使用中
 * @param id
 */
export function checkInOrderById(id: number | string) {
  return http.post(`/admin/order/checkIn?id=${id}`)
}
/**
 * @description 订单签退：使用中 → 已完成
 * @param id
 */
export function checkOutOrderById(id: number | string) {
  return http.post(`/admin/order/checkOut?id=${id}`)
}
/**
 * @description 根据id删除订单信息
 * @param id
 */
export function deleteOrderInfoById(id: number | string) {
  return http.delete(`/admin/order/removeById?id=${id}`)
}
/**
 * @description 根据id更新订单状态
 * @param id
 * @param status
 */
export function updateOrderStatusById({
  id,
  status,
}: {
  id: number | string
  status: OrderStatus
}) {
  return http.post(
    `/admin/order/updateStatusById?id=${id}&status=${status}`,
  )
}
//#endregion
