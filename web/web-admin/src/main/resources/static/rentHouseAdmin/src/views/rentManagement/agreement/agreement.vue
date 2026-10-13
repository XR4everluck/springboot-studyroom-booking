<template>
  <el-card>
    <ProTable
      ref="proTable"
      :dataCallback="dataCallback"
      :columns="columns"
      :requestApi="getAgreementInfoList"
      :initParam="initParam"
      :stripe="true"
    >
      <template #tableHeader>
        <el-button type="primary" icon="Plus" @click="addHandle">
          新增订单
        </el-button>
      </template>
      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button type="success" icon="Edit" @click="editHandle(scope.row)">
          修改
        </el-button>
        <el-button type="danger" icon="Delete" @click="deleteHandle(scope.row)">
          删除
        </el-button>
        <el-button
          type="warning"
          icon="Warning"
          v-if="scope.row.status === AgreementStatus.RENEW_TO_BE_CONFIRMED"
          @click="confirmLeaseHandle(scope.row)"
        >
          确认续期
        </el-button>
        <el-button
          type="primary"
          icon="Select"
          v-if="scope.row.status === AgreementStatus.WAITING"
          @click="confirmBookingHandle(scope.row)"
        >
          确认预订
        </el-button>
        <el-button
          type="warning"
          icon="Warning"
          v-if="scope.row.status === AgreementStatus.WAITING"
          @click="cancelLeaseHandle(scope.row)"
        >
          取消订单
        </el-button>
        <el-button
          type="danger"
          icon="Warning"
          v-if="scope.row.status === AgreementStatus.TO_BE_CONFIRMED"
          @click="confirmThrowLeaseHandle(scope.row)"
        >
          确认签退
        </el-button>
      </template>
    </ProTable>
  </el-card>
</template>

