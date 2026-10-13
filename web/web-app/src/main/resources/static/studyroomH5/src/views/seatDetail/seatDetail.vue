<template>
  <van-skeleton :row="20" :loading="!seatDetailInfo?.id">
    <div class="page-container">
      <!--  轮播图-->
      <van-swipe class="my-swipe" :autoplay="3000" indicator-color="white">
        <van-swipe-item
          v-for="item in seatDetailInfo.graphVoList"
          :key="item.url"
        >
          <van-image fit="fill" :src="item.url" width="100%" height="360px">
            <template v-slot:error>加载失败</template>
            <template v-slot:loading>
              <van-loading type="spinner" size="20" />
            </template>
          </van-image>
        </van-swipe-item>
      </van-swipe>

      <!--  座位的信息-->
      <div class="card">
        <div class="">
          <!--      标题-->
          <div class="font-bold flex items-center">
            {{
              `${seatDetailInfo.storeItemVo.name} ${seatDetailInfo.seatCode}座位`
            }}
            <van-tag
              class="ml-[8px]"
              :type="
                seatDetailInfo.realtimeStatus === 2
                  ? 'danger'
                  : seatDetailInfo.realtimeStatus === 1
                    ? 'warning'
                    : 'success'
              "
              >{{ seatDetailInfo.realtimeStatusName || "空闲" }}</van-tag
            >
          </div>
          <!--      标签-->
          <div class="my-[7px]">
            <van-tag
              class="last:mr-0 mr-[5px]"
              plain
              v-for="item in seatDetailInfo.labelInfoList"
              :key="item.id"
              type="primary"
              >{{ item.name }}
            </van-tag>
          </div>
          <!--      价格-->
          <div>
            <span class="text-red-500 text-[16px]">￥</span>
            <span class="text-red-500 text-[18px]"
              >{{ seatDetailInfo.price }}/时</span
            >
          </div>
        </div>
      </div>

      <!--    基本信息-->
      <div class="card">
        <div class="base-info-title py-[4px]">基本信息</div>
        <div class="my-[5px]">
          <van-row gutter="10">
            <van-col
              span="12"
              class="my-[3px]"
              v-for="item in seatDetailInfo.attrValueVoList"
              :key="item.id"
            >
              <van-row>
                <van-col span="6">
                  <van-tag type="primary">{{ item.attrKeyName }}</van-tag>
                </van-col>
                <van-col span="18">
                  {{ item.name }}
                </van-col>
              </van-row>
            </van-col>
            <!--          占位修饰-->
            <van-col
              class="my-[3px]"
              v-if="seatDetailInfo.attrValueVoList?.length % 2 !== 0"
              span="12"
            >
            </van-col>
          </van-row>
        </div>
      </div>
      <!--    配套信息-->
      <div class="card">
        <div class="base-info-title py-[4px]">配套信息</div>
        <div class="my-[5px]">
          <van-row>
            <van-col
              span="4"
              class="my-[3px]"
              v-for="item in seatDetailInfo.facilityInfoList"
              :key="item.id"
            >
              <div class="flex flex-col justify-center items-center">
                <SvgIcon :name="item.icon" size="25" />
                <span class="text-center">
                  {{ item.name }}
                </span>
              </div>
            </van-col>
          </van-row>
        </div>
      </div>
      <!--    位置详情-->
      <div class="card">
        <div class="base-info-title py-[4px]">位置详情</div>
        <div class="my-[5px]">
          <div class="text-xs mb-[5px] w-[300px]">
            {{ seatDetailInfo.storeItemVo.addressDetail }}
          </div>
        </div>
        <!--        地图容器-->
        <div id="container" class="w-full h-[360px]"></div>
      </div>
      <!--    费用明细-->
      <div class="card">
        <div class="base-info-title py-[4px]">费用明细</div>
        <div class="my-[5px]">
          <van-row gutter="10">
            <van-col span="12">
              <van-tag type="primary" size="medium">费用科目</van-tag>
            </van-col>
            <van-col span="12">
              <van-tag type="primary" size="medium">收费标准</van-tag>
            </van-col>
            <template
              v-for="item in seatDetailInfo.feeValueVoList"
              :key="item.id"
            >
              <van-col span="12" class="my-[5px]">
                <span>{{ item.feeKeyName }}</span>
              </van-col>
              <van-col span="12" class="my-[5px]">
                <span>{{ `￥${item.name}${item.unit}` }}</span>
              </van-col>
            </template>
          </van-row>
        </div>
      </div>
      <!--    付款说明-->
      <div class="card">
        <div class="base-info-title py-[4px]">可选付款方式</div>
        <div class="my-[5px]">
          <van-row gutter="10">
            <van-col span="12">
              <van-tag type="primary" size="medium">可选付款方式</van-tag>
            </van-col>
            <van-col span="12">
              <van-tag type="primary" size="medium">说明</van-tag>
            </van-col>
            <template
              v-for="item in seatDetailInfo.paymentTypeList"
              :key="item.id"
            >
              <van-col span="12" class="my-[5px]">
                <span>{{ item.name }}</span>
              </van-col>
              <van-col span="12" class="my-[5px]">
                <span>{{ item.additionalInfo }}</span>
              </van-col>
            </template>
          </van-row>
        </div>
      </div>
      <div class="card">
        <div class="base-info-title py-[4px]">可选预订时长</div>
        <div class="my-[5px]">
          <van-row gutter="10">
            <van-col span="12">
              <van-tag type="primary" size="medium">可选预订时长</van-tag>
            </van-col>
            <van-col span="12">
              <van-tag type="primary" size="medium">说明</van-tag>
            </van-col>
            <template
              v-for="item in seatDetailInfo.bookingTermList"
              :key="item.id"
            >
              <van-col span="12" class="my-[5px]">
                <span>{{ item.monthCount + item.unit }}</span>
              </van-col>
              <van-col span="12" class="my-[5px]">
                <span> 到期可续 </span>
              </van-col>
            </template>
          </van-row>
        </div>
      </div>
      <!--    所属自习室门店-->
      <div class="card">
        <div class="base-info-title py-[4px]">所属自习室门店</div>
        <div class="my-[5px] pb-[50px] px-[10px]">
          <StoreCard :data="seatDetailInfo.storeItemVo"></StoreCard>
        </div>
      </div>
      <!--    底部操作-->
      <van-sticky :offset-bottom="0" position="bottom">
        <div class="flex px-[10px] py-[8px]">
          <van-button
            class="mr-[10px]"
            type="primary"
            round
            block
            @click="showBooking = true"
            >立即预订</van-button
          >
          <van-button type="default" round block @click="appointmentToViewHandle"
            >到店体验</van-button
          >
        </div>
      </van-sticky>

      <!--    预订下单-->
      <van-popup v-model:show="showBooking" position="bottom" round>
        <van-form @submit="onSubmitBooking">
          <div class="px-[16px] py-[12px] font-bold">预订 {{ seatDetailInfo.seatCode }} 号座位</div>
          <van-cell-group inset>
            <van-field
              v-model.trim="bookingForm.name"
              name="name"
              label="联系人"
              autocomplete="off"
              placeholder="请输入姓名"
              :rules="[{ required: true, message: '请填写姓名' }]"
            />
            <van-field label="联系手机" readonly>
              <template #input>
                <span class="text-[12px]">以下单账号登录手机号为准</span>
              </template>
            </van-field>
            <van-field
              v-model="bookingForm.startDate"
              is-link
              readonly
              name="startDate"
              label="预订日期"
              placeholder="点击选择日期"
              :rules="[{ required: true, message: '请选择预订日期' }]"
              @click="showBookingDate = true"
            />
            <van-popup v-model:show="showBookingDate" position="bottom">
              <van-date-picker
                :min-date="new Date()"
                :default-date="new Date()"
                :columns-type="['year', 'month', 'day']"
                @confirm="onConfirmBookingDate"
                @cancel="showBookingDate = false"
              />
            </van-popup>
            <van-field
              v-model="bookingTermText"
              is-link
              readonly
              name="bookingTermId"
              label="预订时长"
              placeholder="点击选择预订时长"
              :rules="[{ required: true, message: '请选择预订时长' }]"
              @click="showBookingTerm = true"
            />
            <van-popup v-model:show="showBookingTerm" position="bottom">
              <van-picker
                title="预订时长"
                :columns="bookingTermColumns"
                @confirm="onConfirmBookingTerm"
                @cancel="showBookingTerm = false"
              />
            </van-popup>
            <van-field
              v-model="paymentTypeText"
              is-link
              readonly
              name="paymentTypeId"
              label="付款方式"
              placeholder="点击选择付款方式"
              :rules="[{ required: true, message: '请选择付款方式' }]"
              @click="showPaymentType = true"
            />
            <van-popup v-model:show="showPaymentType" position="bottom">
              <van-picker
                title="付款方式"
                :columns="paymentTypeColumns"
                @confirm="onConfirmPaymentType"
                @cancel="showPaymentType = false"
              />
            </van-popup>
            <van-field label="预订时段" readonly :model-value="bookingRangeText" />
            <van-field label="合计金额" readonly :model-value="`${totalAmount} 元`" />
            <van-field
              v-model.trim="bookingForm.additionalInfo"
              name="additionalInfo"
              label="备注"
              rows="2"
              autosize
              type="textarea"
              maxlength="50"
              placeholder="请输入备注信息"
            />
          </van-cell-group>
          <div class="px-[16px] py-[16px]">
            <van-button round block type="primary" native-type="submit">确认下单</van-button>
          </div>
        </van-form>
      </van-popup>
    </div>
  </van-skeleton>
