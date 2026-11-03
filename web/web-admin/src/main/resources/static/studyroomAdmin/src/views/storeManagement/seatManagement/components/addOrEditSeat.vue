<template>
  <el-card>
    <template #header>
      <div class="card-header">
        <span>{{ formData.id ? '修改' : '新增' }}座位</span>
      </div>
    </template>
    <el-form
      ref="storeFormRef"
      :model="formData"
      :rules="rules"
      label-width="120px"
      style="max-width: 660px"
      status-icon
    >
      <el-form-item label="座位编号" prop="seatCode">
        <el-input v-model="formData.seatCode" />
      </el-form-item>
      <el-form-item label="座位类型" prop="seatType">
        <el-select v-model="formData.seatType" placeholder="请选择座位类型">
          <el-option label="单人座" value="单人座"></el-option>
          <el-option label="双人座" value="双人座"></el-option>
          <el-option label="多人桌" value="多人桌"></el-option>
          <el-option label="独立隔间" value="独立隔间"></el-option>
          <el-option label="静音舱" value="静音舱"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="单价（元/小时）" prop="price">
        <el-input-number v-model="formData.price" :step="1" step-strictly />
      </el-form-item>
      <el-form-item label="座位配置">
        <el-checkbox v-model="formData.hasPower">插座</el-checkbox>
        <el-checkbox v-model="formData.hasLight">台灯</el-checkbox>
        <el-checkbox v-model="formData.isQuiet">静音区</el-checkbox>
      </el-form-item>
      <el-form-item label="所属门店" required>
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
        </div>
      </el-form-item>
      <el-form-item label="是否发布" prop="isRelease">
        <el-radio-group v-model="formData.isRelease" class="ml-4">
          <el-radio :label="SeatReleaseStatus.NOT_RELEASED">
            {{
              getLabelByValue(
                SeatReleaseStatusMap,
                SeatReleaseStatus.NOT_RELEASED,
              )
            }}
          </el-radio>
          <el-radio :label="SeatReleaseStatus.RELEASED">
            {{
              getLabelByValue(SeatReleaseStatusMap, SeatReleaseStatus.RELEASED)
            }}
          </el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="座位属性" prop="attrValueIds">
        <el-tree-select
          style="width: 100%"
          v-model="formData.attrValueIds"
          placeholder="请选择座位属性"
          :data="attrInfoList"
          multiple
          clearable
          node-key="value"
          :render-after-expand="false"
          @node-click="attrNodeClickHandle"
        ></el-tree-select>
      </el-form-item>
      <el-form-item label="座位配套" prop="facilityInfoIds">
        <el-select
          style="width: 100%"
          v-model="formData.facilityInfoIds"
          placeholder="请选择座位配套"
          multiple
          clearable
        >
          <el-option
            v-for="item in facilityInfoList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="座位标签" prop="labelInfoIds">
        <el-select
          style="width: 100%"
          v-model="formData.labelInfoIds"
          placeholder="请选择座位标签"
          multiple
          clearable
        >
          <el-option
            v-for="item in labelInfoList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="支付方式" prop="paymentTypeIds">
        <el-select
          style="width: 100%"
          v-model="formData.paymentTypeIds"
          placeholder="请选择支付方式"
          multiple
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
      <el-form-item label="可选租期" prop="bookingTermIds">
        <el-select
          style="width: 100%"
          v-model="formData.bookingTermIds"
          placeholder="请选择可选租期"
          multiple
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
      <el-form-item label="图片" prop="graphVoList">
        <upload-img
          v-model:file-list="formData.graphVoList"
          :on-success="uploadSuccessHandle"
          list-type="picture-card"
          :limit="5"
        ></upload-img>
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
import { nextTick, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, FormInstance, UploadFiles } from 'element-plus'
import {
  StoreInterface,
  FacilityInfoInterface,
  LabelInfoInterface,
  PaymentInfoInterface,
  RegionInterface,
  SaveSeatInterface,
  TermInfoInterface,
  TreeData,
} from '@/api/storeManagement/types'
import {
  getStoreListByDistrictId,
  getAttrInfoList,
  getCityList,
  getDistrictList,
  getFacilityInfoList,
  getLabelInfoList,
  getPaymentList,
  getProvinceList,
  getSeatById,
  getTermList,
  saveOrUpdateSeat,
} from '@/api/storeManagement'
import { UploadFile } from 'element-plus/es/components/upload/src/upload'
import UploadImg from '@/components/uploadImg/uploadImg.vue'
import { useRoute, useRouter } from 'vue-router'
// import { ElTree } from 'element-plus/es/components/tree'
import {
  BuildingType,
  getLabelByValue,
  SeatReleaseStatus,
  SeatReleaseStatusMap,
} from '@/enums/constEnums'
const route = useRoute()
const router = useRouter()
//#region <表单相关>
// 表单数据
const storeFormRef = ref<FormInstance>()
const formData = ref<SaveSeatInterface>({
  id: '',
  seatCode: '',
  price: 0,
  storeId: '',
  seatType: '单人座',
  hasPower: false,
  hasLight: false,
  isQuiet: false,
  isRelease: SeatReleaseStatus.NOT_RELEASED,
  // 	属性信息列表
  attrValueIds: [],
  // 	配套id
  facilityInfoIds: [],
  // 	标签信息列表
  labelInfoIds: [],
  // 	支付方式列表
  paymentTypeIds: [],
  // 可选租期列表
  bookingTermIds: [],
  graphVoList: [
    // {
    //   name: 'food.jpeg',
    //   url: 'https://fuss10.elemecdn.com/3/63/4e7f3a15429bfda99bce42a18cdd1jpeg.jpeg?imageMogr2/thumbnail/360x360/format/webp/quality/100',
    // },
  ] as UploadFile[],
})
// 表单验证规则
const rules = reactive({
  seatCode: [{ required: true, message: '请输入座位号', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  storeId: [{ required: true, message: '请选择门店', trigger: 'change' }],
  attrValueIds: [{ required: true, message: '请选择属性', trigger: 'change' }],
  facilityInfoIds: [
    { required: true, message: '请选择配套', trigger: 'change' },
  ],
  labelInfoIds: [{ required: true, message: '请选择标签', trigger: 'change' }],
  paymentTypeIds: [
    { required: true, message: '请选择支付方式', trigger: 'change' },
  ],
  bookingTermIds: [
    { required: true, message: '请选择可选租期', trigger: 'change' },
  ],
  graphVoList: [{ required: true, message: '请上传图片', trigger: 'change' }],
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
})
// 监视地区信息变化,更新门店信息
watch(
  () => areaInfo,
  (newVal) => {
    formData.value.storeId = newVal.storeId
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
// 省份改变回调
const provinceChangeCallback = async () => {
  let provinceId = areaInfo.provinceId
  if (provinceId) {
    resetCity()
    resetDistrict()
    resetStore()
    await getCityListHandle(provinceId)
  }
}
// 省份清除回调
const provinceClearCallback = () => {
  areaInfo.provinceId = ''
  resetCity()
  resetDistrict()
  resetStore()
}
// 城市改变回调
const cityChangeCallback = async () => {
  let cityId = areaInfo.cityId
  if (cityId) {
    resetDistrict()
    resetStore()
    await getDistrictListHandle(cityId)
  }
}
// 城市清除回调
const cityClearCallback = () => {
  console.log('清空城市')
  areaInfo.cityId = ''
  resetDistrict()
  resetStore()
}
// 区域改变回调
const districtChangeCallback = async () => {
  let districtId = areaInfo.districtId
  if (districtId) {
    resetStore()
    await getStoreListHandle(districtId)
  }
}
// 区域清除回调
const districtClearCallback = () => {
  console.log('清空区域')
  areaInfo.districtId = ''
  resetStore()
}
// 门店改变回调
const storeChangeCallback = async () => {
  console.log('门店改变')
}
// 门店清除回调
const storeClearCallback = () => {
  console.log('清空门店')
  areaInfo.storeId = ''
}
//#endregion
//#region <配套信息、标签信息、属性信息>
// 配套信息
const facilityInfoList = ref<FacilityInfoInterface[]>([])
// 标签信息
const labelInfoList = ref<LabelInfoInterface[]>([])
// 属性信息
const attrInfoList = ref<TreeData[]>([])
// 支付方式信息
const paymentInfoList = ref<PaymentInfoInterface[]>([])
// 可选租期信息
const bookingTermInfoList = ref<TermInfoInterface[]>([])
// 配套信息树实例
// const attrTreeSelectRef = ref<InstanceType<typeof ElTree>>()
// 获取配套信息
async function getFacilityInfoListHandle() {
  try {
    const { data } = await getFacilityInfoList(BuildingType.SEAT)
    facilityInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 获取标签信息
async function getLabelInfoListHandle() {
  try {
    const { data } = await getLabelInfoList(BuildingType.SEAT)
    labelInfoList.value = data
  } catch (error) {
    console.log(error)
  }
}
// 获取属性信息
async function getAttrInfoListHandle() {
  try {
    const { data } = await getAttrInfoList()
    attrInfoList.value = data.map((item) => {
      return {
        value: item.id + item.name,
        label: item.name,
        children: item.attrValueList.map((child) => {
          return {
            value: child.id,
            label: `${child.name}(${item.name})`,
            parentId: item.id + item.name,
          }
        }),
      }
    })
  } catch (error) {
    console.log(error)
  }
}
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

// 属性信息节点点击回调
function attrNodeClickHandle(data: TreeData) {
  //   保证每个子节点只有一个被选中
  if (data.parentId) {
    //   收集当前父节点下所有的子节点列表
    const childrenList = attrInfoList.value
      .find((item) => item.value === data.parentId)
      ?.children?.map((item) => item.value)
    nextTick(() => {
      //   遍历feeValueIds节点，删除所有childrenList中的值
      formData.value.attrValueIds = formData.value.attrValueIds?.filter(
        (item) => !childrenList?.includes(item),
      )
      //   将当前节点的值添加到feeValueIds中
      formData.value.attrValueIds?.push(data.value as number)
    })
  }
}
//#endregion
// 图片上传成功
function uploadSuccessHandle(
  response: any,
  uploadFile: UploadFile,
  uploadFiles: UploadFiles,
) {
  formData.value.graphVoList = uploadFiles?.map((item) => {
    return {
      ...item,
      url: (item?.response as any)?.data || item.url,
    }
  })
}
// 根据id获取座位信息
async function getSeatInfoByIdHandle(id: number | string) {
  try {
    const { data } = await getSeatById(id)
    formData.value = data as unknown as SaveSeatInterface
    // 	属性ids
    formData.value.attrValueIds =
      data.attrValueVoList?.map((item) => item.id) || []
    delete data.attrValueVoList
    // 	配套ids
    formData.value.facilityInfoIds =
      (data.facilityInfoList?.map((item) => item.id) as number[]) || []
    delete data.facilityInfoList
    // 	标签ids
    formData.value.labelInfoIds =
      (data.labelInfoList?.map((item) => item.id) as number[]) || []
    delete data.labelInfoList
    // 	支付方式ids
    formData.value.paymentTypeIds =
      (data.paymentTypeList?.map((item) => item.id) as number[]) || []
    delete data.paymentTypeList
    // 可选租期ids
    formData.value.bookingTermIds =
      (data.bookingTermList?.map((item) => item.id) as number[]) || []
    delete data.bookingTermList
    // 重置省市区
    // 获取城市
    areaInfo.provinceId = data.studyStore.provinceId as string
    areaInfo.cityId = data.studyStore.cityId as string
    areaInfo.districtId = data.studyStore.districtId as string
    areaInfo.storeId = data.storeId as string
    data.studyStore.provinceId &&
      getCityListHandle(data.studyStore.provinceId)
    // 获取区域
    data.studyStore.cityId &&
      getDistrictListHandle(data.studyStore.cityId)
    // 获取门店
    data.studyStore.districtId &&
      getStoreListHandle(data.studyStore.districtId)
  } catch (error) {
    console.log(error)
  }
}
// 新增或更新门店信息
async function addOrUpdateSeatInfoHandle() {
  try {
    await saveOrUpdateSeat(formData.value)
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
      await addOrUpdateSeatInfoHandle()
    } else {
      ElMessage.error('表单填写有误，请检查')
      return false
    }
  })
}
onMounted(() => {
  // 获取省份
  getProvinceListHandle()
  // 获取配套信息
  getFacilityInfoListHandle()
  // 获取标签信息
  getLabelInfoListHandle()
  // 获取属性信息
  getAttrInfoListHandle()
  // 获取支付方式信息列表
  getPaymentInfoListHandle()
  // 获取可选租期信息列表
  getBookingTermInfoListHandle()
  if (route.query?.id) {
    getSeatInfoByIdHandle(route.query.id as string)
  }
})
</script>

<style scoped lang="scss">
.card-header {
  font-size: 18px;
  font-weight: bold;
}
</style>
