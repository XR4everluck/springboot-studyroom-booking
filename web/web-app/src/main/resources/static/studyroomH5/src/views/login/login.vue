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
            </div>

            <div class="login-page__demo" @click="fillDemoAccount">
              <span class="login-page__demo-label">演示账号</span>
              <van-tag plain type="primary" size="medium">demo001</van-tag>
              <van-tag plain type="success" size="medium">123456</van-tag>
              <span class="login-page__demo-fill">一键填入</span>
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
                show-word-limit
                placeholder="1-12个字符，展示给其他用户"
                :rules="[{ required: true, message: '请输入昵称' }]"
              />
              <van-field
                v-model.trim="registerForm.phone"
                name="phone"
                label="手机号"
                type="tel"
                clearable
                maxlength="11"
                autocomplete="tel"
                placeholder="选填，作为订单联系方式"
                :rules="[
                  {
                    pattern: PHONE_PATTERN,
                    required: false,
                    message: '手机号格式不正确'
                  }
                ]"
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
              <div v-if="registerStrength.level" class="login-page__strength">
                <span class="--van-gray-6">密码强度</span>
                <span class="login-page__strength-bar">
                  <i
                    v-for="i in 3"
                    :key="i"
                    :class="[
                      registerStrength.className,
                      { 'is-on': i <= registerStrength.level }
                    ]"
                  />
                </span>
                <span :class="registerStrength.className">
                  {{ registerStrength.text }}
                </span>
              </div>
              <van-field
                v-model="registerForm.confirmPassword"
                name="confirmPassword"
                label="确认密码"
                clearable
                autocomplete="new-password"
                :type="showRegPassword ? 'text' : 'password'"
                placeholder="请再次输入密码"
                :rules="[
                  { required: true, message: '请再次输入密码' },
                  {
                    validator: value => value === registerForm.password,
                    message: '两次输入的密码不一致'
                  }
                ]"
              />
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
import defaultAvatarUrl from "../../../public/favicon.ico";
import { computed, onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { login, register } from "@/api/user";
import type { FormInstance } from "vant";
import { showToast } from "vant";
import { useUserStore } from "@/store/modules/user";
import LoadingButton from "@/components/LoadingButton/LoadingButton.vue";
import {
  PHONE_PATTERN,
  USERNAME_PATTERN,
  PASSWORD_PATTERN,
  USERNAME_RULE_MESSAGE,
  PASSWORD_RULE_MESSAGE,
  passwordStrengthMeta
} from "@/utils/validate";

const route = useRoute();
const router = useRouter();
const userStore = useUserStore();

const REMEMBER_KEY = "studyroom:remember-account";

// 当前激活的 Tab：0 登录，1 注册
const activeTab = ref(0);

// 登录表单
const loginFormRef = ref<FormInstance>();
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
  phone: "",
  password: "",
  confirmPassword: ""
});
const showRegPassword = ref(false);
// 注册密码强度提示
const registerStrength = computed(() =>
  passwordStrengthMeta(registerForm.password)
);

// 演示账号一键填入（对外演示用）
const fillDemoAccount = () => {
  loginForm.username = "demo001";
  loginForm.password = "123456";
  rememberAccount.value = false;
  showToast({ type: "success", message: "已填入演示账号" });
};

// 登录
const onSubmitLogin = async () => {
  // 验证字段
  await loginFormRef.value?.validate();
  // 登录
  await userStore.LoginAction({
    username: loginForm.username,
    password: loginForm.password
  });
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
    nickname: registerForm.nickname,
    phone: registerForm.phone || undefined
  });
  showToast({ type: "success", message: "注册成功" });
  // 注册即登录，省去重复输入
  await userStore.LoginAction({
    username: registerForm.username,
    password: registerForm.password
  });
  await redirectAfterAuth();
};

// 登录/注册成功后的跳转
const redirectAfterAuth = async () => {
  await router.replace(
    route.query?.redirect
      ? decodeURIComponent(route.query?.redirect as string)
      : "/"
  );
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

.login-page__hint {
  padding: 8px 16px 0;
  font-size: 12px;
}

.login-page__strength {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px 0;
  font-size: 12px;
}

.login-page__strength-bar {
  display: inline-flex;
  gap: 4px;

  i {
    display: inline-block;
    width: 22px;
    height: 6px;
    border-radius: 3px;
    background-color: var(--van-border-color);

    &.is-on.is-weak {
      background-color: #ee4d38;
    }

    &.is-on.is-medium {
      background-color: #ff976a;
    }

    &.is-on.is-strong {
      background-color: #07c160;
    }
  }
}

.login-page__strength .is-weak {
  color: #ee4d38;
}

.login-page__strength .is-medium {
  color: #ff976a;
}

.login-page__strength .is-strong {
  color: #07c160;
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
