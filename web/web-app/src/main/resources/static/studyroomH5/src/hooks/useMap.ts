// 高德地图hooks
import AMapLoader from "@amap/amap-jsapi-loader";
import {
  AMAP_MAP_KEY,
  AMAP_MAP_SECRET_KEY,
  AMAP_MAP_SERVICE_HOST
} from "@/config/config";
import { onUnmounted, ref } from "vue";

export const useMap = ({ domId }: { domId?: string } = {}) => {
  const AMap = ref();
  const map = ref();
  async function initMap() {
    try {
      (window as any)._AMapSecurityConfig = {
        securityJsCode: AMAP_MAP_SECRET_KEY
        // serviceHost: AMAP_MAP_SERVICE_HOST
      };
      AMap.value = await AMapLoader.load({
        key: AMAP_MAP_KEY, // 申请好的Web端开发者Key，首次调用 load 时必填
        version: "2.0", // 指定要加载的 JS API 的版本，缺省时默认为 1.4.15
        plugins: [] // 需要使用的的插件列表，如比例尺'AMap.Scale'等
      });
      map.value = domId && new AMap.value.Map(domId);
    } catch (error) {
      // 未配置可用的高德 Key（如 localhost 被域名白名单拦截）时静默降级，
      // 页面只展示文字地址，不弹错误提示。
      console.warn("地图初始化失败，已降级为文本地址显示", error);
    }
  }
  function destroyMap() {
    map.value && map.value.destroy();
  }
  onUnmounted(() => {
    destroyMap();
  });
  return {
    map,
    AMap,
    initMap
  };
};
