<template>
  <div class="home container">
    <el-card shadow="hover">
      <div class="page-header">
        <el-avatar :size="60" :src="userInfo.avatarUrl || logoPng" />
        <div class="page-header-tip">
          <p class="page-header-tip-title">
            {{ timeFix() }}{{ userInfo?.name }}，{{ welcome() }}
          </p>
          <p class="page-header-tip-desc">
            {{ getEnvByName('VITE_APP_TITLE') }}
          </p>
        </div>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="16" class="stat-row">
      <el-col v-for="item in statCards" :key="item.label" :xs="12" :sm="8" :md="6" :lg="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-card__icon" :style="{ background: item.color }">
            <el-icon :size="26" color="#fff"><component :is="item.icon" /></el-icon>
          </div>
          <div class="stat-card__info">
            <p class="stat-card__value">{{ item.value }}</p>
            <p class="stat-card__label">{{ item.label }}</p>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 近 7 日趋势 -->
    <el-card shadow="hover" class="trend-card">
      <template #header>
        <div class="trend-card__header">
          <span>近 7 日订单与营收趋势</span>
          <el-tag type="info" size="small">数据实时统计</el-tag>
        </div>
      </template>
      <div ref="chartRef" class="trend-card__chart" />
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { timeFix, welcome } from '@/utils/index'
import { getEnvByName } from '@/utils/getEnv'
import logoPng from '@/assets/images/logo.png'
import { getHomeStatistics } from '@/api/statistics'
import type { HomeStatisticsInterface } from '@/api/statistics'
import * as echarts from 'echarts'
import { Ticket, Money, Calendar, CreditCard, Bell } from '@element-plus/icons-vue'

const userStore = useUserStore()
const userInfo = userStore.userInfo

const statistics = ref<HomeStatisticsInterface>({
  todayOrders: 0,
  todayRevenue: 0,
  todayAppointments: 0,
  memberCardCount: 0,
  pendingOrders: 0,
  weekTrend: [],
})

const statCards = computed(() => [
  { label: '今日订单', value: statistics.value.todayOrders, icon: Ticket, color: '#409eff' },
  {
    label: '今日营收',
    value: `¥${Number(statistics.value.todayRevenue || 0).toFixed(2)}`,
    icon: Money,
    color: '#67c23a',
  },
  { label: '今日预约', value: statistics.value.todayAppointments, icon: Calendar, color: '#e6a23c' },
  { label: '有效会员卡', value: statistics.value.memberCardCount, icon: CreditCard, color: '#f56c6c' },
  { label: '待处理订单', value: statistics.value.pendingOrders, icon: Bell, color: '#909399' },
])

// 趋势图
const chartRef = ref<HTMLDivElement>()
let chart: echarts.ECharts | null = null

const renderChart = () => {
  if (!chartRef.value) return
  chart = chart || echarts.init(chartRef.value)
  const trend = statistics.value.weekTrend || []
  chart.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['订单量', '营收(¥)'] },
    grid: { left: 48, right: 48, top: 48, bottom: 32 },
    xAxis: { type: 'category', data: trend.map((t) => t.date) },
    yAxis: [
      { type: 'value', name: '订单量', minInterval: 1 },
      { type: 'value', name: '营收(¥)' },
    ],
    series: [
      {
        name: '订单量',
        type: 'bar',
        barMaxWidth: 36,
        itemStyle: { color: '#409eff', borderRadius: [4, 4, 0, 0] },
        data: trend.map((t) => t.orderCount),
      },
      {
        name: '营收(¥)',
        type: 'line',
        yAxisIndex: 1,
        smooth: true,
        itemStyle: { color: '#67c23a' },
        areaStyle: { color: 'rgba(103, 194, 58, 0.15)' },
        data: trend.map((t) => Number(t.revenue || 0)),
      },
    ],
  })
}

const resizeHandler = () => chart?.resize()

const getStatisticsHandle = async () => {
  const { data } = await getHomeStatistics()
  statistics.value = data
  await nextTick()
  renderChart()
}

onMounted(() => {
  getStatisticsHandle()
  window.addEventListener('resize', resizeHandler)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeHandler)
  chart?.dispose()
  chart = null
})
</script>

<style scoped lang="scss">
.home {
  height: 100%;

  .page-header {
    display: flex;
    align-items: center;

    .page-header-tip {
      flex: 1;
      margin-left: 20px;

      .page-header-tip-title {
        margin-bottom: 12px;
        font-size: 20px;
        font-weight: 700;
        color: #3c4a54;
      }

      .page-header-tip-desc {
        min-height: 20px;
        font-size: 14px;
        color: #808695;
      }
    }
  }

  .stat-row {
    margin-top: 16px;

    .stat-card {
      margin-bottom: 12px;

      :deep(.el-card__body) {
        display: flex;
        align-items: center;
        gap: 14px;
        padding: 18px;
      }

      .stat-card__icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 52px;
        height: 52px;
        border-radius: 12px;
        flex-shrink: 0;
      }

      .stat-card__value {
        font-size: 22px;
        font-weight: 700;
        color: #303133;
        line-height: 1.3;
      }

      .stat-card__label {
        font-size: 13px;
        color: #909399;
      }
    }
  }

  .trend-card {
    margin-top: 4px;

    .trend-card__header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-weight: 600;
    }

    .trend-card__chart {
      width: 100%;
      height: 360px;
    }
  }
}
</style>
