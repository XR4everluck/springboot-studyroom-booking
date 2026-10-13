<template>
  <van-skeleton :row="20" :loading="!orderDetailInfo?.seatId">
    <div class="page-container py-[15px]">
      <!--    预订自习室门店-->
      <div>
        <div class="base-info-title main-container py-[4px]">预订自习室门店</div>
        <div class="my-[5px] px-[20px]">
          <van-card @click="goStoreDetail()" class="rounded-xl shadow">
            <!--      title-->
            <template #title>
              <h2 class="text-[16px] font-bold mt-[25px] ml-[25px]">
                {{ `${orderDetailInfo.storeName}` }}
              </h2>
            </template>
            <!--    thumb-->
            <template #thumb>
              <van-image
                class="w-full h-full object-cover"
                :src="
                  orderDetailInfo.storeGraphVoList?.[0]?.url || '失败'
                "
              >
                <template v-slot:error>加载失败</template>
                <template v-slot:loading>
                  <van-loading type="spinner" size="20" />
                </template>
              </van-image>
            </template>
          </van-card>
        </div>
      </div>
      <!--    预订座位-->
      <div>
        <div class="base-info-title main-container py-[4px]">预订座位</div>
        <div class="my-[5px] px-[20px]">
          <van-card @click="goSeatDetail()" class="rounded-xl shadow">
            <!--      title-->
            <template #title>
              <h2 class="text-[16px] font-bold mt-[25px] ml-[25px]">
                {{ `${orderDetailInfo.seatCode}座位` }}
              </h2>
            </template>
            <!--    thumb-->
            <template #thumb>
              <van-image
                class="w-full h-full object-cover"
                :src="orderDetailInfo.seatGraphVoList?.[0]?.url || '失败'"
              >
                <template v-slot:error>加载失败</template>
                <template v-slot:loading>
                  <van-loading type="spinner" size="20" />
                </template>
              </van-image>
            </template>
          </van-card>
        </div>
      </div>
      <!--    预订人信息-->
      <div>
        <div class="base-info-title main-container py-[4px]">预订人信息</div>
        <div class="main-container my-[5px]">
          <div class="rounded-xl shadow overflow-hidden">
            <van-row>
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="姓名"
                  v-model="orderDetailInfo.name"
                ></van-field>
              </van-col>
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="手机号"
                  v-model="orderDetailInfo.phone"
                ></van-field>
              </van-col>
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="身份证号"
                  v-model="orderDetailInfo.identificationNumber"
                ></van-field>
              </van-col>
            </van-row>
          </div>
        </div>
      </div>
      <!--    订单详情-->
      <div>
        <div class="base-info-title main-container py-[4px]">订单详情</div>
        <div class="main-container my-[5px]">
          <div class="rounded-xl shadow overflow-hidden">
            <van-row>
              <!--          预订时长-->
              <van-col span="24">
                <van-field
                  v-model="bookingTermInfo.text"
                  is-link
                  readonly
                  name="picker"
                  label-width="70px"
                  label="预订时长"
                  placeholder="点击选择预订时长"
                  @click="isAllowEdit && (showPickerBookingTerm = true)"
                />
                <van-popup v-model:show="showPickerBookingTerm" position="bottom">
                  <van-picker
                    title="预订时长"
                    :columns="bookingTermColumns"
                    @confirm="onConfirmBookingTerm"
                    @cancel="showPickerBookingTerm = false"
                  />
                </van-popup>
              </van-col>
              <!--          预订时段-->
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="预订时段"
                  v-model="bookingDate"
                ></van-field>
              </van-col>
              <!--          总价-->
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="总价(元)"
                  v-model="priceText"
                ></van-field>
              </van-col>
              <!--          支付方式-->
              <van-col span="24">
                <van-field
                  v-model="paymentType.text"
                  is-link
                  readonly
                  name="picker"
                  label-width="70px"
                  label="支付方式"
                  placeholder="点击选择支付方式"
                  @click="isAllowEdit && (showPickerPaymentType = true)"
                />
                <van-popup
                  v-model:show="showPickerPaymentType"
                  position="bottom"
                >
                  <van-picker
                    title="支付方式"
                    :columns="paymentTypeColumns"
                    @confirm="onConfirmPaymentType"
                    @cancel="showPickerBookingTerm = false"
                  />
                </van-popup>
              </van-col>
            </van-row>
          </div>
        </div>
      </div>
      <!--    按钮-->
      <div class="main-container py-[20px]">
        <van-button
          v-if="isAllowEdit && isRenew"
          type="primary"
          block
          round
          class="m-t-20"
          @click="submitHandle"
        >
          {{ isAddRenew ? "确认续期" : "保存" }}
        </van-button>
        <!--      确认预订-->
        <van-button
          v-if="isConfirmOrder"
          type="primary"
          block
          round
          class="m-t-20"
          @click="submitHandle"
        >
          确认预订
        </van-button>
        <!--      签到-->
        <van-button
          v-if="
            !isAllowEdit &&
            orderDetailInfo.status === OrderStatus.SIGNED
          "
          type="success"
          block
          round
          class="m-t-20"
          @click="checkInHandle"
        >
          签到
        </van-button>
        <!--      签退-->
        <van-button
          v-if="
            !isAllowEdit &&
            orderDetailInfo.status === OrderStatus.IN_USE
          "
          type="warning"
          block
          round
          class="m-t-20"
          @click="checkOutHandle"
        >
          签退
        </van-button>
      </div>
    </div>
  </van-skeleton>