</template>
<script setup lang="ts">
import { getSeatDetailById, saveOrUpdateOrder } from "@/api/search";
import { computed, onMounted, ref } from "vue";
import dayjs from "dayjs";
import { showToast } from "vant";
import type {
  PaymentInfoInterface,
  SeatDetailInterface,
  TermInfoInterface
} from "@/api/search/types";
import { useMap } from "@/hooks/useMap";
import poiMarkerRed from "@/assets/poi-marker-red.png";
import StoreCard from "@/components/StoreCard/StoreCard.vue";
import { useRouter, useRoute } from "vue-router";
import { OrderSource, OrderStatus } from "@/enums/constEnums";
import {
  getBusinessHoursPerDay,
  getTermEndDate,
  getTermHours
} from "@/utils/bookingTerm";
const router = useRouter();
const route = useRoute();
// 座位的详情信息
const seatDetailInfo = ref<SeatDetailInterface>({} as SeatDetailInterface);
// 获取座位的详情信息
const getSeatDetailHandle = async () => {
  const { data } = await getSeatDetailById(route.query.id as string);
  seatDetailInfo.value = data;
  bookingForm.value.bookingTermId = String(data.bookingTermList?.[0]?.id ?? "");
  bookingForm.value.paymentTypeId = String(
    data.paymentTypeList?.[0]?.id ?? ""
  );
};
//#region <预订下单>
const showBooking = ref(false);
const showBookingDate = ref(false);
const showBookingTerm = ref(false);
const showPaymentType = ref(false);
const bookingForm = ref({
  name: "",
  startDate: dayjs().format("YYYY-MM-DD"),
  bookingTermId: "",
  paymentTypeId: "",
  additionalInfo: ""
});
// 单日营业小时数，用于把「天/月」时长折算成小时计价
const businessHoursPerDay = computed(() =>
  getBusinessHoursPerDay(seatDetailInfo.value.storeItemVo?.businessHours)
);
// 选中的预订时长
const selectedTerm = computed<TermInfoInterface | undefined>(() =>
  seatDetailInfo.value.bookingTermList?.find(
    item => String(item.id) === String(bookingForm.value.bookingTermId)
  )
);
// 选中的付款方式
const selectedPaymentType = computed<PaymentInfoInterface | undefined>(() =>
  seatDetailInfo.value.paymentTypeList?.find(
    item => String(item.id) === String(bookingForm.value.paymentTypeId)
  )
);
const bookingTermColumns = computed(() =>
  (seatDetailInfo.value.bookingTermList || []).map(item => ({
    text: `${item.monthCount}${item.unit}`,
    value: item.id
  }))
);
const paymentTypeColumns = computed(() =>
  (seatDetailInfo.value.paymentTypeList || []).map(item => ({
    text: item.name,
    value: item.id
  }))
);
const bookingTermText = computed(() => {
  const term = selectedTerm.value;
  return term ? `${term.monthCount}${term.unit}` : "";
});
const paymentTypeText = computed(() => selectedPaymentType.value?.name || "");
const bookingRangeText = computed(() => {
  if (!bookingForm.value.startDate || !selectedTerm.value) return "";
  return `${bookingForm.value.startDate} 至 ${getTermEndDate(
    bookingForm.value.startDate,
    selectedTerm.value
  )}`;
});
// 合计金额 = 小时单价 × 折算小时数
const totalAmount = computed(() => {
  const term = selectedTerm.value;
  if (!term) return 0;
  const hours = getTermHours(term, businessHoursPerDay.value);
  return Math.round(Number(seatDetailInfo.value.price) * hours * 100) / 100;
});
const onConfirmBookingDate = (date: { selectedValues: string[] }) => {
  bookingForm.value.startDate = date.selectedValues.join("-");
  showBookingDate.value = false;
};
const onConfirmBookingTerm = (data: { selectedOptions: { value: number }[] }) => {
  bookingForm.value.bookingTermId = String(data.selectedOptions[0].value);
  showBookingTerm.value = false;
};
const onConfirmPaymentType = (data: {
  selectedOptions: { value: number }[];
}) => {
  bookingForm.value.paymentTypeId = String(data.selectedOptions[0].value);
  showPaymentType.value = false;
};
const onSubmitBooking = async () => {
  const term = selectedTerm.value;
  if (!term) {
    showToast("请选择预订时长");
    return;
  }
  await saveOrUpdateOrder({
    name: bookingForm.value.name,
    phone: "",
    identificationNumber: "",
    storeId: seatDetailInfo.value.storeId,
    seatId: seatDetailInfo.value.id,
    startDate: bookingForm.value.startDate,
    endDate: getTermEndDate(bookingForm.value.startDate, term),
    bookingType: `${term.monthCount}${term.unit}`,
    bookingTermId: term.id,
    totalAmount: totalAmount.value,
    paymentTypeId: bookingForm.value.paymentTypeId,
    status: OrderStatus.SIGNED,
    sourceType: OrderSource.NEW,
    additionalInfo: bookingForm.value.additionalInfo
  });
  showToast("下单成功");
  showBooking.value = false;
  await getSeatDetailHandle();
  router.push("/myOrder");
};
//#endregion
//#region <高德地图相关>
// 地图实例
const { AMap, initMap } = useMap();
function initMapPage(opts?: { lng: number; lat: number }) {
  // 未配置有效高德 Key 或脚本加载失败时跳过地图渲染，不影响详情其余信息
  if (!AMap.value) return;
  const map = new AMap.value.Map("container", {
    zoom: 19, //初始地图级别
    center: [opts?.lng, opts?.lat], //初始地图中心点
    showIndoorMap: false //关闭室内地图
  });
  const icon = new AMap.value.Icon({
    size: new AMap.value.Size(25, 34), // 图标尺寸
    image: poiMarkerRed, // Icon的图像
    imageSize: new AMap.value.Size(25, 34)
  });
  const marker = new AMap.value.Marker({
    icon: icon,
    position: [opts?.lng, opts?.lat],
    anchor: "bottom-center"
  });
  map.add(marker);
  map.setFitView();
}
// 到店体验
const appointmentToViewHandle = () => {
  router.push({
    path: "/visit",
    query: { storeId: seatDetailInfo.value.storeId }
  });
};
//#endregion
onMounted(async () => {
  await getSeatDetailHandle();
  await initMap();
  initMapPage({
    lng: +seatDetailInfo.value.storeItemVo.longitude,
    lat: +seatDetailInfo.value.storeItemVo.latitude
  });
});
</script>

<style scoped lang="less">
.base-info-title {
  //background-color: var(--van-primary-background-color);
  font-weight: bold;
  //color: white;
}
</style>
