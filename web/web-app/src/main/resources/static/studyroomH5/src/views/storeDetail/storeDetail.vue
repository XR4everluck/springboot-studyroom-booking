<template>
  <van-skeleton :row="20" :loading="loading">
    <div class="std" v-if="storeDetailInfo?.id">
      <div class="std__back">
        <van-button icon="arrow-left" size="small" @click="router.back()"
          >返回</van-button
        >
        <span class="std__back-title">自习室门店详情</span>
      </div>

      <div class="std__layout">
        <div class="std__main">
          <!--  轮播图-->
          <van-swipe
            class="std__swipe"
            :autoplay="3000"
            indicator-color="white"
            :show-indicators="true"
          >
            <van-swipe-item
              v-for="item in storeDetailInfo.graphVoList"
              :key="item.url"
            >
              <van-image fit="fill" :src="item.url" width="100%" height="400px">
                <template v-slot:error>加载失败</template>
                <template v-slot:loading>
                  <van-loading type="spinner" size="20" />
                </template>
              </van-image>
            </van-swipe-item>
          </van-swipe>

          <!--    基本信息-->
          <section class="std__card">
            <h3 class="std__card-title">门店介绍</h3>
            <div class="std__intro">{{ storeDetailInfo.introduction }}</div>
            <div class="std__hours">
              营业时间：{{ storeDetailInfo.businessHours }}
            </div>
          </section>

          <!--    配套说明-->
          <section class="std__card">
            <h3 class="std__card-title">配套说明</h3>
            <div class="std__facility">
              <div
                class="std__facility-item"
                v-for="item in storeDetailInfo.facilityInfoList"
                :key="item.id"
              >
                <SvgIcon :name="item.icon" size="26" />
                <span>{{ item.name }}</span>
              </div>
            </div>
          </section>

          <!--    位置详情-->
          <section class="std__card">
            <h3 class="std__card-title">位置详情</h3>
            <div class="std__address">{{ storeDetailInfo.addressDetail }}</div>
            <!--        地图容器-->
            <div id="container" class="std__map"></div>
          </section>

          <!--    可选座位列表-->
          <section class="std__card">
            <h3 class="std__card-title">可选座位列表</h3>
            <div class="std__seat-grid" v-if="seatList?.length">
              <SeatCard
                v-for="item in seatList"
                :key="item.id"
                :data="item"
                class="std__seat-card"
              ></SeatCard>
            </div>
            <van-empty v-else description="暂无可选座位" />
          </section>
        </div>

        <aside class="std__side">
          <section class="std__card">
            <!--      标题-->
            <div class="font-bold std__title">{{ storeDetailInfo.name }}</div>
            <!--      标签-->
            <div class="std__labels">
              <van-tag
                class="last:mr-0 mr-[5px]"
                plain
                v-for="item in storeDetailInfo.labelInfoList"
                :key="item.id"
                type="primary"
                >{{ item.name }}
              </van-tag>
            </div>
            <!--      价格-->
            <div class="std__price">
              <span class="text-red-500 text-[16px]">￥</span>
              <span class="text-red-500 text-[22px]"
                >{{ storeDetailInfo.minPrice }}/时起</span
              >
            </div>
            <!--    到店体验-->
            <van-button type="primary" block @click="appointmentToViewHandle"
              >到店体验</van-button
            >
          </section>
        </aside>
      </div>
    </div>

    <!-- 门店不存在 / 已被下架时给出明确提示，避免骨架屏常驻 -->
    <van-empty
      v-else
      class="std__empty"
      description="未找到该自习室门店，可能已下架"
    >
      <van-button round type="primary" @click="router.push('/search')"
        >去找座位</van-button
      >
    </van-empty>
  </van-skeleton>
