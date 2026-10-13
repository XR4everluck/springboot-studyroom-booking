<template>
  <van-skeleton :row="20" :loading="!agreementDetailInfo?.seatId">
    <div class="page-container min-h-[100vh] py-[15px]">
      <!--    预订自习室门店-->
      <div>
        <div class="base-info-title main-container py-[4px]">预订自习室门店</div>
        <div class="my-[5px] px-[20px]">
          <van-card @click="goApartmentDetail()" class="rounded-xl shadow">
            <!--      title-->
            <template #title>
              <h2 class="text-[16px] font-bold mt-[25px] ml-[25px]">
                {{ `${agreementDetailInfo.storeName}` }}
              </h2>
            </template>
            <!--    thumb-->
            <template #thumb>
              <van-image
                class="w-full h-full object-cover"
                :src="
                  agreementDetailInfo.storeGraphVoList?.[0]?.url || '失败'
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
          <van-card @click="goRoomDetail()" class="rounded-xl shadow">
            <!--      title-->
            <template #title>
              <h2 class="text-[16px] font-bold mt-[25px] ml-[25px]">
                {{ `${agreementDetailInfo.seatCode}座位` }}
              </h2>
            </template>
            <!--    thumb-->
            <template #thumb>
              <van-image
                class="w-full h-full object-cover"
                :src="agreementDetailInfo.seatGraphVoList?.[0]?.url || '失败'"
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
        <div class="base-info-title main-container py-[4px]">预订座位</div>
        <div class="main-container my-[5px]">
          <div class="rounded-xl shadow overflow-hidden">
            <van-row>
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="姓名"
                  v-model="agreementDetailInfo.name"
                ></van-field>
              </van-col>
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="手机号"
                  v-model="agreementDetailInfo.phone"
                ></van-field>
              </van-col>
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="身份证号"
                  v-model="agreementDetailInfo.identificationNumber"
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
                  v-model="leaseTermInfo.text"
                  is-link
                  readonly
                  name="picker"
                  label-width="70px"
                  label="预订时长"
                  placeholder="点击选择预订时长"
                  @click="isAllowEdit && (showPickerLeaseTerm = true)"
                />
                <van-popup v-model:show="showPickerLeaseTerm" position="bottom">
                  <van-picker
                    title="预订时长"
                    :columns="leaseTermColumns"
                    @confirm="onConfirmLeaseTerm"
                    @cancel="showPickerLeaseTerm = false"
                  />
                </van-popup>
              </van-col>
              <!--          预订时段-->
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="预订时段"
                  v-model="leaseDate"
                ></van-field>
              </van-col>
              <!--          身份证号-->
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="身份证号"
                  v-model="agreementDetailInfo.identificationNumber"
                ></van-field>
              </van-col>
              <!--          总价-->
              <van-col span="24">
                <van-field
                  readonly
                  label-width="70px"
                  label="总价(元)"
                  v-model="rentText"
                ></van-field>
              </van-col>
              <!--          支付方式-->
              <van-col span="24">
                <van-field
                  v-model="paymentTypeInfo.text"
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
                    @cancel="showPickerLeaseTerm = false"
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
          v-if="isConfirmAgreement"
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
            agreementDetailInfo.status === AgreementStatus.SIGNED
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
            agreementDetailInfo.status === AgreementStatus.IN_USE
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
  AgreementDetailInterface,
  PaymentInfoInterface,
  TermInfoInterface
} from "@/api/search/types";
import { useRoute, useRouter } from "vue-router";
import { showToast } from "vant";
import {
  checkInOrder,
  checkOutOrder,
  getAgreementDetailById,
  getPaymentListByRoomId,
  getTermListByRoomId,
  saveOrUpdateAgreement
} from "@/api/search";
import { AgreementSource, AgreementStatus } from "@/enums/constEnums";
const route = useRoute();
const router = useRouter();
// 是否允许修改
const isAllowEdit = ref(route.query.isEdit === "true");
// 是否是新增续期
const isRenew = ref(route.query.isRenew === "true");