</template>
<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import dayjs from "dayjs";
import type {
  OrderDetailInterface,
  PaymentInfoInterface,
  TermInfoInterface
} from "@/api/search/types";
import { useRoute, useRouter } from "vue-router";
import { showToast } from "vant";
import {
  checkInOrder,
  checkOutOrder,
  getOrderDetailById,
  getPaymentListBySeatId,
  getTermListBySeatId,
  saveOrUpdateOrder
} from "@/api/search";
import { getTermEndDate } from "@/utils/bookingTerm";
import { OrderSource, OrderStatus } from "@/enums/constEnums";
const route = useRoute();
const router = useRouter();
// 是否允许修改
const isAllowEdit = ref(route.query.isEdit === "true");
// 是否是新增续期
const isRenew = ref(route.query.isRenew === "true");

const isAddRenew = ref(route.query.isAdd === "true");
// 是否确认预订
const isConfirmOrder = ref(route.query.isConfirm === "true");
// 自习室门店的详情信息
const orderDetailInfo = ref<Partial<OrderDetailInterface>>({
  id: "",
  // 	预订人姓名
  name: "",
  // phone	预订人手机号码
  phone: "",
  // identificationNumber	预订人身份证号码
  identificationNumber: "",
  // storeId	预订自习室门店id
  storeId: "",
  // seatId	预订座位id
  seatId: "",
  // startDate	订单开始日期
  startDate: "",
  // endDate	订单结束日期
  endDate: "",
  // bookingTermId	预订时长id
  bookingTermId: "",
  // totalAmount	订单总金额（元）
  totalAmount: "",
  // paymentTypeId	支付类型id
  paymentTypeId: "",
  // status	订单状态,可用值:1,2,3,4,5,6
  status: OrderStatus.WAITING,
  // sourceType	订单来源,可用值:1,2
  sourceType: OrderSource.NEW,
  // additionalInfo	备注信息
  additionalInfo: ""
});
//#region <预订时长相关>
// 预订时段
const bookingDate = computed(() => {
  return `${dayjs(orderDetailInfo.value.startDate).format(
    "YYYY-MM-DD"
  )} 至 ${dayjs(orderDetailInfo.value.endDate).format("YYYY-MM-DD")}`;
});
// 预订时长
const bookingTermInfo = ref({
  text: "",
  value: ""
});
// 监视bookingTermInfo的变化
watch(
  bookingTermInfo,
  newValue => {
    if (!newValue.value) return;
    orderDetailInfo.value.bookingTermId = newValue.value;
    const term = bookingTermList.value.find(
      item => String(item.id) === String(newValue.value)
    );
    if (!term) return;
    orderDetailInfo.value.bookingTermMonthCount = Number(term.monthCount);
    orderDetailInfo.value.bookingTermUnit = term.unit;
    //   重新计算起止日期
    orderDetailInfo.value.endDate = getTermEndDate(
      orderDetailInfo.value.startDate as string,
      term
    );
  },
  { immediate: true, deep: true }
);
// 预订时长的列表
const bookingTermList = ref<TermInfoInterface[]>([]);
// 预订时长的列表的列
const showPickerBookingTerm = ref(false);
const bookingTermColumns = computed(() => {
  return bookingTermList.value.map(item => ({
    text: item.monthCount + item.unit,
    value: item.id
  }));
});
// 获取预订时长列表
const getBookingTermListHandle = async () => {
  const { data } = await getTermListBySeatId(
    orderDetailInfo.value.seatId as string
  );
  bookingTermList.value = data;
};
// 确认选择预订时长
const onConfirmBookingTerm = (data: {
  selectedOptions: (typeof bookingTermInfo.value)[];
}) => {
  bookingTermInfo.value = data.selectedOptions[0];
  showPickerBookingTerm.value = false;
};
//#endregion
//#region <支付方式相关>
// 支付方式的列表
const paymentTypeList = ref<PaymentInfoInterface[]>([]);
// 支付方式
const paymentType = ref({
  text: "",
  value: ""
});
// 监视paymentType的变化
watch(
  paymentType,
  newValue => {
    if (newValue) {
      // 重赋值支付方式
      orderDetailInfo.value.paymentTypeId = newValue.value;
      orderDetailInfo.value.paymentTypeName = newValue.text;
    }
  },
  { immediate: true, deep: true }
);
// 支付方式的列表的列
const showPickerPaymentType = ref(false);
const paymentTypeColumns = computed(() => {
  return paymentTypeList.value.map(item => ({
    text: `${item.name}`,
    value: item.id
  }));
});
// 获取支付方式列表
const getPaymentTypeListHandle = async () => {
  const { data } = await getPaymentListBySeatId(
    orderDetailInfo.value.seatId as string
  );
  paymentTypeList.value = data;
};
// 确认选择支付方式
const onConfirmPaymentType = (data: {
  selectedOptions: (typeof paymentType.value)[];
}) => {
  paymentType.value = data.selectedOptions[0];
  showPickerPaymentType.value = false;
};
//#endregion
//#region <单价、押金相关>
// 总价
const priceText = computed(() => {
  return `${orderDetailInfo.value.totalAmount}元`;
});
//#endregion
// 获取自习室门店的详情信息
const getOrderDetailHandle = async () => {
  const { data } = await getOrderDetailById(route.query.id as string);
  orderDetailInfo.value = data;
  // 单独预订时长相关
  bookingTermInfo.value.text = data.bookingTermMonthCount + data.bookingTermUnit;
  bookingTermInfo.value.value = data.bookingTermId as string;
  // 单独支付方式相关
  paymentType.value.text = data.paymentTypeName as string;
  paymentType.value.value = data.paymentTypeId as string;
  //   如果是续期，允许修改，
  if (isAllowEdit.value) {
    // 重新计算起止日期
    // 结束日期变成开始日期
    orderDetailInfo.value.startDate = orderDetailInfo.value.endDate;
    // 开始日期加上预订时长得到新的结束日期
    orderDetailInfo.value.endDate = getTermEndDate(
      orderDetailInfo.value.startDate as string,
      {
        id: data.bookingTermId,
        monthCount: String(data.bookingTermMonthCount),
        unit: data.bookingTermUnit
      }
    );
    //   重置订单状态
    orderDetailInfo.value.status = OrderStatus.RENEW_TO_BE_CONFIRMED;
    //   重置订单来源
    orderDetailInfo.value.sourceType = OrderSource.RENEW;
  }
  //   如果是新增的续期,重置id为空
  isAddRenew.value && (orderDetailInfo.value.id = "");
  //   如果是确认预订，修改状态为已预订，等待确认
  isConfirmOrder.value &&
    (orderDetailInfo.value.status = OrderStatus.SIGNED);
};
// 更正预订时长和支付方式
const correctBookingTermAndPaymentType = () => {
  // 修正预订时长，如果不存在就改为数组第一项
  let targetTerm =
    bookingTermList.value.find(
      item => String(item.id) === String(orderDetailInfo.value.bookingTermId)
    ) || bookingTermList.value[0];
  if (targetTerm) {
    bookingTermInfo.value.text = targetTerm.monthCount + targetTerm.unit;
    bookingTermInfo.value.value = targetTerm.id as unknown as string;
  }
  //   修正支付方式，如果不存在就改为数组第一项
  let targetPaymentType =
    paymentTypeList.value.find(
      item =>
        String(item.id) === String(orderDetailInfo.value.paymentTypeId)
    ) || paymentTypeList.value[0];
  if (targetPaymentType) {
    paymentType.value.text = targetPaymentType.name as string;
    paymentType.value.value = targetPaymentType.id as unknown as string;
  }
};
// 跳转到自习室门店的详情页面
const goStoreDetail = () => {
  router.push({
    path: "/storeDetail",
    query: { id: orderDetailInfo.value.storeId }
  });
};
// 跳转到座位的详情页面
const goSeatDetail = () => {
  router.push({
    path: "/seatDetail",
    query: { id: orderDetailInfo.value.seatId }
  });
};
// 续期,修改
const submitHandle = async () => {
  try {
    if (
      !orderDetailInfo.value.bookingTermId ||
      !orderDetailInfo.value.paymentTypeId ||
      !orderDetailInfo.value.endDate
    ) {
      showToast({
        type: "fail",
        message: "请选择预订时长和付款方式"
      });
      return;
    }

    await saveOrUpdateOrder(orderDetailInfo.value);
    showToast({ type: "success", message: "操作成功" });
    router.back();
  } catch (error) {
    console.log(error);
  }
};
// 签到
const checkInHandle = async () => {
  try {
    await checkInOrder(orderDetailInfo.value.id as string);
    showToast({ type: "success", message: "签到成功" });
    await getOrderDetailHandle();
  } catch (error) {
    console.log(error);
  }
};
// 签退
const checkOutHandle = async () => {
  try {
    await checkOutOrder(orderDetailInfo.value.id as string);
    showToast({ type: "success", message: "签退成功" });
    await getOrderDetailHandle();
  } catch (error) {
    console.log(error);
  }
};
onMounted(async () => {
  route.query.id && (await getOrderDetailHandle());
  await getBookingTermListHandle();
  await getPaymentTypeListHandle();
  //  如果允许修改，修正预订时长和支付方式
  isAllowEdit.value && correctBookingTermAndPaymentType();
});
</script>

<style scoped lang="less">
.base-info-title {
  //background-color: var(--van-primary-background-color);
  font-weight: bold;
  //color: white;
}
::v-deep .van-card {
  background: var(--van-background-2) !important;
}
</style>
