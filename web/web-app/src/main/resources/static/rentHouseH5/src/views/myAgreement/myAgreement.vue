<template>
  <van-skeleton :row="20" :loading="!agreementList">
    <div class="p-[10px]">
      <van-card
        class="rounded-xl shadow"
        v-for="item in agreementList"
        :key="item.id"
        @click="goAgreementDetail(item)"
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
              `${item.leaseStartDate} 至 ${item.leaseEndDate}`
            }}</span>
          </div>
        </template>
        <!--      tag-->
        <template #tag>
          <van-tag type="primary" size="medium" class="translate-y-[-3px]">{{
            getLabelByValue(AgreementSourceMap, item.sourceType)
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
            v-if="item.leaseStatus === AgreementStatus.WAITING"
            class="mt-[5px]"
            type="primary"
            size="medium"
            >{{
              getLabelByValue(AgreementStatusMap, item.leaseStatus)
            }}</van-tag
          >
          <van-tag
            v-else-if="
              item.leaseStatus === AgreementStatus.RENEW_TO_BE_CONFIRMED
            "
            class="mt-[5px]"
            type="primary"
            size="medium"
            >{{
              getLabelByValue(AgreementStatusMap, item.leaseStatus)
            }}</van-tag
          >
          <van-tag
            v-else-if="item.leaseStatus === AgreementStatus.SIGNED"
            class="mt-[5px]"
            type="success"
            size="medium"
            >{{
              getLabelByValue(AgreementStatusMap, item.leaseStatus)
            }}</van-tag
          >
          <van-tag
            v-else-if="item.leaseStatus === AgreementStatus.TO_BE_CONFIRMED"
            class="mt-[5px]"
            type="danger"
            size="medium"
            >{{
              getLabelByValue(AgreementStatusMap, item.leaseStatus)
            }}</van-tag
          >
          <van-tag v-else class="mt-[5px]" type="default" size="medium">{{
            getLabelByValue(AgreementStatusMap, item.leaseStatus)
          }}</van-tag>
        </template>
        <!--      price-->
        <template #price>
          <!--      价格-->
          <div>
            <span class="text-red-500 text-[14px]">￥</span>
            <span class="text-red-500 text-[16px]">{{ item.rent }}/月</span>
          </div>
        </template>
        <template #footer>
          <div class="absolute bottom-[7px] right-[15px]" @click.stop>
            <van-button
              v-if="item.leaseStatus === AgreementStatus.SIGNED"
              size="mini"
              plain
              type="primary"
              @click="
                goAgreementDetail(item, {
                  isEdit: true,
                  isRenew: true,
                  isAdd: true
                })
              "
              >续期</van-button
            >
            <van-button
              v-if="item.leaseStatus === AgreementStatus.RENEW_TO_BE_CONFIRMED"
              size="mini"
              type="primary"
              plain
              @click="
                goAgreementDetail(item, {
                  isRenew: true,
                  isEdit: true,
                  isAdd: false
                })
              "
              >修改</van-button
            >
            <van-button
              v-if="item.leaseStatus === AgreementStatus.WAITING"
              size="mini"
              type="primary"
              plain
              @click="
                goAgreementDetail(item, { isEdit: true, isConfirm: true })
              "
              >确认</van-button
            >
            <van-button
              v-if="item.leaseStatus === AgreementStatus.SIGNED"
              size="mini"
              plain
              type="danger"
              @click="rentRefundHandle(item)"
              >提前签退</van-button
            >
          </div>
        </template>
      </van-card>
    </div>

    <van-empty v-if="agreementList?.length <= 0" description="搜索不到" />
  </van-skeleton>
</template>
<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import type { AgreementItemInterface } from "@/api/search/types";
import { getMyAgreementList, saveOrUpdateAgreement } from "@/api/search";
import { showConfirmDialog, showToast } from "vant";
import {
  AgreementSourceMap,
  AgreementStatus,
  AgreementStatusMap,
  getLabelByValue
} from "@/enums/constEnums";

const router = useRouter();
// 订单列表
const agreementList = ref<AgreementItemInterface[]>();
// 获取订单列表
const getAgreementListHandle = async () => {
  const { data } = await getMyAgreementList();
  agreementList.value = data;
};
// 跳转到订单详情
const goAgreementDetail = (
  item: AgreementItemInterface,
  query: object = {}
) => {
  router.push({
    path: "/order",
    query: { id: item.id, ...query }
  });
};
// 提前签退
const rentRefundHandle = (item: AgreementItemInterface) => {
  showConfirmDialog({
    title: "提前签退",
    message: "确定要提前签退吗？",
    cancelButtonText: "取消",
    confirmButtonText: "确定"
  })
    .then(async () => {
      await saveOrUpdateAgreement({
        id: item.id,
        status: AgreementStatus.TO_BE_CONFIRMED
      });
      //   操作成功
      showToast({
        type: "success",
        message: "操作成功"
      });
      //   修改当前订单状态
      item.leaseStatus = AgreementStatus.TO_BE_CONFIRMED;
    })
    .catch(() => {});
};
onMounted(async () => {
  await getAgreementListHandle();
});
</script>

<style scoped lang="less"></style>
