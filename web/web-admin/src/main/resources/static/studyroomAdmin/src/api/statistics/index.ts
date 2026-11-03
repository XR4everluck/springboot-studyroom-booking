import http from '@/utils/http'

export interface TrendPointInterface {
  date: string
  orderCount: number
  revenue: number
}

export interface HomeStatisticsInterface {
  todayOrders: number
  todayRevenue: number
  todayAppointments: number
  memberCardCount: number
  pendingOrders: number
  weekTrend: TrendPointInterface[]
}

/**
 * @description 查询首页看板统计数据
 */
export function getHomeStatistics() {
  return http.get<HomeStatisticsInterface>('/admin/statistics/home')
}
