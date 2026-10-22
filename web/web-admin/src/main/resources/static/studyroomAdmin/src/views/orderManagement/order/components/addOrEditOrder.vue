<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>{{ formData.id ? '修改' : '新增' }}订单</span>
      </div>
    </template>
    <el-form
      ref="storeFormRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      style="max-width: 1000px"
      status-icon
    >
      <el-form-item label="姓名" prop="name">
        <el-input v-model.trim="formData.name" placeholder="请输入姓名" />
      </el-form-item>
      <el-form-item label="手机号码" prop="phone">
        <el-input v-model.trim="formData.phone" placeholder="请输入手机号码" />
      </el-form-item>
      <el-form-item label="身份证号码" prop="identificationNumber">
        <el-input
          v-model.trim="formData.identificationNumber"
          placeholder="请输入身份证号码"
        />
      </el-form-item>
      <el-form-item label="座位" required>
        <div class="flex-center">
          <el-form-item prop="provinceId">
            <el-select
              v-model="areaInfo.provinceId"
              placeholder="请选择省份"
              clearable
              @change="provinceChangeCallback"
              @clear="provinceClearCallback"
            >
              <el-option
                v-for="item in areaInfo.provinceList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="cityId">
            <el-select
              v-model="areaInfo.cityId"
              placeholder="请选择城市"
              clearable
              @change="cityChangeCallback"
              @clear="cityClearCallback"
            >
              <el-option
                v-for="item in areaInfo.cityList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="districtId">
            <el-select
              v-model="areaInfo.districtId"
              placeholder="请选择区域"
              clearable
              @change="districtChangeCallback"
              @clear="districtClearCallback"
            >
              <el-option
                v-for="item in areaInfo.districtList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="storeId">
            <el-select
              v-model="areaInfo.storeId"
              placeholder="请选择门店"
              clearable
              filterable
              @change="storeChangeCallback"
              @clear="storeClearCallback"
            >
              <el-option
                v-for="item in areaInfo.storeList"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item prop="seatId">
            <el-select
              v-model="areaInfo.seatId"
              placeholder="请选择座位"
              clearable
              filterable
              @change="seatChangeCallback"
              @clear="seatClearCallback"
            >
              <el-option
                v-for="item in areaInfo.seatList"
                :key="item.id"
                :label="item.seatCode"
                :value="item.id"
              ></el-option>
            </el-select>
          </el-form-item>
        </div>
      </el-form-item>

      <el-form-item label="开始时间" prop="startDate">
        <el-date-picker
          v-model="formData.startDate"
          type="date"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          placeholder="开始时间"
          clearable
        />
      </el-form-item>
      <el-form-item label="结束时间" prop="endDate">
        <el-date-picker
          v-model="formData.endDate"
          type="date"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
          placeholder="结束时间"
          clearable
        />
      </el-form-item>
      <el-form-item label="支付方式" prop="paymentTypeIds">
        <el-select
          style="width: 100%"
          v-model="formData.paymentTypeId"
          placeholder="请选择支付方式"
          clearable
        >
          <el-option
            v-for="item in paymentInfoList"
            :key="item.id"
            :label="`${item.name} (${item.additionalInfo})`"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="预订时长" prop="bookingTermIds">
        <el-select
          style="width: 100%"
          v-model="formData.bookingTermId"
          placeholder="请选择预订时长"
          clearable
        >
          <el-option
            v-for="item in bookingTermInfoList"
            :key="item.id"
            :label="`${item.monthCount}${item.unit}`"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="总价（元）" prop="totalAmount">
        <el-input-number v-model="formData.totalAmount" :step="1" step-strictly />
      </el-form-item>
      <el-form-item label="备注信息" prop="additionalInfo">
        <el-input
          v-model.trim="formData.additionalInfo"
          type="textarea"
          placeholder="请输入备注信息"
        />
      </el-form-item>
    </el-form>
    <div class="flex-center m-t-20">
      <el-button style="width: 150px" type="info" @click="router.back()">
        取消
      </el-button>
      <el-button style="width: 150px" type="primary" @click="submitHandle">
        {{ formData.id ? '保存' : '新增' }}
      </el-button>
    </div>
  </el-card>
