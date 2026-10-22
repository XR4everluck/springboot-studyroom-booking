<template>
  <van-skeleton :row="20" :loading="loading">
    <div class="order-page">
      <div class="order-page__grid">
        <van-card
          class="rounded-xl shadow order-page__card"
          v-for="item in orderList"
          :key="item.id"
          @click="goOrderDetail(item)"
        >
        <!--      title-->
        <template #title>
          <h2 class="text-[15px] font-bold">
            {{ `${item.storeName} ${item.seatCode}座位` }}
          </h2>
        </template>
        <!--      desc-->
        <template #desc>
          <div>
            <span class="text-[12px] --van-gray-6">{{
              `${item.bookingStartDate} 至 ${item.bookingEndDate}`
            }}</span>
          </div>
        </template>
        <!--      tag-->
        <template #tag>
          <van-tag type="primary" size="medium" class="translate-y-[-3px]">{{
            getLabelByValue(OrderSourceMap, item.sourceType)
          }}</van-tag>
        </template>
        <!--    thumb-->
        <template #thumb>
          <van-image
            class="w-full h-full object-cover"
            :src="item.seatGraphVoList?.[0]?.url || '失败'"
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
            v-if="item.bookingStatus === OrderStatus.WAITING"
            class="mt-[5px]"
            type="primary"
            size="medium"
            >{{
              getLabelByValue(OrderStatusMap, item.bookingStatus)
            }}</van-tag
          >
          <van-tag
            v-else-if="
              item.bookingStatus === OrderStatus.RENEW_TO_BE_CONFIRMED
            "
            class="mt-[5px]"
            type="primary"
            size="medium"
            >{{
              getLabelByValue(OrderStatusMap, item.bookingStatus)
            }}</van-tag
          >
          <van-tag
            v-else-if="item.bookingStatus === OrderStatus.SIGNED"
            class="mt-[5px]"
            type="success"
            size="medium"
            >{{
              getLabelByValue(OrderStatusMap, item.bookingStatus)
            }}</van-tag
          >
          <van-tag
            v-else-if="item.bookingStatus === OrderStatus.TO_BE_CONFIRMED"
            class="mt-[5px]"
            type="danger"
            size="medium"
            >{{
              getLabelByValue(OrderStatusMap, item.bookingStatus)
            }}</van-tag
          >
          <van-tag v-else class="mt-[5px]" type="default" size="medium">{{
            getLabelByValue(OrderStatusMap, item.bookingStatus)
          }}</van-tag>
        </template>
        <!--      price-->
        <template #price>
          <!--      价格-->
          <div>
            <span class="text-red-500 text-[14px]">￥</span>
            <span class="text-red-500 text-[16px]">{{ item.price }}/时</span>
          </div>
        </template>
        <template #footer>
          <div class="absolute bottom-[7px] right-[15px]" @click.stop>
            <van-button
              v-if="item.bookingStatus === OrderStatus.SIGNED"
              size="mini"
              plain
              type="primary"
              @click="
                goOrderDetail(item, {
                  isEdit: true,
                  isRenew: true,
                  isAdd: true
                })
              "
              >续期</van-button
            >
            <van-button
              v-if="item.bookingStatus === OrderStatus.RENEW_TO_BE_CONFIRMED"
              size="mini"
              type="primary"
              plain
              @click="
                goOrderDetail(item, {
                  isRenew: true,
                  isEdit: true,
                  isAdd: false
                })
              "
              >修改</van-button
            >
            <van-button
              v-if="item.bookingStatus === OrderStatus.WAITING"
              size="mini"
              type="primary"
              plain
              @click="
                goOrderDetail(item, { isEdit: true, isConfirm: true })
              "
              >确认</van-button
            >
            <van-button
              v-if="item.bookingStatus === OrderStatus.SIGNED"
              size="mini"
              plain
              type="danger"
              :loading="pendingId === item.id"
              @click="refundHandle(item)"
              >提前签退</van-button
            >
          </div>
        </template>
        </van-card>
      </div>

      <van-empty v-if="!loading && !orderList.length" description="暂无订单" />
    </div>
  </van-skeleton>
</template>
<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import type { OrderItemInterface } from "@/api/search/types";
import { getMyOrderList, updateOrderStatusById } from "@/api/search";
import { showConfirmDialog, showToast } from "vant";
import {
  OrderSourceMap,
  OrderStatus,
  OrderStatusMap,
  getLabelByValue
} from "@/enums/constEnums";

const router = useRouter();
// 订单列表
const orderList = ref<OrderItemInterface[]>([]);
// 列表加载态：请求无论成功还是失败都要收敛，否则骨架屏会一直挂着
const loading = ref(true);
// 正在处理的订单 id，避免同一订单重复提交
const pendingId = ref<number | string | null>(null);
// 获取订单列表
const getOrderListHandle = async () => {
  try {
    const { data } = await getMyOrderList();
    orderList.value = data || [];
  } catch (error) {
    console.error("获取订单列表失败", error);
  } finally {
    loading.value = false;
  }
};
// 跳转到订单详情
const goOrderDetail = (
  item: OrderItemInterface,
  query: object = {}
) => {
  router.push({
    path: "/order",
    query: { id: item.id, ...query }
  });
};
// 提前签退
// 注意：这里必须走「按 id 更新状态」接口。原先复用 saveOrUpdate 只传 id+status，
// 后端 createOrUpdateOrder 会先按 storeId 查门店，storeId 为空必然抛 DATA_ERROR，
// 表现为「点提前签退一定失败」。
const refundHandle = (item: OrderItemInterface) => {
  showConfirmDialog({
    title: "提前签退",
    message: "确定要提前签退吗？",
    cancelButtonText: "取消",
    confirmButtonText: "确定"
  })
    .then(async () => {
      if (pendingId.value === item.id) return;
      pendingId.value = item.id;
      try {
        await updateOrderStatusById({
          id: item.id,
          status: OrderStatus.TO_BE_CONFIRMED
        });
        showToast({
          type: "success",
          message: "操作成功"
        });
        //   修改当前订单状态
        item.bookingStatus = OrderStatus.TO_BE_CONFIRMED;
      } finally {
        pendingId.value = null;
      }
    })
    .catch(() => {});
};
onMounted(async () => {
  await getOrderListHandle();
});
</script>

<style scoped lang="less">
.order-page {
  padding: 24px 24px 0;
}

.order-page__grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.order-page__card {
  margin: 0;
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s;

  &:hover {
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
    transform: translateY(-2px);
  }
}

@media (max-width: 1100px) {
  .order-page__grid {
    grid-template-columns: minmax(0, 1fr);
  }
}
</style>
