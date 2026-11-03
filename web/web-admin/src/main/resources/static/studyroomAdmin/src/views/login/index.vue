<template>
  <div class="login-container">
    <div class="login-brand">
      <div class="brand-badge">
        <img src="/logo.svg" alt="logo" />
      </div>
      <div class="brand-name">上岸自习室</div>
      <div class="brand-slogan">预订座位，专注上岸</div>
      <ul class="brand-features">
        <li>门店与座位管理</li>
        <li>订单与续期审批</li>
        <li>数据看板与运营报表</li>
      </ul>
      <i class="brand-blob brand-blob--1" aria-hidden="true"></i>
      <i class="brand-blob brand-blob--2" aria-hidden="true"></i>
    </div>
    <div class="login-panel">
      <div class="login-box">
        <div class="form-header">
          <div class="title">欢迎登录</div>
          <div class="title-tips">{{ getEnvByName('VITE_APP_TITLE') }}</div>
        </div>
        <el-form
          ref="ruleFormRef"
          :model="ruleForm"
          status-icon
          :rules="rules"
          class="login-form"
          @keyup.enter="submitForm(ruleFormRef)"
        >
          <el-form-item prop="username">
            <el-input
              v-model.trim="ruleForm.username"
              :prefix-icon="User"
              autocomplete="username"
              placeholder="请输入用户名"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              v-model.trim="ruleForm.password"
              type="password"
              show-password
              :prefix-icon="Lock"
              autocomplete="current-password"
              placeholder="请输入密码"
            />
          </el-form-item>
          <el-form-item>
            <el-button
              class="login-btn"
              type="primary"
              size="large"
              :loading="loading"
              @click="submitForm(ruleFormRef)"
            >
              登录
            </el-button>
          </el-form-item>
          <div class="form-demo">演示账号：admin / 123456</div>
          <div class="form-entry">
            没有管理权限？
            <el-link type="primary" :underline="false" href="/h5/">前往用户端</el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import type { FormInstance } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { ElNotification, ElMessageBox } from 'element-plus'
import { HOME_URL } from '@/config/config'
import { timeFix } from '@/utils/index'
import { getUserInfo, login } from '@/api/user'
import { getEnvByName } from '@/utils/getEnv'
const router = useRouter()
const route = useRoute()
const ruleFormRef = ref<FormInstance>()
const userStore = useUserStore()
const ruleForm = reactive({
  username: 'admin',
  password: '123456',
})
const loading = ref(false)
const validateUsername = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('用户名不能为空'))
  } else if (value.length < 4) {
    callback(new Error('用户名长度不能小于4位'))
  } else {
    callback()
  }
}

const validatePassword = (rule: any, value: string, callback: any) => {
  if (value === '') {
    callback(new Error('密码不能为空'))
  } else if (value.length < 6) {
    callback(new Error('密码长度不能小于6位'))
  } else {
    callback()
  }
}
const rules = reactive({
  username: [{ required: true, validator: validateUsername }],
  password: [{ required: true, validator: validatePassword }],
})
const submitForm = (formEl: FormInstance | undefined) => {
  if (!formEl) return
  formEl.validate(async (valid) => {
    if (!valid) return
    try {
      loading.value = true
      const { data } = await login(ruleForm)
      userStore.setToken(data)
      router.replace((route.query.redirect as string) || HOME_URL)

      const userInfo = await getUserInfo()
      userStore.setUserInfo(userInfo.data)

      ElNotification({
        title: `hi,${timeFix()}!`,
        message: `欢迎回来`,
        type: 'success',
      })
    } catch (err: any) {
      // 311：凭证匹配的是用户端账号（user_info），引导前往用户端登录
      if (err?.code === 311) {
        ElMessageBox.confirm('该账号为用户端账号，是否前往用户端登录？', '提示', {
          confirmButtonText: '前往用户端登录',
          cancelButtonText: '取消',
          type: 'warning',
        })
          .then(() => {
            window.location.href = '/h5/'
          })
          .catch(() => {})
      }
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped lang="scss">
@import './index';

.form-entry {
  margin-top: 14px;
  font-size: 13px;
  color: #86909c;
  text-align: center;
}
</style>
