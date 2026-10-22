<template>
  <van-skeleton :row="20" :loading="loading">
    <div class="sd" v-if="seatDetailInfo?.id">
      <div class="sd__back">
        <van-button icon="arrow-left" size="small" @click="router.back()"
          >返回</van-button
        >
        <span class="sd__back-title">座位详情</span>
      </div>

      <div class="sd__layout">
        <div class="sd__main">
          <!--  轮播图-->
          <van-swipe
            class="sd__swipe"
            :autoplay="3000"
            indicator-color="white"
            :show-indicators="true"
          >
            <van-swipe-item
              v-for="item in seatDetailInfo.graphVoList"
              :key="item.url"
            >
              <van-image fit="fill" :src="item.url" width="100%" height="400px">
                <template v-slot:error>加载失败</template>
                <template v-slot:loading>
                  <van-loading type="spinner" size="20" />
                </template>
              </van-image>
            </van-swipe-item>
          </van-swipe>

          <!--    基本信息-->
          <section class="sd__card">
            <h3 class="sd__card-title">基本信息</h3>
            <div class="sd__attr-grid">
              <div
                class="sd__attr"
                v-for="item in seatDetailInfo.attrValueVoList"
                :key="item.id"
              >
                <van-tag type="primary">{{ item.attrKeyName }}</van-tag>
                <span class="sd__attr-value">{{ item.name }}</span>
              </div>
            </div>
          </section>

          <!--    配套信息-->
          <section class="sd__card">
            <h3 class="sd__card-title">配套信息</h3>
            <div class="sd__facility">
              <div
                class="sd__facility-item"
                v-for="item in seatDetailInfo.facilityInfoList"
                :key="item.id"
              >
                <SvgIcon :name="item.icon" size="26" />
                <span>{{ item.name }}</span>
              </div>
            </div>
          </section>

          <!--    位置详情-->
          <section class="sd__card">
            <h3 class="sd__card-title">位置详情</h3>
            <div class="sd__address">
              {{ seatDetailInfo.storeItemVo.addressDetail }}
            </div>
            <!--        地图容器-->
            <div id="container" class="sd__map"></div>
          </section>

          <!--    费用明细-->
          <section class="sd__card">
            <h3 class="sd__card-title">费用明细</h3>
            <table class="sd__table">
              <thead>
                <tr>
                  <th>费用科目</th>
                  <th>收费标准</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in seatDetailInfo.feeValueVoList" :key="item.id">
                  <td>{{ item.feeKeyName }}</td>
                  <td>{{ `￥${item.name}${item.unit}` }}</td>
                </tr>
              </tbody>
            </table>
          </section>
        </div>

        <aside class="sd__side">
          <!--  座位的信息-->
          <section class="sd__card">
            <div class="font-bold sd__title">
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
            <div class="sd__labels">
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
            <div class="sd__price">
              <span class="text-red-500 text-[16px]">￥</span>
              <span class="text-red-500 text-[22px]"
                >{{ seatDetailInfo.price }}/时</span
              >
            </div>
            <!--    操作-->
            <div class="sd__actions">
              <van-button type="primary" block @click="showBooking = true"
                >立即预订</van-button
              >
              <van-button block @click="appointmentToViewHandle"
                >到店体验</van-button
              >
            </div>
          </section>

          <!--    付款说明-->
          <section class="sd__card">
            <h3 class="sd__card-title">可选付款方式</h3>
            <table class="sd__table">
              <thead>
                <tr>
                  <th>付款方式</th>
                  <th>说明</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in seatDetailInfo.paymentTypeList" :key="item.id">
                  <td>{{ item.name }}</td>
                  <td>{{ item.additionalInfo }}</td>
                </tr>
              </tbody>
            </table>
          </section>

          <section class="sd__card">
            <h3 class="sd__card-title">可选预订时长</h3>
            <table class="sd__table">
              <thead>
                <tr>
                  <th>预订时长</th>
                  <th>说明</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="item in seatDetailInfo.bookingTermList" :key="item.id">
                  <td>{{ item.monthCount + item.unit }}</td>
                  <td>到期可续</td>
                </tr>
              </tbody>
            </table>
          </section>

          <!--    所属自习室门店-->
          <section class="sd__card">
            <h3 class="sd__card-title">所属自习室门店</h3>
            <StoreCard :data="seatDetailInfo.storeItemVo"></StoreCard>
          </section>
        </aside>
      </div>

      <!--    预订下单-->
      <van-popup
        v-model:show="showBooking"
        position="center"
        round
        :style="{ width: '520px' }"
      >
        <van-form @submit="onSubmitBooking">
          <div class="px-[16px] py-[12px] font-bold">
            预订 {{ seatDetailInfo.seatCode }} 号座位
          </div>
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
            <van-popup v-model:show="showBookingDate" position="center" round>
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
            <van-popup v-model:show="showBookingTerm" position="center" round>
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
            <van-popup v-model:show="showPaymentType" position="center" round>
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
            <van-button round block type="primary" native-type="submit"
              >确认下单</van-button
            >
          </div>
        </van-form>
      </van-popup>
    </div>

    <!-- 座位不存在 / 已被下架时给出明确提示，避免骨架屏常驻 -->
    <van-empty
      v-else
      class="sd__empty"
      description="未找到该座位，可能已下架"
    >
      <van-button round type="primary" @click="router.push('/search')"
        >去找座位</van-button
      >
    </van-empty>
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
// 页面加载态：请求成功/失败/座位不存在都要收敛，避免骨架屏常驻
const loading = ref(true);
// 获取座位的详情信息
const getSeatDetailHandle = async () => {
  const { data } = await getSeatDetailById(route.query.id as string);
  seatDetailInfo.value = data || ({} as SeatDetailInterface);
  if (!data) return;
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
  try {
    if (!route.query.id) return;
    await getSeatDetailHandle();
    if (!seatDetailInfo.value?.id) return;
    await initMap();
    initMapPage({
      lng: +seatDetailInfo.value.storeItemVo.longitude,
      lat: +seatDetailInfo.value.storeItemVo.latitude
    });
  } catch (error) {
    console.error("座位详情初始化失败", error);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="less">
.sd {
  padding: 24px 24px 0;
}

.sd__back {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.sd__empty {
  padding: 40px 24px 0;
}

.sd__back-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--van-text-color);
}

.sd__layout {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.sd__main {
  flex: 1;
  min-width: 0;
}

.sd__side {
  width: 360px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.sd__swipe {
  border-radius: 12px;
  overflow: hidden;
}

.sd__card {
  box-sizing: border-box;
  margin-top: 16px;
  padding: 16px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
}

.sd__side .sd__card {
  margin-top: 0;
}

.sd__card-title {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--van-text-color);
}

.sd__title {
  font-size: 17px;
  line-height: 1.4;
}

.sd__labels {
  margin: 8px 0;
}

.sd__price {
  margin: 6px 0 14px;
}

.sd__actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.sd__attr-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.sd__attr {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--van-text-color-2);
}

.sd__facility {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.sd__facility-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  width: 72px;
  font-size: 12px;
  text-align: center;
  color: var(--van-text-color-2);
}

.sd__address {
  margin-bottom: 10px;
  font-size: 13px;
  color: var(--van-text-color-2);
}

.sd__map {
  width: 100%;
  height: 360px;
  border-radius: 8px;
  background-color: var(--van-active-color);
}

.sd__table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;

  th,
  td {
    padding: 8px 10px;
    text-align: left;
    border-bottom: 1px solid var(--van-border-color);
    color: var(--van-text-color-2);
  }

  th {
    color: var(--van-text-color);
    font-weight: 600;
    background-color: var(--van-active-color);
  }

  tr:last-child td {
    border-bottom: none;
  }
}

@media (max-width: 1100px) {
  .sd__layout {
    flex-direction: column;
  }

  .sd__side {
    width: 100%;
  }
}
</style>
