/**
 * @description 📚 路由参数配置简介
 * @param path ==> 菜单路径
 * @param name ==> 菜单别名
 * @param redirect ==> 重定向地址
 * @param component ==> 视图文件路径
 * @param meta ==> 菜单信息
 * @param meta.icon ==> 菜单图标
 * @param meta.title ==> 菜单标题
 * @param meta.activeMenu ==> 当前路由为详情页时，需要高亮的菜单
 * @param meta.isLink ==> 是否外链
 * @param meta.isHide ==> 是否隐藏
 * @param meta.isFull ==> 是否全屏(示例：数据大屏页面)
 * @param meta.isAffix ==> 是否固定在 tabs nav
 * @param meta.isKeepAlive ==> 是否缓存
 * */

import { RouteRecordRaw } from 'vue-router'
import { HOME_URL, LOGIN_URL } from '@/config/config'

const LAYOUT = () => import('@/layouts/index.vue')

/**
 * @description 静态路由
 */
export const staticRoutes: RouteRecordRaw[] = [
  {
    path: LOGIN_URL,
    name: 'login',
    meta: {
      isHide: true,
    },
    component: () => import('@/views/login/index.vue'),
  },
  {
    path: '/404',
    name: '404',
    meta: {
      isHide: true,
    },
    component: () => import('@/views/error/error-404.vue'),
  },
  {
    path: '/',
    name: 'LAYOUT',
    component: LAYOUT,
    redirect: HOME_URL,
    meta: {
      title: '首页',
      icon: 'HomeFilled',
      isHide: false,
    },
    children: [
      {
        path: '/index',
        name: 'Index',
        component: () => import('@/views/home/index.vue'),
        meta: {
          title: '首页',
          icon: 'HomeFilled',
          affix: true,
        },
      },
      {
        path: '/system/user',
        component: () => import('@/views/system/user/user.vue'),
        meta: {
          title: '用户管理',
          icon: 'UserFilled',
          isHide: false,
          activeMenu: null,
        },
        children: [],
      },
      {
        path: '/system/post',
        component: () => import('@/views/system/post/post.vue'),
        meta: {
          title: '岗位管理',
          icon: 'Postcard',
          isHide: false,
          activeMenu: null,
        },
        children: [],
      },
      {
        path: '/storeManagement/store/store',
        component: () =>
          import(
            '@/views/storeManagement/storeManagement/storeManagement.vue'
          ),
        meta: {
          title: '门店管理',
          icon: 'OfficeBuilding',
          isHide: false,
          activeMenu: '',
        },
        children: [],
      },
      {
        path: '/storeManagement/store/addOrEditStore',
        component: () =>
          import(
            '@/views/storeManagement/storeManagement/components/addOrEditStore.vue'
          ),
        meta: {
          title: '门店详情',
          icon: 'Document',
          isHide: true,
          activeMenu:
            '/storeManagement/store/store',
        },
        children: [],
      },
      {
        path: '/seatManagement/seat/seat',
        component: () =>
          import(
            '@/views/storeManagement/seatManagement/seatManagement.vue'
          ),
        meta: {
          title: '座位管理',
          icon: 'House',
          isHide: false,
          activeMenu: null,
        },
        children: [],
      },
      {
        path: '/seatManagement/seat/addOrEditSeat',
        component: () =>
          import(
            '@/views/storeManagement/seatManagement/components/addOrEditSeat.vue'
          ),
        meta: {
          title: '座位详情',
          icon: 'Document',
          isHide: true,
          activeMenu: '/seatManagement/seat/seat',
        },
        children: [],
      },
      {
        path: '/attributeManagement/attribute/attribute',
        component: () =>
          import(
            '@/views/storeManagement/attributeManagement/attributeManagement.vue'
          ),
        meta: {
          title: '属性管理',
          icon: 'List',
          isHide: false,
          activeMenu: null,
        },
        children: [],
      },
      {
        path: '/visitManagement/visit/visit',
        component: () =>
          import('@/views/orderManagement/appointment/appointment.vue'),
        meta: {
          title: '体验预约管理',
          icon: 'Clock',
          isHide: false,
          activeMenu: null,
        },
        children: [],
      },
      {
        path: '/orderManagement/order/order',
        component: () =>
          import('@/views/orderManagement/order/order.vue'),
        meta: {
          title: '订单管理',
          icon: 'CreditCard',
          isHide: false,
          activeMenu: null,
        },
        children: [],
      },
      {
        path: '/orderManagement/order/addOrEditOrder',
        component: () =>
          import(
            '@/views/orderManagement/order/components/addOrEditOrder.vue'
          ),
        meta: {
          title: '订单详情',
          icon: null,
          isHide: true,
          activeMenu: '/orderManagement/order/order',
        },
        children: [],
      },
      {
        path: '/userManagement/userManagement',
        component: () => import('@/views/userManagement/userManagement.vue'),
        meta: {
          title: '用户管理',
          icon: 'UserFilled',
          isHide: false,
          activeMenu: null,
        },
        children: [],
      },
    ],
  },
  {
    path: '/:catchAll(.*)',
    meta: {
      isHide: true,
    },
    component: () => import('@/views/error/error-404.vue'), //这个是我自己的路径
  },
]
