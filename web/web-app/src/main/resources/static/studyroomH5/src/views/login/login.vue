<template>
  <div class="login-page">
    <div class="login-page__card">
      <div class="login-page__brand">
        <van-image round width="64px" height="64px" :src="defaultAvatarUrl" />
        <h1 class="login-page__title">上岸自习室</h1>
        <p class="--van-gray-6 login-page__subtitle">账号密码登录，没有账号可免费注册</p>
      </div>

      <van-tabs v-model:active="activeTab" shrink class="login-page__tabs">
        <!--          登录-->
        <van-tab title="登录">
          <van-form ref="loginFormRef" class="login-page__form" @submit="onSubmitLogin">
            <van-cell-group>
              <van-field
                v-model.trim="loginForm.username"
                name="username"
                label="账号"
                clearable
                autocomplete="username"
                placeholder="请输入登录账号"
                :rules="[
                  { required: true, message: '请输入登录账号' },
                  { pattern: USERNAME_PATTERN, message: USERNAME_RULE_MESSAGE }
                ]"
              />
              <van-field
                v-model="loginForm.password"
                ref="passwordFieldRef"
                name="password"
                label="密码"
                clearable
                autocomplete="current-password"
                :type="showPassword ? 'text' : 'password'"
                placeholder="请输入密码"
                :rules="[{ required: true, message: '请输入密码' }]"
              >
                <template #right-icon>
                  <van-icon
                    :name="showPassword ? 'eye-o' : 'closed-eye'"
                    class="login-page__eye"
                    @click="showPassword = !showPassword"
                  />
                </template>
              </van-field>
            </van-cell-group>

            <div class="login-page__options">
              <van-checkbox v-model="rememberAccount" shape="square" icon-size="16px">
                记住账号
              </van-checkbox>
              <span class="login-page__forgot" @click="onForgotPassword">忘记密码？</span>
            </div>

            <div class="login-page__demo" @click="fillDemoAccount">
              <span class="login-page__demo-label">演示账号</span>
              <van-tag plain type="primary" size="medium">demo001</van-tag>
              <van-tag plain type="success" size="medium">123456</van-tag>
              <span class="login-page__demo-fill">一键填入</span>
            </div>

            <div class="login-page__entry">
              管理员？
              <span class="login-page__entry-link" @click="goAdminPage">前往管理后台 →</span>
            </div>

            <div class="login-page__submit">
              <loading-button
                round
                block
                type="primary"
                native-type="submit"
                :loadingClick="onSubmitLogin"
              >
                登录
              </loading-button>
            </div>
          </van-form>
        </van-tab>

        <!--          注册-->
        <van-tab title="注册">
          <van-form ref="registerFormRef" class="login-page__form" @submit="onSubmitRegister">
            <van-cell-group>
              <van-field
                v-model.trim="registerForm.username"
                name="username"
                label="账号"
                clearable
                autocomplete="username"
                placeholder="4-20位字母、数字或下划线"
                :rules="[
                  { required: true, message: '请输入登录账号' },
                  { pattern: USERNAME_PATTERN, message: USERNAME_RULE_MESSAGE }
                ]"
              />
              <van-field
                v-model.trim="registerForm.nickname"
                name="nickname"
                label="昵称"
                clearable
                maxlength="12"
                placeholder="选填，不填默认使用登录账号"
                :rules="[{ validator: validateNickname, message: '昵称最长12个字符' }]"
              />
              <van-field
                v-model="registerForm.password"
                name="password"
                label="密码"
                clearable
                autocomplete="new-password"
                :type="showRegPassword ? 'text' : 'password'"
                placeholder="8-20位，需包含字母和数字"
                :rules="[
                  { required: true, message: '请输入密码' },
                  { pattern: PASSWORD_PATTERN, message: PASSWORD_RULE_MESSAGE }
                ]"
              >
                <template #right-icon>
                  <van-icon
                    :name="showRegPassword ? 'eye-o' : 'closed-eye'"
                    class="login-page__eye"
                    @click="showRegPassword = !showRegPassword"
                  />
                </template>
              </van-field>
            </van-cell-group>

            <div class="login-page__hint --van-gray-6">
              密码将加盐加密后存储，请勿使用其他网站的密码
            </div>

            <div class="login-page__submit">
              <loading-button
                round
                block
                type="primary"
                native-type="submit"
                :loadingClick="onSubmitRegister"
              >
                注册并登录
              </loading-button>
            </div>
          </van-form>
        </van-tab>
      </van-tabs>
    </div>
  </div>
