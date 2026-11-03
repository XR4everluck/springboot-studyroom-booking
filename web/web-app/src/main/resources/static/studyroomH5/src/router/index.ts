import {
  createRouter,
  createWebHashHistory,
  type RouteLocationNormalized
} from "vue-router";
import routes from "./routes";
import { useCachedViewStoreHook } from "@/store/modules/cachedView";
import { getToken } from "@/utils/token";
import NProgress from "@/utils/progress";
import setPageTitle from "@/utils/set-page-title";

const router = createRouter({
  history: createWebHashHistory(),
  routes
});

export interface toRouteType extends RouteLocationNormalized {
  meta: {
    title?: string;
    noCache?: boolean;
  };
}

router.beforeEach((to: toRouteType, from, next) => {
  NProgress.start();
  // 已登录访问登录页直接回首页，避免重复登录（token 失效时拦截器会先清除 token 再跳回登录页，不会死循环）
  if (to.name === "Login" && getToken()) {
    next({ path: "/" });
    return;
  }
  // 解决路由缓存导致的 keep-alive 组件不刷新的问题
  if (to.name === "Login") {
    useCachedViewStoreHook().delAllCachedViews();
  }
  // 路由缓存
  useCachedViewStoreHook().addCachedView(to);
  // 页面 title
  setPageTitle(to.meta.title);
  next();
});

router.afterEach(() => {
  NProgress.done();
});

export default router;
