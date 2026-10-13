<template>
  <div class="uc-page">
    <section class="uc-page__profile">
      <van-image
        @click="
          showImagePreview([userStore.userInfo?.avatarUrl || defaultAvatarUrl])
        "
        round
        width="96px"
        height="96px"
        :src="userStore.userInfo?.avatarUrl || defaultAvatarUrl"
        class="uc-page__avatar"
      >
        <template v-slot:error>加载失败</template>
      </van-image>
      <div class="uc-page__nickname">
        {{ userStore.userInfo?.nickname || "测试" }}
      </div>
      <div class="--van-gray-6 uc-page__hint">点击头像可查看大图</div>
    </section>

    <section class="uc-page__panel">
      <h2 class="uc-page__title">常用功能</h2>
      <div class="uc-page__grid">
        <button
          type="button"
          v-for="item in navList"
          :key="item.path"
          class="uc-page__entry"
          @click="router.push(item.path)"
        >
          <SvgIcon :name="item.icon" size="36" />
          <span>{{ item.name }}</span>
        </button>
      </div>

      <van-button
        type="primary"
        class="uc-page__logout"
        @click="logoutHandle"
        >退出登录</van-button
      >
    </section>
  </div>
</template>
<script setup lang="ts" name="UserCenter">
import { useUserStore } from "@/store/modules/user";
import { showImagePreview } from "vant";
import defaultAvatarUrl from "../../../public/favicon.ico";
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
const router = useRouter();
const navList = ref([
  {
    icon: "历史",
    name: "浏览历史",
    path: "/browsingHistory"
  },
  {
    icon: "预约",
    name: "我的预约",
    path: "/myVisit"
  },
  {
    icon: "合同",
    name: "我的订单",
    path: "/myOrder"
  }
]);
const userStore = useUserStore();
// 退出登陆
const logoutHandle = () => {
  userStore.Logout();
  // 清空路由浏览历史记录
  router.replace("/");
};
onMounted(() => {
  userStore.GetInfoAction();
});
</script>

<style scoped lang="less">
.uc-page {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 28px 24px 0;
}

.uc-page__profile {
  box-sizing: border-box;
  width: 280px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 36px 24px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
}

.uc-page__avatar {
  cursor: zoom-in;
}

.uc-page__nickname {
  font-size: 16px;
  font-weight: 600;
  color: var(--van-text-color);
}

.uc-page__hint {
  font-size: 12px;
}

.uc-page__panel {
  flex: 1;
  min-width: 0;
  box-sizing: border-box;
  padding: 24px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
}

.uc-page__title {
  margin: 0 0 16px;
  font-size: 16px;
  font-weight: 600;
  color: var(--van-text-color);
}

.uc-page__grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.uc-page__entry {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 24px 12px;
  border: 1px solid var(--van-border-color);
  border-radius: 12px;
  background: transparent;
  color: var(--van-text-color);
  font-size: 14px;
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

.uc-page__logout {
  margin-top: 24px;
  width: 200px;
}
</style>
