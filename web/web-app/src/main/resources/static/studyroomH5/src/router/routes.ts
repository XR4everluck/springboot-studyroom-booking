import Layout from "@/layout/index.vue";
import type { RouteRecordRaw } from "vue-router";
import appRoutes from "./appRoutes";

const routes: Array<RouteRecordRaw> = [
  {
    path: "/",
    name: "root",
    component: Layout,
    redirect: "/search",
    children: appRoutes
  }
];

export default routes;
