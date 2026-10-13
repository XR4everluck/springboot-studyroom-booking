<template>
  <SearchBar :confirm-callback="confirmCallback"></SearchBar>

  <div class="main-container">
    <PullDownRefreshContainer
      :request="getSeatListHandler"
      ref="pullDownRefreshContainerRef"
      class="min-h-[70vh]"
    >
      <template v-if="seatList?.length">
        <SeatCard
          v-for="item in seatList"
          :key="item.id"
          :data="item"
        ></SeatCard>
      </template>
    </PullDownRefreshContainer>
  </div>
</template>

<script setup lang="ts" name="Search">
import SearchBar from "@/components/SearchBar/SearchBar.vue";
import type { SeatInterface, SeatListQueryInterface } from "@/api/search/types";
import { ref } from "vue";
import { getSeatList } from "@/api/search";
import PullDownRefreshContainer from "@/components/PullDownRefreshContainer/PullDownRefreshContainer.vue";
import type { ReqPage } from "@/api/types";
import SeatCard from "@/components/SeatCard/SeatCard.vue";
// 搜索参数
const searchParams = ref<Partial<SeatListQueryInterface>>({});
const pullDownRefreshContainerRef =
  ref<InstanceType<typeof PullDownRefreshContainer>>();
// 座位列表
const seatList = ref<SeatInterface[]>([]);
// 点击查找，搜索条件
function confirmCallback(params: SeatListQueryInterface) {
  //    收集参数，调用接口
  let targetObi = {};
  //   去除空值
  for (let key in params) {
    if ((params as any)[key] !== "") {
      (targetObi as any)[key] = (params as any)[key];
    }
  }
  searchParams.value = targetObi;
  pullDownRefreshContainerRef.value?.onRefresh();
}
// 分页获取座位的信息
async function getSeatListHandler(pageInfo: ReqPage) {
  //   调用接口
  const { data } = await getSeatList({ ...searchParams.value, ...pageInfo });
  console.log("pageInfo.current", pageInfo.current);
  if (pageInfo.current === 1) {
    seatList.value = data.records;
  } else {
    seatList.value = [...seatList.value, ...data.records];
  }
  pullDownRefreshContainerRef.value?.setFinished(
    seatList.value.length >= data.total
  );
}
</script>