</template>
<script setup lang="ts">
import { getStoreDetailById, getSeatListByStoreId } from "@/api/search";
import { onMounted, ref } from "vue";
import type { StoreInterface } from "@/api/search/types";
import { useMap } from "@/hooks/useMap";
import poiMarkerRed from "@/assets/poi-marker-red.png";
import SeatCard from "@/components/SeatCard/SeatCard.vue";
import type { SeatInterface } from "@/api/search/types";
import { useRouter, useRoute } from "vue-router";
const router = useRouter();
const route = useRoute();
// 自习室门店的详情信息
const storeDetailInfo = ref<StoreInterface>({} as StoreInterface);
// 页面加载态：请求成功/失败/门店不存在都要收敛，避免骨架屏常驻
const loading = ref(true);
// 获取自习室门店的详情信息
const getStoreDetailHandle = async () => {
  const { data } = await getStoreDetailById(route.query.id as string);
  storeDetailInfo.value = data || ({} as StoreInterface);
};
// 座位列表（门店详情内一次取满，size 给足即可覆盖常规门店座位量）
const seatList = ref<SeatInterface[]>([]);
async function getSeatListHandler() {
  if (!storeDetailInfo.value?.id) return;
  const { data } = await getSeatListByStoreId({
    id: route.query.id as string,
    current: 1,
    size: 100
  });
  const records = data?.records || [];
  records.forEach(item => {
    item.studyStore = storeDetailInfo.value;
  });
  seatList.value = records;
}
//#region <高德地图相关>
// 地图实例
const { AMap, initMap } = useMap();
function initMapPage(opts?: { lng: number; lat: number }) {
  // 未配置有效高德 Key 或脚本加载失败时跳过地图渲染，不影响详情其余信息
  if (!AMap.value) return;
  const map = new AMap.value.Map("container", {
    zoom: 19, //初始地图级别
    center: [opts?.lng, opts?.lat], //初始地图中心点
    showIndoorMap: false //关闭室内地图
  });
  const icon = new AMap.value.Icon({
    size: new AMap.value.Size(25, 34), // 图标尺寸
    image: poiMarkerRed, // Icon的图像
    imageSize: new AMap.value.Size(25, 34)
  });
  const marker = new AMap.value.Marker({
    icon: icon,
    position: [opts?.lng, opts?.lat],
    anchor: "bottom-center"
  });
  map.add(marker);
  map.setFitView();
}
// 到店体验
const appointmentToViewHandle = () => {
  router.push({
    path: "/visit",
    query: { storeId: storeDetailInfo.value.id }
  });
};
//#endregion
onMounted(async () => {
  try {
    if (!route.query.id) return;
    await getStoreDetailHandle();
    if (!storeDetailInfo.value?.id) return;
    await getSeatListHandler();
    await initMap();
    initMapPage({
      lng: +storeDetailInfo.value.longitude,
      lat: +storeDetailInfo.value.latitude
    });
  } catch (error) {
    console.error("门店详情初始化失败", error);
  } finally {
    loading.value = false;
  }
});
</script>

<style scoped lang="less">
.std {
  padding: 24px 24px 0;
}

.std__back {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;
}

.std__empty {
  padding: 40px 24px 0;
}

.std__back-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--van-text-color);
}

.std__layout {
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.std__main {
  flex: 1;
  min-width: 0;
}

.std__side {
  width: 320px;
  flex-shrink: 0;
}

.std__swipe {
  border-radius: 12px;
  overflow: hidden;
}

.std__card {
  box-sizing: border-box;
  margin-top: 16px;
  padding: 16px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
}

.std__side .std__card {
  margin-top: 0;
  position: sticky;
  top: 76px;
}

.std__card-title {
  margin: 0 0 12px;
  font-size: 15px;
  font-weight: 600;
  color: var(--van-text-color);
}

.std__title {
  font-size: 17px;
  line-height: 1.4;
}

.std__labels {
  margin: 8px 0;
}

.std__price {
  margin: 6px 0 14px;
}

.std__intro {
  font-size: 14px;
  line-height: 1.7;
  color: var(--van-text-color-2);
}

.std__hours {
  margin-top: 8px;
  font-size: 13px;
  color: var(--van-text-color-3);
}

.std__facility {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.std__facility-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  width: 72px;
  font-size: 12px;
  text-align: center;
  color: var(--van-text-color-2);
}

.std__address {
  margin-bottom: 10px;
  font-size: 13px;
  color: var(--van-text-color-2);
}

.std__map {
  width: 100%;
  height: 360px;
  border-radius: 8px;
  background-color: var(--van-active-color);
}

.std__seat-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.std__seat-card {
  margin: 0;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }
}

@media (max-width: 1100px) {
  .std__layout {
    flex-direction: column;
  }

  .std__side {
    width: 100%;
  }

  .std__side .std__card {
    position: static;
  }
}
</style>
