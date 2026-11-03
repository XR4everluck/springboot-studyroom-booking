<script setup lang="ts">
import AppHeader from "@/components/AppHeader/index.vue";
import { useCachedViewStoreHook } from "@/store/modules/cachedView";
import { useDarkMode } from "@/hooks/useToggleDarkMode";
import { computed, onMounted, onUnmounted } from "vue";

const cachedViews = computed(() => {
  return useCachedViewStoreHook().cachedViewList;
});

// Vant 弹层无键盘处理，桌面端用 Esc 触发最上层遮罩点击以关闭弹层
const onKeydown = (e: KeyboardEvent) => {
  if (e.key !== "Escape") return;
  const overlays = [...document.querySelectorAll<HTMLElement>(".van-overlay")].filter(
    o => getComputedStyle(o).display !== "none"
  );
  overlays[overlays.length - 1]?.click();
};
onMounted(() => window.addEventListener("keydown", onKeydown));
onUnmounted(() => window.removeEventListener("keydown", onKeydown));
</script>

<template>
  <div class="app-wrapper">
    <van-config-provider :theme="useDarkMode() ? 'dark' : 'light'">
      <app-header />
      <main class="app-main">
        <router-view v-slot="{ Component }">
          <keep-alive :include="cachedViews">
            <component :is="Component" />
          </keep-alive>
        </router-view>
      </main>
    </van-config-provider>
  </div>
</template>

<style lang="less" scoped>
.app-wrapper {
  position: relative;
  width: 100%;
  min-height: 100%;
}

.app-main {
  box-sizing: border-box;
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding-bottom: 40px;
}
</style>