</template>
<script setup lang="ts">
import { onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, FormInstance } from 'element-plus'
import {
  StoreInterface,
  PaymentInfoInterface,
  RegionInterface,
  TermInfoInterface,
} from '@/api/storeManagement/types'
import {
  getStoreListByDistrictId,
  getCityList,
  getDistrictList,
  getPaymentList,
  getProvinceList,
  getTermList,
} from '@/api/storeManagement'
import { useRoute, useRouter } from 'vue-router'
import { OrderSource, OrderStatus } from '@/enums/constEnums'
import {
  OrderInterface,
  SeatInfoInterface,
} from '@/api/orderManagement/types'
import {
  getOrderInfoById,
  getSeatListByStoreId,
  saveOrUpdateOrderInfo,
} from '@/api/orderManagement'
const route = useRoute()
const router = useRouter()
//#region <表单相关>
// 表单数据
const storeFormRef = ref<FormInstance>()
const formData = ref<OrderInterface>({
  id: '',
  name: '',
  phone: '',
  identificationNumber: '',
  storeId: '',
  seatId: '',
  startDate: '',
  endDate: '',
  bookingTermId: '',
  totalAmount: 0,
  paymentTypeId: '',
  status: OrderStatus.WAITING,
  sourceType: OrderSource.NEW,
  additionalInfo: '',
})
// 表单验证规则
const rules = reactive({
  seatCode: [{ required: true, message: '请输入座位号', trigger: 'blur' }],
})
//#endregion
//#region <省市区查询相关>
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
  storeList: [] as StoreInterface[],
  storeId: '',
  // 座位的数据
  seatList: [] as SeatInfoInterface[],
  seatId: '',
})
// 监视地区信息变化,更新门店信息
watch(
  () => areaInfo,
  (newVal) => {
    formData.value.storeId = newVal.storeId
    formData.value.seatId = newVal.seatId
  },
  { immediate: true, deep: true },
)
// 获取省份
async function getProvinceListHandle() {
  try {
    const { data } = await getProvinceList()
    areaInfo.provinceList = data
  } catch (error) {
    console.log(error)
  }
}
// 获取城市
async function getCityListHandle(
  provinceId: number | string = areaInfo.provinceId,
) {
  try {
    const { data } = await getCityList(provinceId)
    areaInfo.cityList = data
  } catch (error) {
    console.log(error)
  }
}
// 获取区域
async function getDistrictListHandle(
  cityId: number | string = areaInfo.cityId,
) {
  try {
    const { data } = await getDistrictList(cityId)
    areaInfo.districtList = data
  } catch (error) {
    console.log(error)
  }
}
// 获取门店
async function getStoreListHandle(
  districtId: number | string = areaInfo.districtId,
) {
  try {
    const { data } = await getStoreListByDistrictId(districtId)
    areaInfo.storeList = data
  } catch (error) {
    console.log(error)
  }
}
// 获取座位
async function getSeatListHandle(
  storeId: number | string = areaInfo.storeId,
) {
  try {
    const { data } = await getSeatListByStoreId(storeId)
    areaInfo.seatList = data
  } catch (error) {
    console.log(error)
  }
}
// 重置市数据
function resetCity() {
  areaInfo.cityId = ''
  areaInfo.cityList = []
}
// 重置区数据
function resetDistrict() {
  areaInfo.districtId = ''
  areaInfo.districtList = []
}
// 重置门店数据
function resetStore() {
  areaInfo.storeId = ''
  areaInfo.storeList = []
}
// 重置座位的数据
function resetSeat() {
  areaInfo.seatId = ''
  areaInfo.seatList = []
}
// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = areaInfo.provinceId
  if (provinceId) {
    resetCity()
    resetDistrict()
    resetStore()
    resetSeat()
    await getCityListHandle(provinceId)
  }
}
// 省份清除回调
const provinceClearCallback = () => {
  areaInfo.provinceId = ''
  resetCity()
  resetDistrict()
  resetStore()
  resetSeat()
}
// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = areaInfo.cityId
  if (cityId) {
    resetDistrict()
    resetStore()
    resetSeat()
    await getDistrictListHandle(cityId)
  }
}
// 城市清除回调
const cityClearCallback = () => {
  console.log('清空城市')
  areaInfo.cityId = ''
  resetDistrict()
  resetStore()
  resetSeat()
}
// 区域改变回调
const districtChangeCallback = async () => {
  let districtId = areaInfo.districtId
  if (districtId) {
    resetStore()
    resetSeat()
    await getStoreListHandle(districtId)
  }
}
// 区域清除回调
const districtClearCallback = () => {
  console.log('清空区域')
  areaInfo.districtId = ''
  resetStore()
  resetSeat()
}
// 门店改变回调
const storeChangeCallback = async () => {
  console.log('门店改变')
  let storeId = areaInfo.storeId
  if (storeId) {
    resetSeat()
    await getSeatListHandle(storeId)
  }
}
// 门店清除回调
const storeClearCallback = () => {
  console.log('清空门店')
  areaInfo.storeId = ''
  resetSeat()
}
// 座位改变回调
const seatChangeCallback = async () => {
  console.log('座位改变')
}
// 座位清除回调
const seatClearCallback = () => {
  console.log('清空座位')
  areaInfo.seatId = ''
}
//#endregion
//#region <支付方式、可选租期>
// 支付方式信息
const paymentInfoList = ref<PaymentInfoInterface[]>([])
// 可选租期信息
const bookingTermInfoList = ref<TermInfoInterface[]>([])
// 获取支付方式信息列表
async function getPaymentInfoListHandle() {
  try {
    const { data } = await getPaymentList()
    paymentInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 获取可选租期信息列表
async function getBookingTermInfoListHandle() {
  try {
    const { data } = await getTermList()
    bookingTermInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}

//#endregion

// 根据id获取座位信息
async function getOrderInfoByIdHandle(id: number | string) {
  try {
    const { data } = await getOrderInfoById(id)
    formData.value = data
    // 重置省市区
    // 获取城市
    areaInfo.provinceId = data.studyStore.provinceId as string
    areaInfo.cityId = data.studyStore.cityId as string
    areaInfo.districtId = data.studyStore.districtId as string
    areaInfo.storeId = data.storeId as string
    areaInfo.seatId = data.seatId as string
    data.studyStore.provinceId &&
      (await getCityListHandle(data.studyStore.provinceId))
    // 获取区域
    data.studyStore.cityId &&
      (await getDistrictListHandle(data.studyStore.cityId))
    // 获取门店
    data.studyStore.districtId &&
      (await getStoreListHandle(data.studyStore.districtId))
    // 获取座位
    data.storeId && (await getSeatListHandle(data.storeId))
  } catch (error) {
    console.log(error)
  }
}
// 新增或更新门店信息
async function addOrUpdateOrderInfoHandle() {
  try {
    await saveOrUpdateOrderInfo(formData.value)
    ElMessage.success('操作成功')
    router.back()
  } catch (error) {
    console.log(error)
  }
}
// 提交
function submitHandle() {
  storeFormRef.value?.validate(async (valid) => {
    if (valid) {
      await addOrUpdateOrderInfoHandle()
    } else {
      ElMessage.error('表单填写有误，请检查')
      return false
    }
  })
}
onMounted(() => {
  // 获取省份信息列表
  getProvinceListHandle()
  // 获取支付方式信息列表
  getPaymentInfoListHandle()
  // 获取可选租期信息列表
  getBookingTermInfoListHandle()
  if (route.query?.id) {
    getOrderInfoByIdHandle(route.query.id as string)
  }
})
</script>

<style scoped lang="scss">
.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>
