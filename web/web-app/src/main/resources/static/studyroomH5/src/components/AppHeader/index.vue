<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import SvgIcon from "@/components/SvgIcon/index.vue";
import appRoutes from "@/router/appRoutes";
import { useDarkMode, useToggleDarkMode } from "@/hooks/useToggleDarkMode";
import { useUserStore } from "@/store/modules/user";
import defaultAvatarUrl from "@/assets/avatar-shangan.svg";

const route = useRoute();
const userStore = useUserStore();

const navItems = computed(() =>
  appRoutes
    .filter(item => item.meta?.inNav)
    .map(item => ({ path: item.path, title: item.meta?.title as string }))
);

const isLogged = computed(() => Boolean(userStore.token));
const nickname = computed(() => userStore.userInfo?.nickname || "我的账户");
const avatarUrl = computed(
  () => userStore.userInfo?.avatarUrl || defaultAvatarUrl
);

const isActive = (path: string) => route.path === path;
</script>

<template>
  <header class="app-header">
    <div class="app-header__inner">
      <router-link to="/search" class="app-header__brand">
        <span class="app-header__logo">上</span>
        <span class="app-header__title">上岸自习室</span>
      </router-link>

      <nav class="app-header__nav">
        <router-link
          v-for="item in navItems"
          :key="item.path"
          :to="item.path"
          class="app-header__nav-link"
          :class="{ 'is-active': isActive(item.path) }"
        >
          {{ item.title }}
        </router-link>
      </nav>

      <div class="app-header__actions">
        <button
          type="button"
          class="app-header__icon-btn"
          :title="useDarkMode() ? '切换到浅色模式' : '切换到深色模式'"
          @click="useToggleDarkMode()"
        >
          <svg-icon :name="useDarkMode() ? 'light' : 'dark'" :size="18" />
        </button>

        <router-link v-if="isLogged" to="/userCenter" class="app-header__user">
          <van-image round width="28px" height="28px" :src="avatarUrl" />
          <span class="app-header__nickname">{{ nickname }}</span>
        </router-link>
        <router-link v-else to="/login" class="app-header__login">登录</router-link>
      </div>
    </div>
  </header>
</template>

<style lang="less" scoped>
.app-header {
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: var(--van-background-2);
  border-bottom: 1px solid var(--van-border-color);
  box-shadow: 0 2px 10px rgba(24, 34, 28, 0.04);
}

.app-header__inner {
  box-sizing: border-box;
  display: flex;
  align-items: center;
  gap: 20px;
  height: 60px;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.app-header__brand {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.app-header__logo {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border-radius: 8px;
  background-color: var(--van-primary-color);
  color: var(--sr-text-on-primary);
  font-size: 16px;
  font-weight: 600;
}

.app-header__title {
  font-size: 17px;
  font-weight: 600;
  color: var(--van-text-color);
  white-space: nowrap;
}

.app-header__nav {
  display: flex;
  align-items: center;
  gap: 2px;
  flex: 1;
  min-width: 0;
  justify-content: center;
}

.app-header__nav-link {
  padding: 0 12px;
  height: 60px;
  display: flex;
  align-items: center;
  font-size: 14px;
  white-space: nowrap;
  color: var(--van-text-color);
  border-bottom: 2px solid transparent;
  transition: color 0.2s, border-color 0.2s;

  &:hover {
    color: var(--van-primary-color);
  }

  &:focus-visible {
    outline: 2px solid var(--van-primary-color);
    outline-offset: -2px;
  }

  &.is-active {
    color: var(--van-primary-color);
    font-weight: 600;
    border-bottom-color: var(--van-primary-color);
  }
}

.app-header__actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.app-header__icon-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  padding: 0;
  border: none;
  border-radius: 6px;
  background: transparent;
  color: var(--van-text-color);
  cursor: pointer;

  &:hover {
    background-color: var(--van-active-color);
  }

  &:focus-visible {
    outline: 2px solid var(--van-primary-color);
  }
}

.app-header__user {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px;
  border-radius: 16px;
  color: var(--van-text-color);

  &:hover {
    background-color: var(--van-active-color);
  }
}

.app-header__nickname {
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 14px;
}

.app-header__login {
  padding: 6px 18px;
  border-radius: 16px;
  font-size: 14px;
  color: var(--sr-text-on-primary);
  background-color: var(--van-primary-color);

  &:hover {
    background-color: var(--sr-primary-hover);
  }
}

@media (max-width: 1200px) {
  .app-header__nav-link {
    padding: 0 8px;
    font-size: 13px;
  }

  .app-header__title {
    display: none;
  }
}

// 窄屏：导航横向滑动，收缩次级元素，保证触控区域
@media (max-width: 768px) {
  .app-header__inner {
    gap: 10px;
    padding: 0 12px;
  }

  .app-header__nav {
    overflow-x: auto;
    justify-content: flex-start;
    -webkit-overflow-scrolling: touch;
    scrollbar-width: none;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .app-header__nav-link {
    flex-shrink: 0;
    height: 56px;
    padding: 0 10px;
    font-size: 13px;
  }

  .app-header__actions {
    gap: 6px;
  }

  .app-header__user {
    padding: 4px;

    .app-header__nickname {
      display: none;
    }
  }
}
</style>
