// 页签标题统一展示品牌名（上岸自习室），不再随路由显示功能名，
// 便于对外演示时浏览器标签页始终呈现品牌
export default function setPageTitle(_routerTitle?: string): void {
  window.document.title = import.meta.env.VITE_API_TITLE;
}
