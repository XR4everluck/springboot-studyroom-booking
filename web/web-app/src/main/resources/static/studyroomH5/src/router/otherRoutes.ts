import type { RouteRecordRaw } from "vue-router";
/**
 * @description 📚 路由参数配置简介
 * @param path ==> 菜单路径
 * @param name ==> 菜单别名
 * @param redirect ==> 重定向地址
 * @param component ==> 视图文件路径
 * @param meta ==> 菜单信息
 * @param meta.icon ==> 菜单图标，只有tabBar菜单才需要配置
 * @param meta.title ==> 菜单标题
 * @param meta.noCache ==> 是否不缓存，默认开启组件缓存，需要vue组件中配置name属性和路由菜单别名一致
 * @param meta.isShowNavBar ==> 是否展示顶部导航栏，默认不展示
 * */

const routes: Array<RouteRecordRaw> = [
  {
    name: "SeatDetail",
    path: "/seatDetail",
    component: () => import("@/views/seatDetail/seatDetail.vue"),
    meta: {
      title: "座位详情",
      noCache: true
    }
  },
  {
    name: "StoreDetail",
    path: "/storeDetail",
    component: () => import("@/views/storeDetail/storeDetail.vue"),
    meta: {
      title: "自习室门店详情",
      noCache: true
    }
  },
  {
    name: "Visit",
    path: "/visit",
    component: () => import("@/views/appointment/appointment.vue"),
    meta: {
      title: "到店体验",
      noCache: true
    }
  },
  {
    name: "MyVisit",
    path: "/myVisit",
    component: () => import("@/views/myAppointment/myAppointment.vue"),
    meta: {
      title: "我的预约",
      noCache: true
    }
  },
  {
    name: "MyOrder",
    path: "/myOrder",
    component: () => import("@/views/myOrder/myOrder.vue"),
    meta: {
      title: "我的订单",
      noCache: true
    }
  },
  {
    name: "Order",
    path: "/order",
    component: () => import("@/views/order/order.vue"),
    meta: {
      title: "订单详情",
      noCache: true
    }
  },
  {
    name: "BrowsingHistory",
    path: "/browsingHistory",
    component: () => import("@/views/browsingHistory/browsingHistory.vue"),
    meta: {
      title: "浏览历史",
      noCache: true
    }
  },
  {
    name: "Login",
    path: "/login",
    component: () => import("@/views/login/login.vue"),
    meta: {
      title: "登录",
      noCache: true
    }
  }
];

export default routes;
