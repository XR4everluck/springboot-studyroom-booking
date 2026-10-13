<template>
  <div class="history-page">
    <div class="history-page__grid" v-if="seatList?.length">
      <SeatCard v-for="item in seatList" :key="item.id" :data="item" class="history-page__card">
        <template #tags>
          <div>
            <span class="text-[12px] --van-gray-6">{{ item.browseTime }}</span>
          </div>
        </template>
      </SeatCard>
    </div>
    <van-empty v-else-if="!loading" description="暂无浏览历史" />

    <div class="history-page__pager" v-if="total > 0">
      <van-pagination
        v-model="pageInfo.current"
        :page-size="pageInfo.size"
        :total-items="total"
        @change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts" name="BrowsingHistory">
import { ref } from "vue";
import { getHistoryList } from "@/api/search";
import SeatCard from "@/components/SeatCard/SeatCard.vue";
import type { SeatInterface } from "@/api/search/types";
// 分页信息
const pageInfo = ref({ current: 1, size: 10 });
// 总数
const total = ref(0);
// 加载中
const loading = ref(false);
// 座位列表
type SeatInterfaceWithBrowseTime = SeatInterface & { browseTime: string };
const seatList = ref<SeatInterfaceWithBrowseTime[]>([]);
// 翻页
function onPageChange(page: number) {
  pageInfo.value.current = page;
  getHistoryListHandler();
}
// 分页获取浏览历史
async function getHistoryListHandler() {
  loading.value = true;
  try {
    let { data } = await getHistoryList({ ...pageInfo.value });
    seatList.value = data.records.map(item => {
      return {
        id: item.seatId,
        seatCode: item.seatCode,
        price: item.price,
        graphVoList: item.seatGraphVoList,
        labelInfoList: [],
        studyStore: {
          name: item.storeName || "",
          provinceName: item.provinceName || "",
          cityName: item.cityName || "",
          districtName: item.districtName || ""
        },
        browseTime: item.browseTime
      };
    }) as unknown as SeatInterfaceWithBrowseTime[];
    total.value = data.total;
  } finally {
    loading.value = false;
  }
}
getHistoryListHandler();
</script>

<style scoped lang="less">
.history-page {
  padding: 16px 20px 0;
}

.history-page__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.history-page__card {
  margin: 0;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }
}

.history-page__pager {
  display: flex;
  justify-content: center;
  padding: 20px 0 8px;
}

@media (max-width: 1100px) {
  .history-page__grid {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
