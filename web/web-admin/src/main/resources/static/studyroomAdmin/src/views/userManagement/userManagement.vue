<template>
  <el-card>
    <ProTable
      ref="proTable"
      :dataCallback="dataCallback"
      :columns="columns"
      :requestApi="getUserManagementInfoList"
      :initParam="initParam"
      :stripe="true"
    ></ProTable>
  </el-card>
</template>

<script setup lang="tsx">
import { reactive, ref } from 'vue'
import { ColumnProps } from '@/components/ProTable/src/types'
import ProTable from '@/components/ProTable/src/ProTable.vue'
import { UserStatus, UserStatusMap } from '@/enums/constEnums'
import { ElMessageBox } from 'element-plus'
import {
  getUserManagementInfoList,
  resetUserManagementPassword,
  updateUserManagementStatus,
} from '@/api/userManagement'
import { UserManagementInfoInterface } from '@/api/userManagement/types'

// *获取 ProTable 元素，调用其获取刷新数据方法
const proTable = ref<InstanceType<typeof ProTable>>()

// *表格配置项
const columns: ColumnProps[] = [
  {
    prop: 'nickname',
    label: '昵称',
  },
  {
    prop: 'avatarUrl',
    label: '头像',
    render: ({ row }: { row: UserManagementInfoInterface }) => {
      return (
        <el-image
          style={{ width: '60px', height: '60px' }}
          src={row.avatarUrl}
          fit="cover"
        ></el-image>
      )
    },
  },
  {
    prop: 'phone',
    label: '手机',
    search: { el: 'input', props: { placeholder: '请输入手机号' } },
  },
  {
    prop: 'status',
    label: '帐号状态',
    enum: UserStatusMap,
    search: {
      el: 'select',
      props: { placeholder: '请选择帐号状态' },
    },
    render: ({ row }: { row: UserManagementInfoInterface }) => {
      return (
        <el-switch
          active-value={UserStatus.NORMAL}
          inactive-value={UserStatus.DISABLED}
          v-model={row.status}
          onChange={() =>
            updateUserManagementStatus(row.id, row.status as UserStatus)
          }
        ></el-switch>
      )
    },
  },
  {
    prop: 'operation',
    label: '操作',
    fixed: 'right',
    width: 120,
    render: ({ row }: { row: UserManagementInfoInterface }) => {
      return (
        <el-button type="primary" link onClick={() => onResetPassword(row)}>
          重置密码
        </el-button>
      )
    },
  },
]

// 管理员核验用户身份后重置密码：生成临时密码并强制用户登录后修改，重置前旧登录立即失效
const onResetPassword = async (row: UserManagementInfoInterface) => {
  await ElMessageBox.confirm(
    `确认为用户「${row.nickname || row.username || row.id}」重置密码？重置后该用户的当前登录将立即失效。`,
    '重置密码',
    { confirmButtonText: '重置', cancelButtonText: '取消', type: 'warning' },
  )
  const { data } = await resetUserManagementPassword(row.id)
  await ElMessageBox.alert(`临时密码：${data}\n请当场告知用户，该用户下次登录后需先修改密码。`, '重置成功', {
    confirmButtonText: '我已告知用户',
    customStyle: { whiteSpace: 'pre-line' },
  })
  proTable.value?.getTableList()
}

// *查询参数
const initParam = reactive({})

// 处理返回的数据格式
const dataCallback = (data: any) => {
  return {
    list: data?.records,
    total: data?.total,
  }
}
</script>