</template>
<script setup lang="ts">
import defaultAvatarUrl from "../../../public/logo.svg";
import { onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { login, register } from "@/api/user";
import type { FieldInstance, FormInstance } from "vant";
import { showDialog, showToast } from "vant";
import { useUserStore } from "@/store/modules/user";
import LoadingButton from "@/components/LoadingButton/LoadingButton.vue";
import {
  USERNAME_PATTERN,
  PASSWORD_PATTERN,
  USERNAME_RULE_MESSAGE,
  PASSWORD_RULE_MESSAGE
} from "@/utils/validate";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const REMEMBER_KEY = "studyroom:remember-account";

// 当前激活的 Tab：0 登录，1 注册
const activeTab = ref(0);

// 登录表单
const loginFormRef = ref<FormInstance>();
const passwordFieldRef = ref<FieldInstance>();
const loginForm = reactive({
  username: "",
  password: ""
});
const showPassword = ref(false);
// 记住账号
const rememberAccount = ref(false);

// 注册表单
const registerFormRef = ref<FormInstance>();
const registerForm = reactive({
  username: "",
  nickname: "",
  password: ""
});
const showRegPassword = ref(false);

// 昵称选填，最长12个字符（与后端校验规则一致）
const validateNickname = (val: string) => !val || val.length <= 12;

// 演示账号一键填入（对外演示用）
const fillDemoAccount = () => {
  loginForm.username = "demo001";
  loginForm.password = "123456";
  rememberAccount.value = false;
  showToast({ type: "success", message: "已填入演示账号" });
};

// 管理员入口：跳转到统一应用的管理端
const goAdminPage = () => {
  window.location.href = "/console/";
};

// 忘记密码指引：系统无邮件/短信通道，改为管理员核验后重置，避免不安全的弱校验找回
const onForgotPassword = () => {
  showDialog({
    title: "忘记密码",
    message:
      "为保障账号安全，暂不支持自助找回。\n请联系管理员，凭「登录账号」核验身份后重置密码。",
    confirmButtonText: "我知道了"
  });
};

// 登录
const onSubmitLogin = async () => {
  // 验证字段
  await loginFormRef.value?.validate();
  try {
    // 登录
    await userStore.LoginAction({
      username: loginForm.username,
      password: loginForm.password
    });
  } catch (error) {
    // 失败后清空密码、保留账号并聚焦，避免明文残留；提示由请求拦截器统一弹出
    loginForm.password = "";
    passwordFieldRef.value?.focus();
    throw error;
  }
  // 记住账号
  if (rememberAccount.value) {
    localStorage.setItem(REMEMBER_KEY, loginForm.username);
  } else {
    localStorage.removeItem(REMEMBER_KEY);
  }
  await redirectAfterAuth();
};

// 注册成功后自动登录
const onSubmitRegister = async () => {
  await registerFormRef.value?.validate();
  await register({
    username: registerForm.username,
    password: registerForm.password,
    // 昵称选填：不传时后端默认使用登录账号
    nickname: registerForm.nickname || undefined
  });
  showToast({ type: "success", message: "注册成功" });
  // 注册即登录，省去重复输入
  await userStore.LoginAction({
    username: registerForm.username,
    password: registerForm.password
  });
  // 清空注册表单，避免密码等敏感值残留在缓存页面中
  resetRegisterForm();
  await redirectAfterAuth();
};

// 重置注册表单及校验状态
const resetRegisterForm = () => {
  registerForm.username = "";
  registerForm.nickname = "";
  registerForm.password = "";
  registerFormRef.value?.resetValidation();
};

// 登录/注册成功后的跳转
const redirectAfterAuth = async () => {
  await router.replace(
    route.query?.redirect
      ? decodeURIComponent(route.query?.redirect as string)
      : "/"
  );
  // 管理员重置过密码的用户：登录后强制引导到修改密码页，改密前每次登录都会提示
  if (userStore.userInfo?.mustChangePwd === 1) {
    await showDialog({
      title: "请修改密码",
      message: "检测到管理员重置了你的密码，为保障账号安全，请先设置新密码。",
      confirmButtonText: "去修改密码"
    });
    await router.push("/changePassword");
  }
};

onMounted(() => {
  // 回填记住的账号
  const remembered = localStorage.getItem(REMEMBER_KEY);
  if (remembered) {
    loginForm.username = remembered;
    rememberAccount.value = true;
  }
});
</script>

<style scoped lang="less">
.login-page {
  display: flex;
  justify-content: center;
  padding: 48px 24px 0;
}

.login-page__card {
  box-sizing: border-box;
  width: 420px;
  padding: 28px 32px 32px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
  box-shadow: 0 1px 2px rgba(24, 34, 28, 0.03), 0 6px 20px rgba(24, 34, 28, 0.04);
}

.login-page__brand {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
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

.login-page__tabs {
  margin-bottom: 4px;
}

.login-page__form {
  padding-top: 12px;
}

.login-page__eye {
  font-size: 18px;
  color: var(--van-text-color-3);
  cursor: pointer;

  &:hover {
    color: var(--van-primary-color);
  }
}

.login-page__options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 16px 0;
}

.login-page__forgot {
  font-size: 12px;
  color: var(--van-primary-color);
  cursor: pointer;

  &:hover {
    opacity: 0.75;
  }
}

.login-page__hint {
  padding: 8px 16px 0;
  font-size: 12px;
}

.login-page__demo {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 12px 16px 0;
  padding: 8px 12px;
  font-size: 12px;
  color: var(--van-text-color-2);
  background: var(--van-background);
  border: 1px dashed var(--van-primary-color);
  border-radius: 8px;
  cursor: pointer;

  &:active {
    opacity: 0.7;
  }

  .login-page__demo-label {
    color: var(--van-text-color-3);
  }

  .login-page__demo-fill {
    margin-left: auto;
    color: var(--van-primary-color);
    font-weight: 600;
  }
}

.login-page__entry {
  margin-top: 12px;
  font-size: 12px;
  color: var(--van-text-color-3);
  text-align: center;
}

.login-page__entry-link {
  color: var(--van-primary-color);
  font-weight: 600;
  cursor: pointer;

  &:hover {
    opacity: 0.75;
  }
}

.login-page__submit {
  margin-top: 20px;
}

// 窄屏适配
@media (max-width: 480px) {
  .login-page__card {
    width: 100%;
    padding: 20px 16px 24px;
  }
}
</style>
