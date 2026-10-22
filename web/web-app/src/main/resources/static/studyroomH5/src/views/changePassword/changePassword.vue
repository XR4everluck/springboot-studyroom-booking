<template>
  <div class="cp-page">
    <div class="cp-page__card">
      <div class="cp-page__head">
        <h1 class="cp-page__title">修改密码</h1>
        <p class="--van-gray-6 cp-page__subtitle">
          修改成功后需要重新登录，请牢记新密码
        </p>
      </div>

      <van-form ref="formRef" class="cp-page__form" @submit="onSubmit">
        <van-cell-group>
          <van-field
            v-model="form.oldPassword"
            name="oldPassword"
            label="原密码"
            clearable
            autocomplete="current-password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请输入当前密码"
            :rules="[{ required: true, message: '请输入原密码' }]"
          />
          <van-field
            v-model="form.newPassword"
            name="newPassword"
            label="新密码"
            clearable
            autocomplete="new-password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="8-20位，需包含字母和数字"
            :rules="[
              { required: true, message: '请输入新密码' },
              { pattern: PASSWORD_PATTERN, message: PASSWORD_RULE_MESSAGE },
              {
                validator: value => value !== form.oldPassword,
                message: '新密码不能与原密码相同'
              }
            ]"
          />
          <van-field
            v-model="form.confirmPassword"
            name="confirmPassword"
            label="确认新密码"
            clearable
            autocomplete="new-password"
            :type="showPassword ? 'text' : 'password'"
            placeholder="请再次输入新密码"
            :rules="[
              { required: true, message: '请再次输入新密码' },
              {
                validator: value => value === form.newPassword,
                message: '两次输入的密码不一致'
              }
            ]"
          >
            <template #right-icon>
              <van-icon
                :name="showPassword ? 'eye-o' : 'closed-eye'"
                class="cp-page__eye"
                @click="showPassword = !showPassword"
              />
            </template>
          </van-field>
        </van-cell-group>

        <div v-if="strength.level" class="cp-page__strength">
          <span class="--van-gray-6">密码强度</span>
          <span class="cp-page__strength-bar">
            <i
              v-for="i in 3"
              :key="i"
              :class="[strength.className, { 'is-on': i <= strength.level }]"
            />
          </span>
          <span :class="strength.className">{{ strength.text }}</span>
        </div>

        <div class="cp-page__hint --van-gray-6">
          密码将加盐加密后存储，请勿与其他网站共用同一密码
        </div>

        <div class="cp-page__submit">
          <loading-button
            round
            block
            type="primary"
            native-type="submit"
            :loadingClick="onSubmit"
          >
            确认修改
          </loading-button>
        </div>
      </van-form>
    </div>
  </div>
</template>

<script setup lang="ts" name="ChangePassword">
import { computed, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { showToast } from "vant";
import type { FormInstance } from "vant";
import { changePassword } from "@/api/user";
import { useUserStore } from "@/store/modules/user";
import LoadingButton from "@/components/LoadingButton/LoadingButton.vue";
import {
  PASSWORD_PATTERN,
  PASSWORD_RULE_MESSAGE,
  passwordStrengthMeta
} from "@/utils/validate";

const router = useRouter();
const userStore = useUserStore();

const formRef = ref<FormInstance>();
const form = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: ""
});
const showPassword = ref(false);

// 新密码强度提示
const strength = computed(() => passwordStrengthMeta(form.newPassword));

const onSubmit = async () => {
  await formRef.value?.validate();
  await changePassword({
    oldPassword: form.oldPassword,
    newPassword: form.newPassword,
    confirmPassword: form.confirmPassword
  });
  showToast({ type: "success", message: "密码修改成功，请重新登录" });
  // 密码已变更，退出登录并回到登录页，避免旧凭证继续使用
  userStore.Logout();
  await router.replace("/login");
};
</script>

<style scoped lang="less">
.cp-page {
  display: flex;
  justify-content: center;
  padding: 48px 24px 0;
}

.cp-page__card {
  box-sizing: border-box;
  width: 420px;
  padding: 28px 32px 32px;
  border-radius: 12px;
  background-color: var(--van-background-2);
  border: 1px solid var(--van-border-color);
  box-shadow: 0 1px 2px rgba(24, 34, 28, 0.03), 0 6px 20px rgba(24, 34, 28, 0.04);
}

.cp-page__head {
  text-align: center;
  margin-bottom: 12px;
}

.cp-page__title {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: var(--van-text-color);
}

.cp-page__subtitle {
  margin: 6px 0 0;
  font-size: 13px;
}

.cp-page__form {
  padding-top: 12px;
}

.cp-page__eye {
  font-size: 18px;
  color: var(--van-text-color-3);
  cursor: pointer;

  &:hover {
    color: var(--van-primary-color);
  }
}

.cp-page__strength {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px 0;
  font-size: 12px;
}

.cp-page__strength-bar {
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

.cp-page__strength .is-weak {
  color: #ee4d38;
}

.cp-page__strength .is-medium {
  color: #ff976a;
}

.cp-page__strength .is-strong {
  color: #07c160;
}

.cp-page__hint {
  padding: 10px 16px 0;
  font-size: 12px;
}

.cp-page__submit {
  margin-top: 20px;
}

@media (max-width: 480px) {
  .cp-page__card {
    width: 100%;
    padding: 20px 16px 24px;
  }
}
</style>
