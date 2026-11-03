<template>
  <van-button v-bind="$attrs" :loading="loading" @click="onButtonClick"
    ><slot></slot
  ></van-button>
</template>
<script setup lang="ts">
/**
 * @description: 具有 loading 状态的提交按钮
 * 用法：放在 van-form 内，native-type="submit"，并把提交方法同时通过 loadingClick 传入
 */
import { ref } from "vue";

const props = defineProps({
  loadingClick: {
    type: Function,
    default: () => Promise.resolve()
  }
});
// 按钮 loading 状态
const loading = ref(false);

/**
 * 该按钮通常同时挂在 van-form 的 submit（native-type="submit"）与自身 click 上。
 * 若直接透传 click，一次点击会先走 click、再冒泡触发原生 submit，
 * 导致提交逻辑被调用两次（注册重复请求、登录失败次数翻倍、改密重复提交）。
 * 因此这里在 click 阶段阻止默认的表单提交，只保留 click 这一条链路；
 * 回车键触发的原生 submit 不受影响，仍可正常提交一次。
 */
async function onButtonClick(event: MouseEvent) {
  event.preventDefault();
  if (loading.value) return;
  try {
    loading.value = true;
    await props.loadingClick();
  } catch (error) {
    console.error(error);
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped lang="scss"></style>
