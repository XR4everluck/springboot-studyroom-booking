<template>
  <van-skeleton :row="20" :loading="loading">
    <div class="ma-page">
      <div class="ma-page__grid">
        <van-card
          class="rounded-xl shadow ma-page__card"
          v-for="item in appointmentList"
          :key="item.id"
          @click="goAppointmentDetail(item)"
        >
        <!--      title-->
        <template #title>
          <h2 class="text-[15px] font-bold">{{ item.storeName }}</h2>
        </template>
        <!--    thumb-->
        <template #thumb>
          <van-image
            class="w-full h-full object-cover"
            :src="item.graphVoList?.[0]?.url || '失败'"
          >
            <template v-slot:error>加载失败</template>
            <template v-slot:loading>
              <van-loading type="spinner" size="20" />
            </template>
          </van-image>
        </template>
        <!--      tags-->
        <template #tags>
          <van-tag
            v-if="item.appointmentStatus === AppointmentStatus.WAITING"
            class="mt-[10px]"
            type="success"
            size="medium"
            >{{
              getLabelByValue(AppointmentStatusMap, item.appointmentStatus)
            }}</van-tag
          >
          <van-tag v-else class="mt-[10px]" type="default" size="medium">{{
            getLabelByValue(AppointmentStatusMap, item.appointmentStatus)
          }}</van-tag>
        </template>
        <!--      price-->
        <template #price>
          <div class="flex justify-between">
            <div class="text-[12px] text-red-500">预约时间</div>
            <div class="text-[12px] text-red-500">
              {{ item.appointmentTime }}
            </div>
          </div>
        </template>
        </van-card>
      </div>

      <van-empty
        v-if="!loading && !appointmentList.length"
        description="暂无预约"
      />
    </div>
  </van-skeleton>
</template>
<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import type { AppointmentItemInterface } from "@/api/search/types";
import { getMyAppointmentList } from "@/api/search";
import {
  AppointmentStatus,
  AppointmentStatusMap,
  getLabelByValue
} from "@/enums/constEnums";

const router = useRouter();
// 预约列表
const appointmentList = ref<AppointmentItemInterface[]>([]);
// 列表加载态：请求无论成功还是失败都要收敛，否则骨架屏会一直挂着
const loading = ref(true);
// 获取预约列表
const getAppointmentListHandle = async () => {
  try {
    const { data } = await getMyAppointmentList();
    appointmentList.value = data || [];
  } catch (error) {
    console.error("获取预约列表失败", error);
  } finally {
    loading.value = false;
  }
};
// 跳转到自习室门店的详情页面
const goAppointmentDetail = (item: AppointmentItemInterface) => {
  router.push({
    path: "/visit",
    query: { appointmentId: item.id }
  });
};
onMounted(async () => {
  await getAppointmentListHandle();
});
</script>

<style scoped lang="less">
.ma-page {
  padding: 24px 24px 0;
}

.ma-page__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.ma-page__card {
  margin: 0;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }
}

@media (max-width: 1100px) {
  .ma-page__grid {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
