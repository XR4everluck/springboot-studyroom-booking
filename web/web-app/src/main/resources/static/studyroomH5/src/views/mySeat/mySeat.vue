<template>
  <div class="my-seat-page">
    <h2 class="my-seat-page__title">我的座位</h2>
    <div class="my-seat-page__grid">
      <button
        type="button"
        v-for="item in navList"
        :key="item.path"
        class="my-seat-page__entry"
        @click="router.push(item.path)"
      >
        <SvgIcon :name="item.icon" size="40" />
        <span class="my-seat-page__name">{{ item.name }}</span>
      </button>
    </div>
  </div>
</template>

<script setup lang="ts" name="MySeat">
import { ref } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
const navList = ref([
  {
    icon: "合同",
    name: "我的订单",
    path: "/myOrder"
  },
  {
    icon: "预约",
    name: "我的预约",
    path: "/myVisit"
  },
  {
    icon: "home",
    name: "到店体验",
    path: "/visit"
  },
  {
    icon: "历史",
    name: "浏览历史",
    path: "/browsingHistory"
  }
]);
</script>
<style lang="less" scoped>
.my-seat-page {
  padding: 28px 24px 0;
}

.my-seat-page__title {
  margin: 0 0 16px;
  font-size: 18px;
  font-weight: 600;
  color: var(--van-text-color);
}

.my-seat-page__grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.my-seat-page__entry {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 32px 14px;
  border: 1px solid var(--van-border-color);
  border-radius: 12px;
  background-color: var(--van-background-2);
  color: var(--van-text-color);
  cursor: pointer;
  transition: box-shadow 0.2s, transform 0.2s, border-color 0.2s;

  &:hover {
    border-color: var(--van-primary-color);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }

  &:focus-visible {
    outline: 2px solid var(--van-primary-color);
    outline-offset: 2px;
  }
}

.my-seat-page__name {
  font-size: 14px;
}

@media (max-width: 1100px) {
  .my-seat-page__grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}
</style>
