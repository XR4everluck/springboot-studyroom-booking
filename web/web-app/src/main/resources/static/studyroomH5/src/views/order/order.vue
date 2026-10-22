<template>
  <van-skeleton :row="20" :loading="loading">
    <div class="od">
      <div class="od__back">
        <van-button icon="arrow-left" size="small" @click="router.back()"
          >返回</van-button
        >
        <span class="od__back-title">订单详情</span>
      </div>

      <div class="od__layout">
        <div class="od__main">
          <!--    预订自习室门店-->
          <section class="od__card">
            <h3 class="od__card-title">预订自习室门店</h3>
            <van-card @click="goStoreDetail()" class="rounded-xl shadow od__link-card">
              <template #title>
                <h2 class="text-[16px] font-bold mt-[25px] ml-[25px]">
                  {{ `${orderDetailInfo.storeName}` }}
                </h2>
              </template>
              <template #thumb>
                <van-image
                  class="w-full h-full object-cover"
                  :src="orderDetailInfo.storeGraphVoList?.[0]?.url || '失败'"
                >
                  <template v-slot:error>加载失败</template>
                  <template v-slot:loading>
                    <van-loading type="spinner" size="20" />
                  </template>
                </van-image>
              </template>
            </van-card>
          </section>

          <!--    预订座位-->
          <section class="od__card">
            <h3 class="od__card-title">预订座位</h3>
            <van-card @click="goSeatDetail()" class="rounded-xl shadow od__link-card">
              <template #title>
                <h2 class="text-[16px] font-bold mt-[25px] ml-[25px]">
                  {{ `${orderDetailInfo.seatCode}座位` }}
                </h2>
              </template>
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
          </section>

          <!--    预订人信息-->
          <section class="od__card">
            <h3 class="od__card-title">预订人信息</h3>
            <table class="od__table">
              <tbody>
                <tr>
                  <th>姓名</th>
                  <td>{{ orderDetailInfo.name }}</td>
                </tr>
                <tr>
                  <th>手机号</th>
                  <td>{{ orderDetailInfo.phone }}</td>
                </tr>
                <tr>
                  <th>身份证号</th>
                  <td>{{ orderDetailInfo.identificationNumber }}</td>
                </tr>
              </tbody>
            </table>
          </section>

          <!--    订单详情-->
          <section class="od__card">
            <h3 class="od__card-title">订单详情</h3>
            <table class="od__table">
              <tbody>
                <tr>
                  <th>预订时长</th>
                  <td>
                    <span
                      class="od__editable"
                      :class="{ 'is-disabled': !isAllowEdit }"
                      @click="isAllowEdit && (showPickerBookingTerm = true)"
                      >{{ bookingTermInfo.text || "点击选择预订时长" }}</span
                    >
                  </td>
                </tr>
                <tr>
                  <th>预订时段</th>
                  <td>{{ bookingDate }}</td>
                </tr>
                <tr>
                  <th>总价(元)</th>
                  <td>{{ priceText }}</td>
                </tr>
                <tr>
                  <th>支付方式</th>
                  <td>
                    <span
                      class="od__editable"
                      :class="{ 'is-disabled': !isAllowEdit }"
                      @click="isAllowEdit && (showPickerPaymentType = true)"
                      >{{ paymentType.text || "点击选择支付方式" }}</span
                    >
                  </td>
                </tr>
              </tbody>
            </table>

            <van-popup v-model:show="showPickerBookingTerm" position="center" round>
              <van-picker
                title="预订时长"
                :columns="bookingTermColumns"
                @confirm="onConfirmBookingTerm"
                @cancel="showPickerBookingTerm = false"
              />
            </van-popup>
            <van-popup v-model:show="showPickerPaymentType" position="center" round>
              <van-picker
                title="支付方式"
                :columns="paymentTypeColumns"
                @confirm="onConfirmPaymentType"
                @cancel="showPickerPaymentType = false"
              />
            </van-popup>
          </section>
        </div>

        <!--    操作-->
        <aside class="od__side">
          <section class="od__card">
            <h3 class="od__card-title">操作</h3>
            <div class="od__actions">
              <van-button
                v-if="isAllowEdit && isRenew"
                type="primary"
                block
                round
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
                @click="submitHandle"
              >
                确认预订
              </van-button>
              <!--      签到-->
              <van-button
                v-if="!isAllowEdit && orderDetailInfo.status === OrderStatus.SIGNED"
                type="success"
                block
                round
                @click="checkInHandle"
              >
                签到
              </van-button>
              <!--      签退-->
              <van-button
                v-if="!isAllowEdit && orderDetailInfo.status === OrderStatus.IN_USE"
                type="warning"
                block
                round
                @click="checkOutHandle"
              >
                签退
              </van-button>
              <div v-if="!hasAction" class="od__no-action">当前状态无可用操作</div>
            </div>
          </section>
        </aside>
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
// 详情加载态：无论成功、失败还是缺少 id，都必须收敛，避免骨架屏常驻
const loading = ref(true);
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
// 当前是否有可用操作按钮
const hasAction = computed(
  () =>
    (isAllowEdit.value && isRenew.value) ||
    isConfirmOrder.value ||
    (!isAllowEdit.value && orderDetailInfo.value.status === OrderStatus.SIGNED) ||
    (!isAllowEdit.value && orderDetailInfo.value.status === OrderStatus.IN_USE)
);
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
  const seatId = orderDetailInfo.value.seatId as string;
  // 没有座位 id 时直接跳过，避免发出一条 seatId 为空的无效请求
  if (!seatId) return;
  const { data } = await getTermListBySeatId(seatId);
  bookingTermList.value = data || [];
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
  const seatId = orderDetailInfo.value.seatId as string;
  // 同预订时长，缺少座位 id 时跳过
  if (!seatId) return;
  const { data } = await getPaymentListBySeatId(seatId);
  paymentTypeList.value = data || [];
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
  // 订单不存在/已删除时后端返回 null，这里直接返回，避免后续解构取值抛错
  if (!data) return;
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
        id: Number(data.bookingTermId),
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
  isConfirmOrder.value && (orderDetailInfo.value.status = OrderStatus.SIGNED);
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
      item => String(item.id) === String(orderDetailInfo.value.paymentTypeId)
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
  try {
    if (route.query.id) {
      await getOrderDetailHandle();
    }
    // 时长与支付方式都挂在座位维度上，只有拿到座位信息后才需要查询
    if (orderDetailInfo.value.seatId) {
      await getBookingTermListHandle();
      await getPaymentTypeListHandle();
      //  如果允许修改，修正预订时长和支付方式
      isAllowEdit.value && correctBookingTermAndPaymentType();
    }
  } catch (error) {
    console.error("订单详情初始化失败", error);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="less">
.od {
  padding: 24px 24px 0;
}

.od__back {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.od__back-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--van-text-color);
}

