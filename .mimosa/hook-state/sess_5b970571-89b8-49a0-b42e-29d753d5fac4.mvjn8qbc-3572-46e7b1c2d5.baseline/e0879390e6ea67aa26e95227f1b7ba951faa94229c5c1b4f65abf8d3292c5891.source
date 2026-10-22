import type { RouteRecordRaw } from "vue-router";

/**
 * @description 📚 路由参数配置简介
 * @param path ==> 菜单路径
 * @param name ==> 菜单别名（同时作为 keep-alive 缓存 key，需与组件 name 一致，勿随意改动）
 * @param component ==> 视图文件路径
 * @param meta.title ==> 页面标题，同时用于顶部导航文案
 * @param meta.noCache ==> 是否不缓存，默认开启组件缓存
 * @param meta.inNav ==> 是否在顶部导航中展示入口
 * */
const appRoutes: Array<RouteRecordRaw> = [
  {
    name: "Search",
    path: "/search",
    component: () => import("@/views/search/search.vue"),
    meta: {
      title: "找座位",
      inNav: true
    }
  },
  {
    name: "MySeat",
    path: "/mySeat",
    component: () => import("@/views/mySeat/mySeat.vue"),
    meta: {
      title: "我的座位",
      inNav: true
    }
  },
  {
    name: "MyOrder",
    path: "/myOrder",
    component: () => import("@/views/myOrder/myOrder.vue"),
    meta: {
      title: "我的订单",
      noCache: true,
      inNav: true
    }
  },
  {
    name: "Visit",
    path: "/visit",
    component: () => import("@/views/appointment/appointment.vue"),
    meta: {
      title: "到店体验",
      noCache: true,
      inNav: true
    }
  },
  {
    name: "MyVisit",
    path: "/myVisit",
    component: () => import("@/views/myAppointment/myAppointment.vue"),
    meta: {
      title: "我的预约",
      noCache: true,
      inNav: true
    }
  },
  {
    name: "BrowsingHistory",
    path: "/browsingHistory",
    component: () => import("@/views/browsingHistory/browsingHistory.vue"),
    meta: {
      title: "浏览历史",
      noCache: true,
      inNav: true
    }
  },
  {
    name: "Group",
    path: "/group",
    component: () => import("@/views/group/group.vue"),
    meta: {
      title: "圈子",
      inNav: true
    }
  },
  {
    name: "Message",
    path: "/message",
    component: () => import("@/views/message/message.vue"),
    meta: {
      title: "消息",
      inNav: true
    }
  },
  {
    name: "UserCenter",
    path: "/userCenter",
    component: () => import("@/views/userCenter/userCenter.vue"),
    meta: {
      title: "个人中心"
    }
  },
  {
    name: "ChangePassword",
    path: "/changePassword",
    component: () => import("@/views/changePassword/changePassword.vue"),
    meta: {
      title: "修改密码",
      noCache: true
    }
  },
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
    name: "Order",
    path: "/order",
    component: () => import("@/views/order/order.vue"),
    meta: {
      title: "订单详情",
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

export default appRoutes;
