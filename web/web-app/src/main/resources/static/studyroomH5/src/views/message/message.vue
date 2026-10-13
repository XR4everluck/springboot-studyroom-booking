<template>
  <div class="message-page">
    <div class="message-page__cats">
      <div
        v-for="item in navList"
        :key="item.name"
        class="message-page__cat"
        :style="{ background: item.color }"
      >
        <van-icon :name="item.icon" :badge="item.badge" size="25" />
        <span class="message-page__cat-name">{{ item.name }}</span>
      </div>
    </div>

    <div class="message-page__list">
      <div class="message-page__item" v-for="item in 9" :key="item">
        <van-image
          round
          width="56px"
          height="56px"
          src="https://fastly.jsdelivr.net/npm/@vant/assets/cat.jpeg"
        >
          <template v-slot:error>加载失败</template>
          <template v-slot:loading>
            <van-loading type="spinner" size="20" />
          </template>
        </van-image>
        <div class="message-page__body">
          <div class="message-page__head">
            <div class="font-bold">游湖123456</div>
            <div class="--van-gray-6">昨天</div>
          </div>
          <div class="--van-gray-6 van-ellipsis">
            噫吁嚱，危乎高哉！蜀道之难，难于上青天！蚕丛及鱼凫，开国何茫然！尔来四万八千岁，不与秦塞通人烟。西当太白有鸟道，可以横绝峨眉巅。
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="Message">
import { ref } from "vue";
const navList = ref([
  {
    icon: "volume-o",
    name: "通知",
    color: "#f1be4a",
    badge: null
  },
  {
    icon: "chat-o",
    name: "评论",
    color: "#51b290",
    badge: 9
  },
  {
    icon: "good-job-o",
    name: "赞",
    color: "#f39653",
    badge: null
  }
]);
</script>
<style lang="less" scoped>
.message-page {
  max-width: 760px;
  margin: 0 auto;
  padding: 28px 24px 0;
}

.message-page__cats {
  display: flex;
  justify-content: center;
  gap: 40px;
  padding: 20px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
}

.message-page__cat {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  width: 88px;
  padding: 14px 0;
  border-radius: 12px;
  color: #fff;
}

.message-page__cat-name {
  font-size: 13px;
}

.message-page__list {
  margin-top: 16px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
  overflow: hidden;
}

.message-page__item {
  display: flex;
  gap: 12px;
  padding: 18px 24px;
  cursor: pointer;
  transition: background-color 0.15s;

  &:hover {
    background-color: var(--van-active-color);
  }

  & + & {
    border-top: 1px solid var(--van-border-color);
  }
}

.message-page__body {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 4px;
  font-size: 13px;
}

.message-page__head {
  display: flex;
  justify-content: space-between;
}
</style>