<script setup lang="tsx">
import { onMounted, reactive, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/src/types'
import ProTable from '@/components/ProTable/src/ProTable.vue'
import { useRouter } from 'vue-router'
import { useHandleData } from '@/hooks/useHandleData'
import {
  getApartmentListByDistrictId,
  getCityList,
  getDistrictList,
  getProvinceList,
} from '@/api/apartmentManagement'
import {
  ApartmentInterface,
  RegionInterface,
} from '@/api/apartmentManagement/types'
import {
  AgreementSourceMap,
  AgreementStatus,
  AgreementStatusMap,
  getLabelByValue,
} from '@/enums/constEnums'
import {
  deleteAgreementInfoById,
  getAgreementInfoList,
  updateAgreementStatusById,
} from '@/api/rentManagement'
import { AgreementInfoInterface } from '@/api/rentManagement/types'
const router = useRouter()

// *获取 ProTable 元素，调用其获取刷新数据方法
const proTable = ref<InstanceType<typeof ProTable>>()
// 地区数据
const areaInfo = reactive({
  // 省份数据
  provinceList: [] as RegionInterface[],
  provinceId: '',
  // 市区数据
  cityList: [] as RegionInterface[],
  cityId: '',
  // 区县数据
  districtList: [] as RegionInterface[],
  districtId: '',
  // 门店数据
  apartmentList: [] as ApartmentInterface[],
  storeId: '',
})
// *表格配置项
const columns: ColumnProps[] = [
  {
    prop: 'name',
    label: '姓名',
    search: { el: 'input', props: { placeholder: '请输入姓名' } },
  },
  {
    prop: 'phone',
    label: '手机',
    search: { el: 'input', props: { placeholder: '请输入手机号' } },
  },
  {
    prop: 'seatCode',
    label: '座位',
    search: { el: 'input', props: { placeholder: '请输入座位号' } },
    render: ({ row }: { row: AgreementInfoInterface }) => {
      return row.seat.seatCode
    },
  },
  {
    prop: 'provinceId',
    label: '省份',
    isShow: false,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'select',
      render: () => {
        return (
          <el-select
            v-model={areaInfo.provinceId}
            placeholder="请选择省份"
            clearable
            key="provinceId"
            onChange={provinceChangeCallback}
            onClear={provinceClearCallback}
          >
            {areaInfo.provinceList.map((item) => (
              <el-option
                key={item.id}
                label={item.name}
                value={item.id}
              ></el-option>
            ))}
          </el-select>
        )
      },
    },
  },
  {
    prop: 'cityId',
    label: '城市',
    isShow: false,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'select',
      key: 'cityId',
      render: () => {
        return (
          <el-select
            v-model={areaInfo.cityId}
            placeholder="请选择城市"
            clearable
            key="cityId"
            onChange={cityChangeCallback}
            onClear={cityClearCallback}
          >
            {areaInfo.cityList.map((item) => (
              <el-option
                key={item.id}
                label={item.name}
                value={item.id}
              ></el-option>
            ))}
          </el-select>
        )
      },
    },
  },
  {
    prop: 'districtId',
    label: '区域',
    isShow: false,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'select',
      key: 'districtId',
      render: () => {
        return (
          <el-select
            v-model={areaInfo.districtId}
            placeholder="请选择区域"
            clearable
            onChange={districtChangeCallback}
            onClear={districtClearCallback}
          >
            {areaInfo.districtList.map((item) => (
              <el-option
                key={item.id}
                label={item.name}
                value={item.id}
              ></el-option>
            ))}
          </el-select>
        )
      },
    },
  },
  {
    prop: 'storeId',
    label: '门店',
    isShow: false,
    fieldNames: { label: 'name', value: 'id' },
    search: {
      el: 'select',
      key: 'storeId',
      render: () => {
        return (
          <el-select
            v-model={areaInfo.storeId}
            placeholder="请选择门店"
            clearable
            onChange={apartmentChangeCallback}
            onClear={apartmentClearCallback}
          >
            {areaInfo.apartmentList.map((item) => (
              <el-option
                key={item.id}
                label={item.name}
                value={item.id}
              ></el-option>
            ))}
          </el-select>
        )
      },
    },
  },
  { prop: 'studyStore.name', label: '预订门店' },
  { prop: 'seat.seatCode', label: '座位编号' },
  { prop: 'studyStore.provinceName', label: '所处省份' },
  { prop: 'studyStore.cityName', label: '所处城市' },
  { prop: 'studyStore.districtName', label: '所处区域' },
  {
    prop: 'startDate',
    label: '预订时段',
    width: 200,
    render: ({ row }: { row: AgreementInfoInterface }) => {
      return row.startDate + ' ～ ' + row.endDate
    },
  },
  { prop: 'totalAmount', label: '总价（元）' },
  {
    prop: 'paymentType.name',
    label: '支付方式',
  },
  { prop: 'leaseTerm.monthCount', label: '预订时长（月）' },
  {
    prop: 'sourceType',
    label: '订单来源',
    render: ({ row }: { row: AgreementInfoInterface }) => {
      return getLabelByValue(AgreementSourceMap, row.sourceType)
    },
  },
  {
    prop: 'status',
    label: '订单状态',
    render: ({ row }: { row: AgreementInfoInterface }) => {
      switch (row.status) {
        case AgreementStatus.WAITING:
          return (
            <el-tag type="warning">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
        case AgreementStatus.RENEW_TO_BE_CONFIRMED:
          return (
            <el-tag type="warning">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
        case AgreementStatus.SIGNED:
          return (
            <el-tag type="success">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
        case AgreementStatus.TO_BE_CONFIRMED:
          return (
            <el-tag type="danger">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
        default:
          return (
            <el-tag type="info">
              {getLabelByValue(AgreementStatusMap, row.status)}
            </el-tag>
          )
      }
    },
  },
  { prop: 'operation', label: '操作', fixed: 'right', width: 320 },
]

// *查询参数
const initParam = reactive({})

//#region <省市区查询相关>
// 获取省份
async function getProvinceListHandle() {
  try {
    const { data } = await getProvinceList()
    areaInfo.provinceList = data
    proTable.value?.enumMap.set('provinceId', areaInfo.provinceList)
  } catch (error) {
    console.log(error)
  }
}
// 获取城市
async function getCityListHandle(provinceId: number) {
  try {
    const { data } = await getCityList(provinceId)
    areaInfo.cityList = data
    proTable.value?.enumMap.set('cityId', areaInfo.cityList)
  } catch (error) {
    console.log(error)
  }
}
// 获取区域
async function getDistrictListHandle(cityId: number) {
  try {
    const { data } = await getDistrictList(cityId)
    areaInfo.districtList = data
    proTable.value?.enumMap.set('districtId', areaInfo.districtList)
  } catch (error) {
    console.log(error)
  }
}
// 获取门店
async function getApartmentListHandle(districtId: number) {
  try {
    const { data } = await getApartmentListByDistrictId(districtId)
    areaInfo.apartmentList = data
    proTable.value?.enumMap.set('storeId', areaInfo.apartmentList)
  } catch (error) {
    console.log(error)
  }
}
// 重置市数据
function resetCity() {
  areaInfo.cityId = ''
  areaInfo.cityList = []
  proTable.value?.enumMap.set('cityId', [])
  proTable.value!.searchParam.cityId = ''
}
// 重置区数据
function resetDistrict() {
  areaInfo.districtId = ''
  areaInfo.districtList = []
  proTable.value?.enumMap.set('districtId', [])
  proTable.value!.searchParam.districtId = ''
}
// 重置门店数据
function resetApartment() {
  areaInfo.storeId = ''
  areaInfo.apartmentList = []
  proTable.value?.enumMap.set('storeId', [])
  proTable.value!.searchParam.storeId = ''
}
// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = proTable.value!.searchParam.provinceId
  if (provinceId) {
    resetCity()
    resetDistrict()
    resetApartment()
    await getCityListHandle(provinceId)
  }
}
// 省份清除回调
const provinceClearCallback = () => {
  areaInfo.provinceId = ''
  proTable.value!.searchParam.provinceId = ''
  resetCity()
  resetDistrict()
  resetApartment()
}
// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = proTable.value!.searchParam.cityId
  if (cityId) {
    resetDistrict()
    resetApartment()
    await getDistrictListHandle(cityId)
  }
}
// 城市清除回调
const cityClearCallback = () => {
  console.log('清空城市')
  areaInfo.cityId = ''
  proTable.value!.searchParam.cityId = ''
  resetDistrict()
  resetApartment()
}
// 区域改变回调
const districtChangeCallback = async () => {
  let districtId = proTable.value!.searchParam.districtId
  if (districtId) {
    resetApartment()
    await getApartmentListHandle(districtId)
  }
}
// 区域清除回调
const districtClearCallback = () => {
  console.log('清空区域')
  areaInfo.districtId = ''
  proTable.value!.searchParam.districtId = ''
  resetApartment()
}
// 门店改变回调
const apartmentChangeCallback = async () => {
  console.log('门店改变')
}
// 门店清除回调
const apartmentClearCallback = () => {
  console.log('清空门店')
  areaInfo.storeId = ''
  proTable.value!.searchParam.storeId = ''
}
//#endregion

// 处理返回的数据格式
const dataCallback = (data: any) => {
  return {
    list: data?.records,
    total: data?.total,
  }
}
// 查看
const editHandle = (row: AgreementInfoInterface) => {
  router.push({
    path: '/orderManagement/order/addOrEditOrder',
    query: {
      id: row.id,
    },
  })
}
// 新增
const addHandle = () => {
  router.push({
    path: '/orderManagement/order/addOrEditOrder',
  })
}
// 删除
const deleteHandle = async (row: AgreementInfoInterface) => {
  await useHandleData(
    deleteAgreementInfoById,
    row.id,
    `删除${row.name + row.studyStore.name + row.seat.seatCode}座位`,
  )
  // 修改row
  proTable.value?.getTableList()
}
// 取消订单
const cancelLeaseHandle = async (row: AgreementInfoInterface) => {
  await useHandleData(
    updateAgreementStatusById,
    { id: row.id, status: AgreementStatus.CANCELLED },
    `取消订单`,
  )
  // 修改row
  proTable.value?.getTableList()
}
// 确认预订
const confirmBookingHandle = async (row: AgreementInfoInterface) => {
  await useHandleData(
    updateAgreementStatusById,
    { id: row.id, status: AgreementStatus.SIGNED },
    `确认预订`,
  )
  // 修改row
  proTable.value?.getTableList()
}
// 确认续期
const confirmLeaseHandle = async (row: AgreementInfoInterface) => {
  await useHandleData(
    updateAgreementStatusById,
    { id: row.id, status: AgreementStatus.SIGNED },
    `确认续期`,
  )
  // 修改row
  proTable.value?.getTableList()
}
// 确认签退
const confirmThrowLeaseHandle = async (row: AgreementInfoInterface) => {
  await useHandleData(
    updateAgreementStatusById,
    { id: row.id, status: AgreementStatus.CONFIRMED },
    `确认签退`,
  )
  // 修改row
  proTable.value?.getTableList()
}

onMounted(() => {
  // 获取省份
  getProvinceListHandle()
})
</script>
