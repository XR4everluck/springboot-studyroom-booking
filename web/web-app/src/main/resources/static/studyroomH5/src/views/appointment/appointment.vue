<template>
  <van-skeleton :row="20" :loading="loading">
    <!-- 已选定门店：展示预约表单 -->
    <div class="ap" v-if="storeDetailInfo?.id">
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
                  @cancel="showTime = false"
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
              <van-button
                round
                block
                type="primary"
                native-type="submit"
                :loading="submitting"
                loading-text="提交中..."
              >
                {{ formData.id ? "重新预约" : "到店体验" }}
              </van-button>
            </div>
          </van-form>
        </section>
      </div>
    </div>

    <!-- 从顶部导航直接进入且未携带门店：给出明确引导，避免骨架屏常驻造成“卡顿”假象 -->
    <van-empty
      v-else
      class="ap__empty"
      description="请先选择一家自习室门店，再发起到店体验预约"
    >
      <van-button
        round
        type="primary"
        class="ap__empty-btn"
        @click="router.push('/search')"
      >
        去找座位
      </van-button>
    </van-empty>
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
// 页面加载态：无论走哪个分支（含从导航空参进入、请求失败）都必须在 finally 中收敛，
// 否则骨架屏会一直挂着，看起来就像「板块启动卡住」
const loading = ref(true);
// 提交中标记，避免重复点击重复下单
const submitting = ref(false);
// 获取自习室门店的详情信息
const getStoreDetailHandle = async () => {
  const storeId = route.query.storeId as string;
  if (!storeId) return;
  const { data } = await getStoreDetailById(storeId);
  storeDetailInfo.value = data || ({} as StoreInterface);
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
async function onSubmit() {
  // 兜底校验：没有门店时不允许提交（正常路径下表单已被空态替换）
  if (!storeDetailInfo.value?.id) {
    showToast("请先选择要体验的自习室门店");
    return;
  }
  submitting.value = true;
  try {
    await saveOrUpdateAppointment({
      ...formData.value,
      storeId: storeDetailInfo.value.id as number | string
    });
    showToast("操作成功");
    route.query.appointmentId
      ? await router.back()
      : await router.replace("/myVisit");
  } finally {
    submitting.value = false;
  }
}

//#endregion
// 预约信息
async function getAppointmentDetailHandle() {
  const appointmentId = route.query.appointmentId as string;
  if (!appointmentId) return;
  const { data } = await getAppointmentDetailById(appointmentId);
  if (!data) return;
  Object.keys(formData.value).forEach(key => {
    if ((data as any)[key] !== undefined) {
      (formData.value as any)[key] = (data as any)[key];
    }
  });
  // 单独处理日期（后端时间字段异常时不至于直接抛错卡住页面）
  const [datePart, timePart] = (data.appointmentTime || "").split(" ");
  if (datePart) dateInfo.value.date = datePart.split("-");
  if (timePart) dateInfo.value.time = timePart.split(":");
  storeDetailInfo.value = data.storeItemVo || ({} as StoreInterface);
}
onMounted(async () => {
  try {
    if (route.query.storeId) {
      await getStoreDetailHandle();
    }
    if (route.query.appointmentId) {
      await getAppointmentDetailHandle();
    }
  } catch (error) {
    console.error("到店体验页面初始化失败", error);
  } finally {
    loading.value = false;
  }
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

.ap__empty {
  padding: 40px 24px 0;
}

.ap__empty-btn {
  min-width: 160px;
}

:deep(.van-card) {
  background: var(--van-background-2) !important;
}
</style>