const isAddRenew = ref(route.query.isAdd === "true");
// 是否确认预订
const isConfirmAgreement = ref(route.query.isConfirm === "true");
// 自习室门店的详情信息
const agreementDetailInfo = ref<Partial<AgreementDetailInterface>>({
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
  // leaseTermId	预订时长id
  leaseTermId: "",
  // totalAmount	单价（元/月）
  totalAmount: "",
  // paymentTypeId	支付类型id
  paymentTypeId: "",
  // status	订单状态,可用值:1,2,3,4,5,6
  status: AgreementStatus.WAITING,
  // sourceType	订单来源,可用值:1,2
  sourceType: AgreementSource.NEW,
  // additionalInfo	备注信息
  additionalInfo: ""
});
//#region <预订时长相关>
// 预订时段
const leaseDate = computed(() => {
  return `${dayjs(agreementDetailInfo.value.startDate).format(
    "YYYY-MM-DD"
  )} 至 ${dayjs(agreementDetailInfo.value.endDate).format("YYYY-MM-DD")}`;
});
// 预订时长
const leaseTermInfo = ref({
  text: "",
  value: ""
});
// 监视leaseTermInfo的变化
watch(
  leaseTermInfo,
  newValue => {
    if (newValue) {
      console.log("newValue", newValue);
      // 重赋值预订时长
      agreementDetailInfo.value.leaseTermId = newValue.value;
      agreementDetailInfo.value.leaseTermMonthCount = +newValue.text.slice(
        0,
        -1
      );
      agreementDetailInfo.value.leaseTermUnit = newValue.text.slice(-1);
      //   重新计算起止日期
      agreementDetailInfo.value.endDate = dayjs(
        agreementDetailInfo.value.startDate
      )
        .add(agreementDetailInfo.value.leaseTermMonthCount as number, "month")
        .format("YYYY-MM-DD");
    }
  },
  { immediate: true, deep: true }
);
// 预订时长的列表
const leaseTermList = ref<TermInfoInterface[]>([]);
// 预订时长的列表的列
const showPickerLeaseTerm = ref(false);
const leaseTermColumns = computed(() => {
  return leaseTermList.value.map(item => ({
    text: item.monthCount + item.unit,
    value: item.id
  }));
});
// 获取预订时长列表
const getLeaseTermListHandle = async () => {
  const { data } = await getTermListByRoomId(
    agreementDetailInfo.value.seatId as string
  );
  leaseTermList.value = data;
};
// 确认选择预订时长
const onConfirmLeaseTerm = (data: {
  selectedOptions: (typeof leaseTermInfo.value)[];
}) => {
  console.log("onConfirmLeaseTerm", data);
  leaseTermInfo.value = data.selectedOptions[0];
  showPickerLeaseTerm.value = false;
};
//#endregion
//#region <支付方式相关>
// 支付方式的列表
const paymentTypeList = ref<PaymentInfoInterface[]>([]);
// 支付方式
const paymentTypeInfo = ref({
  text: "",
  value: ""
});
// 监视paymentTypeInfo的变化
watch(
  paymentTypeInfo,
  newValue => {
    if (newValue) {
      console.log("newValue", newValue);
      // 重赋值支付方式
      agreementDetailInfo.value.paymentTypeId = newValue.value;
      agreementDetailInfo.value.paymentTypeName = newValue.text;
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
  const { data } = await getPaymentListByRoomId(
    agreementDetailInfo.value.seatId as string
  );
  paymentTypeList.value = data;
};
// 确认选择支付方式
const onConfirmPaymentType = (data: {
  selectedOptions: (typeof paymentTypeInfo.value)[];
}) => {
  console.log("onConfirmPaymentType", data);
  paymentTypeInfo.value = data.selectedOptions[0];
  showPickerPaymentType.value = false;
};
//#endregion
//#region <单价、押金相关>
// 总价
const rentText = computed(() => {
  return `${agreementDetailInfo.value.totalAmount}元`;
});
//#endregion
// 获取自习室门店的详情信息
const getAgreementDetailHandle = async () => {
  const { data } = await getAgreementDetailById(route.query.id as string);
  agreementDetailInfo.value = data;
  // 单独预订时长相关
  leaseTermInfo.value.text = data.leaseTermMonthCount + data.leaseTermUnit;
  leaseTermInfo.value.value = data.leaseTermId as string;
  // 单独支付方式相关
  paymentTypeInfo.value.text = data.paymentTypeName as string;
  paymentTypeInfo.value.value = data.paymentTypeId as string;
  //   如果是续期，允许修改，
  if (isAllowEdit.value) {
    // 重新计算起止日期
    // 结束日期变成开始日期
    agreementDetailInfo.value.startDate =
      agreementDetailInfo.value.endDate;
    // 结束日期变成开始日期+预订时长
    agreementDetailInfo.value.endDate = dayjs(
      agreementDetailInfo.value.startDate
    )
      .add(agreementDetailInfo.value.leaseTermMonthCount as number, "month")
      .format("YYYY-MM-DD");
    //   重置订单状态
    agreementDetailInfo.value.status = AgreementStatus.RENEW_TO_BE_CONFIRMED;
    //   重置订单来源
    agreementDetailInfo.value.sourceType = AgreementSource.RENEW;
  }
  //   如果是新增的续期,重置id为空
  isAddRenew.value && (agreementDetailInfo.value.id = "");
  //   如果是确认预订，修改状态为已预订，等待确认
  isConfirmAgreement.value &&
    (agreementDetailInfo.value.status = AgreementStatus.SIGNED);
};
// 更正预订时长和支付方式
const correctLeaseTermAndPaymentType = () => {
  // 修正预订时长，如果不存在就改为数组第一项
  let targetTerm =
    leaseTermList.value.find(
      item => item.id === agreementDetailInfo.value.leaseTermId
    ) || leaseTermList.value[0];
  if (targetTerm) {
    console.log("targetTerm", targetTerm);
    leaseTermInfo.value.text = targetTerm.monthCount + targetTerm.unit;
    leaseTermInfo.value.value = targetTerm.id as unknown as string;
  }
  //   修正支付方式，如果不存在就改为数组第一项
  let targetPaymentType =
    paymentTypeList.value.find(
      item => item.id === agreementDetailInfo.value.paymentTypeId
    ) || paymentTypeList.value[0];
  if (targetPaymentType) {
    console.log("targetPaymentType", targetPaymentType);
    paymentTypeInfo.value.text = targetPaymentType.name as string;
    paymentTypeInfo.value.value = targetPaymentType.id as unknown as string;
  }
};
// 跳转到自习室门店的详情页面
const goApartmentDetail = () => {
  router.push({
    path: "/storeDetail",
    query: { id: agreementDetailInfo.value.storeId }
  });
};
// 跳转到座位的详情页面
const goRoomDetail = () => {
  router.push({
    path: "/seatDetail",
    query: { id: agreementDetailInfo.value.seatId }
  });
};
// 续期,修改
const submitHandle = async () => {
  console.log("确认预订");
  try {
    // 预订时长时长不能小于支付方式月份长度
    let leaseTermMonthCount =
      leaseTermList.value.find(
        item => item.id === agreementDetailInfo.value.leaseTermId
      )?.monthCount || 0;

    console.log(leaseTermList.value);
    console.log(agreementDetailInfo.value);
    console.log("=============================");
    console.log("预订时长长度：" + leaseTermMonthCount);

    let paymentTypeMonthCount =
      paymentTypeList.value.find(
        item => item.id === agreementDetailInfo.value.paymentTypeId
      )?.payMonthCount || 0;

    console.log("支付月份长度：" + paymentTypeMonthCount);

    if (+leaseTermMonthCount < +paymentTypeMonthCount) {
      showToast({
        type: "fail",
        message: "预订时长时长不能小于支付方式月份长度"
      });
      return;
    }

    await saveOrUpdateAgreement(agreementDetailInfo.value);
    showToast({ type: "success", message: "操作成功" });
    router.back();
  } catch (error) {
    console.log(error);
  }
};
// 签到
const checkInHandle = async () => {
  try {
    await checkInOrder(agreementDetailInfo.value.id as string);
    showToast({ type: "success", message: "签到成功" });
    await getAgreementDetailHandle();
  } catch (error) {
    console.log(error);
  }
};
// 签退
const checkOutHandle = async () => {
  try {
    await checkOutOrder(agreementDetailInfo.value.id as string);
    showToast({ type: "success", message: "签退成功" });
    await getAgreementDetailHandle();
  } catch (error) {
    console.log(error);
  }
};
onMounted(async () => {
  route.query.id && (await getAgreementDetailHandle());
  await getLeaseTermListHandle();
  await getPaymentTypeListHandle();
  //  如果允许修改，修正预订时长和支付方式
  isAllowEdit.value && correctLeaseTermAndPaymentType();
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
