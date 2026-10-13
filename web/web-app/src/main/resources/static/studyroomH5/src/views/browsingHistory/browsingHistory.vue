<template>
  <div class="main-container">
    <PullDownRefreshContainer
      :request="getHistoryListHandler"
      ref="pullDownRefreshContainerRef"
      class="min-h-[70vh]"
    >
      <template v-if="seatList?.length">
        <SeatCard v-for="item in seatList" :key="item.id" :data="item">
          <template #tags>
            <div>
              <span class="text-[12px] --van-gray-6">{{
                item.browseTime
              }}</span>
            </div>
          </template>
        </SeatCard>
      </template>
    </PullDownRefreshContainer>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { getHistoryList } from "@/api/search";
import PullDownRefreshContainer from "@/components/PullDownRefreshContainer/PullDownRefreshContainer.vue";
import type { ReqPage } from "@/api/types";
import SeatCard from "@/components/SeatCard/SeatCard.vue";
import type { SeatInterface } from "@/api/search/types";
const pullDownRefreshContainerRef =
  ref<InstanceType<typeof PullDownRefreshContainer>>();
console.log("SeatCard.props", SeatCard);
// 座位列表
// 定义个type
type SeatInterfaceWithBrowseTime = SeatInterface & { browseTime: string };
const seatList = ref<SeatInterfaceWithBrowseTime[]>([]);
// 分页获取座位的信息
async function getHistoryListHandler(pageInfo: ReqPage) {
  //   调用接口
  let { data } = await getHistoryList(pageInfo);
  console.log("pageInfo.current", pageInfo.current);
  let targetRecords = data.records.map(item => {
    return {
      // "id" | "seatCode" | "price" | "graphVoList" | "labelInfoList"
      id: item.seatId,
      seatCode: item.seatCode,
      price: item.price,
      graphVoList: item.seatGraphVoList,
      labelInfoList: [],
      studyStore: {
        //   "name" | "provinceName" | "cityName" | "districtName"
        name: item.storeName || "",
        provinceName: item.provinceName || "",
        cityName: item.cityName || "",
        districtName: item.districtName || ""
      },
      //  浏览时间
      browseTime: item.browseTime
    };
  }) as unknown as SeatInterfaceWithBrowseTime[];
  if (pageInfo.current === 1) {
    seatList.value = targetRecords;
  } else {
    seatList.value = [...seatList.value, ...targetRecords];
  }
  pullDownRefreshContainerRef.value?.setFinished(
    seatList.value.length >= data.total
  );
}
</script>
