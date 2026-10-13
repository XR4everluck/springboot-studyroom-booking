<template>
  <van-skeleton :row="20" :loading="!storeDetailInfo?.id">
    <div class="ap">
      <div class="ap__back">
        <van-button icon="arrow-left" size="small" @click="router.back()"
          >返回</van-button
        >
        <span class="ap__back-title">到店体验</span>
      </div>

      <div class="ap__wrap">
        <!--    预约自习室门店-->
        <section class="ap__card">
          <h3 class="ap__card-title">预约自习室门店</h3>
          <StoreCard :data="storeDetailInfo"></StoreCard>
        </section>

        <!--    预约信息-->
        <section class="ap__card">
          <h3 class="ap__card-title">预约信息</h3>
          <van-form @submit="onSubmit">
            <van-cell-group>
              <!--            姓名-->
              <van-field
                v-model.trim="formData.name"
                name="name"
                label="姓名"
                autocomplete="off"
                placeholder="请输入姓名"
                :rules="[{ required: true, message: '请填写姓名' }]"
              />
              <!--            手机号-->
              <van-field
                v-model.trim="formData.phone"
                name="phone"
                label="手机号"
                type="tel"
                autocomplete="off"
                placeholder="请输入手机号"
                :rules="[
                  {
                    required: true,
                    pattern: /^1([3589]\d|4[5-9]|6[1-2,4-7]|7[0-8])\d{8}$/,
                    message: '请正确填写手机号'
                  }
                ]"
              />
              <!--            预约日期-->
              <van-field
                v-model="formData.date"
                is-link
                readonly
                name="date"
                label="预约日期"
                placeholder="点击选择日期"
                @click="showDate = true"
                :rules="[{ required: true, message: '请选择预约日期' }]"
              />
              <van-popup v-model:show="showDate" position="center" round>
                <van-date-picker
                  :min-date="new Date()"
                  @confirm="onConfirmAppointmentDateHandle"
                  @cancel="showDate = false"
                />
              </van-popup>
              <!--            预约时间-->
              <van-field
                v-model="formData.time"
                is-link
                readonly
                name="time"
                label="预约时间"
                placeholder="点击选择时间"
                @click="showTime = true"
                :rules="[{ required: true, message: '请选择预约时间' }]"
              />
              <van-popup v-model:show="showTime" position="center" round>
                <van-time-picker
                  v-model="dateInfo.time"
                  title="选择时间"
                  @confirm="onConfirmAppointmentTimeHandle"
                  @cancel="showDate = false"
                  :columns-type="['hour', 'minute', 'second']"
              /></van-popup>
              <!--            营业时间提示-->
              <van-field
                v-if="storeDetailInfo?.businessHours"
                label="营业时间"
                readonly
                :input-align="'right'"
              >
                <template #input>
                  <span class="text-[12px] --van-gray-6">
                    {{
                      `门店营业时间 ${storeDetailInfo.businessHours}，预约时间需在此范围内`
                    }}
                  </span>
                </template>
              </van-field>
              <!--            备注信息-->
              <van-field
                v-model.trim="formData.additionalInfo"
                name="additionalInfo"
                label="备注信息"
                rows="3"
                autosize
                type="textarea"
                autocomplete="off"
                placeholder="请输入备注信息"
                show-word-limit
                maxlength="50"
              />
            </van-cell-group>
            <div class="ap__submit">
              <van-button round block type="primary" native-type="submit">
                {{ formData.id ? "重新预约" : "到店体验" }}
              </van-button>
            </div>
          </van-form>
        </section>
      </div>
    </div>
  </van-skeleton>
</template>
<script setup lang="ts">
import {
  getStoreDetailById,
  getAppointmentDetailById,
  saveOrUpdateAppointment
} from "@/api/search";
import { onMounted, ref, watch } from "vue";
import dayjs from "dayjs";
import type {
  StoreInterface,
  AppointmentQueryInterface
} from "@/api/search/types";
import { useRoute, useRouter } from "vue-router";
import StoreCard from "@/components/StoreCard/StoreCard.vue";
import { AppointmentStatus } from "@/enums/constEnums";
import { showToast } from "vant";
const route = useRoute();
const router = useRouter();
// 自习室门店的详情信息
const storeDetailInfo = ref<StoreInterface>({} as StoreInterface);
// 获取自习室门店的详情信息
const getStoreDetailHandle = async () => {
  const { data } = await getStoreDetailById(route.query.storeId as string);
  storeDetailInfo.value = data;
};
//#region <form表单相关>
const formData = ref<
  AppointmentQueryInterface & { date: string; time: string }
>({
  id: "",
  name: "",
  phone: "",
  storeId: (route.query.storeId as string) || "",
  appointmentTime: "",
  additionalInfo: "",
  appointmentStatus: AppointmentStatus.WAITING,
  date: "",
  time: ""
});
// 日期选择器
const dateInfo = ref({
  date: [
    dayjs().get("year").toString(),
    (dayjs().get("month") + 1).toString().padStart(2, "0"),
    dayjs().get("date").toString().padStart(2, "0")
  ],
  time: [
    dayjs().get("hour").toString().padStart(2, "0"),
    dayjs().get("minute").toString().padStart(2, "0"),
    dayjs().get("second").toString().padStart(2, "0")
  ]
});
// 监视dateInfo
watch(
  dateInfo,
  newVal => {
    formData.value.date = newVal.date.join("-");
    formData.value.time = `${newVal.time.join(":")}`;
    formData.value.appointmentTime = `${formData.value.date} ${formData.value.time}`;
  },
  { immediate: true, deep: true }
);
const showDate = ref(false);
const onConfirmAppointmentDateHandle = (date: any) => {
  dateInfo.value.date = date.selectedValues;
  showDate.value = false;
};
// 时间选择器
const showTime = ref(false);
const onConfirmAppointmentTimeHandle = (date: any) => {
  showTime.value = false;
};
// 提交表单
async function onSubmit(values: any) {
  await saveOrUpdateAppointment(formData.value);
  showToast("操作成功");
  route.query.storeId
    ? await router.replace("/myVisit")
    : await router.back();
}

//#endregion
// 预约信息
async function getAppointmentDetailHandle() {
  const { data } = await getAppointmentDetailById(
    route.query.appointmentId as string
  );
  Object.keys(formData.value).forEach(key => {
    (formData.value as any)[key] = (data as any)[key];
  });
  // 单独处理日期
  dateInfo.value.date = data.appointmentTime.split(" ")[0].split("-");
  dateInfo.value.time = data.appointmentTime.split(" ")[1].split(":");
  storeDetailInfo.value = data.storeItemVo;
}
onMounted(async () => {
  route.query.storeId && (await getStoreDetailHandle());
  route.query.appointmentId && (await getAppointmentDetailHandle());
});
</script>

<style scoped lang="less">
.ap {
  padding: 24px 24px 0;
}

.ap__back {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.ap__back-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--van-text-color);
}

.ap__wrap {
  max-width: 640px;
  margin: 0 auto;
}

.ap__card {
  box-sizing: border-box;
  margin-top: 16px;
  padding: 16px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);

  &:first-child {
    margin-top: 0;
  }
}

.ap__card-title {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--van-text-color);
}

.ap__submit {
  margin-top: 24px;
}

:deep(.van-card) {
  background: var(--van-background-2) !important;
}
</style>
