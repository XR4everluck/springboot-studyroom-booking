<template>
  <div class="search-page">
    <SearchBar :confirm-callback="confirmCallback"></SearchBar>

    <div class="search-page__main">
      <div class="search-page__grid" v-if="seatList?.length">
        <SeatCard
          v-for="item in seatList"
          :key="item.id"
          :data="item"
          class="search-page__card"
        ></SeatCard>
      </div>
      <div v-else-if="loading" class="search-page__loading">
        <van-loading size="24" vertical>加载中...</van-loading>
      </div>
      <van-empty v-else description="未找到符合条件的座位" />

      <div class="search-page__pager" v-if="total > 0">
        <van-pagination
          v-model="pageInfo.current"
          :page-size="pageInfo.size"
          :total-items="total"
          @change="onPageChange"
        />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="Search">
import SearchBar from "@/components/SearchBar/SearchBar.vue";
import type { SeatInterface, SeatListQueryInterface } from "@/api/search/types";
import { ref } from "vue";
import { getSeatList } from "@/api/search";
import SeatCard from "@/components/SeatCard/SeatCard.vue";
// 搜索参数
const searchParams = ref<Partial<SeatListQueryInterface>>({});
// 分页信息
const pageInfo = ref({ current: 1, size: 10 });
// 总数
const total = ref(0);
// 加载中
const loading = ref(false);
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
  pageInfo.value.current = 1;
  getSeatListHandler();
}
// 翻页
function onPageChange(page: number) {
  pageInfo.value.current = page;
  getSeatListHandler();
}
// 分页获取座位的信息
async function getSeatListHandler() {
  loading.value = true;
  try {
    const { data } = await getSeatList({
      ...searchParams.value,
      ...pageInfo.value
    });
    seatList.value = data.records;
    total.value = data.total;
  } finally {
    loading.value = false;
  }
}
getSeatListHandler();
</script>

<style scoped lang="less">
.search-page {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 24px 24px 0;
}

.search-page__main {
  flex: 1;
  min-width: 0;
}

.search-page__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.search-page__card {
  margin: 0;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }
}

.search-page__loading {
  padding: 64px 0;
  display: flex;
  justify-content: center;
}

.search-page__pager {
  display: flex;
  justify-content: center;
  padding: 20px 0 8px;
}

@media (max-width: 1100px) {
  .search-page__grid {
    grid-template-columns: minmax(0, 1fr);
  }
}

// 窄屏：筛选面板置顶全宽，内容区纵向排布
@media (max-width: 768px) {
  .search-page {
    flex-direction: column;
  }

  .search-page :deep(.filter-panel) {
    width: 100%;
  }
}
</style>