.od__layout {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.od__main {
  flex: 1;
  min-width: 0;
}

.od__side {
  width: 280px;
  flex-shrink: 0;
}

.od__card {
  box-sizing: border-box;
  margin-top: 16px;
  padding: 16px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
}

.od__main > .od__card:first-child,
.od__side .od__card {
  margin-top: 0;
}

.od__side .od__card {
  position: sticky;
  top: 76px;
}

.od__card-title {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--van-text-color);
}

.od__link-card {
  margin: 0;
  cursor: pointer;
}

.od__table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;

  th,
  td {
    padding: 9px 10px;
    text-align: left;
    border-bottom: 1px solid var(--van-border-color);
    color: var(--van-text-color-2);
  }

  th {
    width: 110px;
    color: var(--van-text-color);
    font-weight: 600;
    background-color: var(--van-active-color);
  }

  tr:last-child th,
  tr:last-child td {
    border-bottom: none;
  }
}

.od__editable {
  color: var(--van-primary-color);
  cursor: pointer;

  &:hover {
    text-decoration: underline;
  }

  &.is-disabled {
    color: var(--van-text-color-2);
    cursor: default;

    &:hover {
      text-decoration: none;
    }
  }
}

.od__actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.od__no-action {
  font-size: 13px;
  color: var(--van-text-color-3);
  text-align: center;
  padding: 8px 0;
}

:deep(.van-card) {
  background: var(--van-background-2) !important;
}

@media (max-width: 1100px) {
  .od__layout {
    flex-direction: column;
  }

  .od__side {
    width: 100%;
  }

  .od__side .od__card {
    position: static;
  }
}
</style>
