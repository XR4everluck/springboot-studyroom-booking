<template>
  <div class="login-page">
    <div class="login-page__card">
      <div class="login-page__brand">
        <van-image round width="72px" height="72px" :src="defaultAvatarUrl" />
        <h1 class="login-page__title">共享自习室</h1>
        <p class="--van-gray-6 login-page__subtitle">手机号验证码登录</p>
      </div>
      <van-form ref="formRef" class="login-page__form">
        <van-cell-group>
          <!--            手机号-->
          <van-field
            v-model.trim="loginInfo.phone"
            border
            name="phone"
            required
            type="tel"
            autocomplete="off"
            placeholder="请输入手机号"
            :rules="[
              {
                required: true,
                pattern: /^1([3589]\d|4[5-9]|6[1-2,4-7]|7[0-8])\d{8}$/,
                message: '请正确填写手机号'
              }
            ]"
          />

          <van-field
            v-model.trim="loginInfo.code"
            name="code"
            placeholder="请输入验证码"
            clearable
            type="digit"
            maxlength="6"
            autocomplete="off"
            :rules="[
              {
                required: true,
                pattern: /^\d{6}$/,
                message: '请填写验证码'
              }
            ]"
          >
            <template #button>
              <van-button @click="getCodeHandle" size="small" type="primary">
                <div class="flex justify-center items-center">
                  <span class="--van-gray-1">{{
                    codeSendStatus ? "已发送" : "发送验证码"
                  }}</span>
                  <van-count-down
                    v-show="codeSendStatus"
                    ref="countDownRef"
                    @finish="countDownFinishHandle"
                    :time="countDown"
                    :auto-start="false"
                    format="ss"
                  >
                    <template #default="{ seconds }">
                      <span class="--van-gray-1">{{ `(${seconds}s)` }}</span>
                    </template>
                  </van-count-down>
                </div>
              </van-button>
            </template>
          </van-field>
        </van-cell-group>
        <div class="login-page__submit">
          <loading-button
            round
            block
            type="primary"
            native-type="submit"
            :loadingClick="onSubmitHandle"
          >
            登录
          </loading-button>
        </div>
      </van-form>
    </div>
  </div>
</template>
<script setup lang="ts">
import defaultAvatarUrl from "../../../public/favicon.ico";
import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { getSmsCode } from "@/api/user";
import type { CountDownInstance, FormInstance } from "vant";
import { useUserStore } from "@/store/modules/user";
import LoadingButton from "@/components/LoadingButton/LoadingButton.vue";
const route = useRoute();
const router = useRouter();
const userStore = useUserStore();
// 登录信息
const loginInfo = ref({
  phone: "13888888888",
  code: "123456"
});
// 表单实例
const formRef = ref<FormInstance>();
// 验证码发送状态
const codeSendStatus = ref(false);
//#region <倒计时相关>
// 倒计时
const countDown = ref(60 * 1000);
const countDownRef = ref<CountDownInstance>();
// 倒计时开始
const countDownStartHandle = () => {
  countDownRef.value?.start();
  //   修改验证码发送状态
  codeSendStatus.value = true;
};
// 倒计时重置
const countDownResetHandle = () => {
  countDownRef.value?.reset();
  //   修改验证码发送状态
  codeSendStatus.value = false;
};
// 倒计时结束
const countDownFinishHandle = () => {
  countDownResetHandle();
};
//#endregion
// 获取验证码
const getCodeHandle = async () => {
  // 验证字段phone
  await formRef.value?.validate("phone");
  // 开始倒计时
  countDownStartHandle();
  getSmsCode({ phone: loginInfo.value.phone });
};
const onSubmitHandle = async () => {
  // 验证字段
  await formRef.value?.validate();
  // 登录
  await userStore.LoginAction(loginInfo.value);
  // 跳转首页
  await router.replace(
    route.query?.redirect
      ? decodeURIComponent(route.query?.redirect as string)
      : "/"
  );
};
onMounted(() => {
  console.log("route", route);
});
</script>

<style scoped lang="less">
.login-page {
  display: flex;
  justify-content: center;
  padding: 48px 20px 0;
}

.login-page__card {
  box-sizing: border-box;
  width: 420px;
  padding: 32px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.06);
}

.login-page__brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-bottom: 24px;
}

.login-page__title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--van-text-color);
}

.login-page__subtitle {
  margin: 0;
  font-size: 13px;
}

.login-page__submit {
  margin-top: 24px;
}
</style>
