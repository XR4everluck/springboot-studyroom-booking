import { createApp } from "vue";
import { store } from "./store";
// normalize.css
import "normalize.css/normalize.css";
// 引入样式
import "./styles/index.ts";
// svg icon
import "virtual:svg-icons-register";

import App from "./App.vue";
import router from "./router";
const app = createApp(App);
app.use(store);
app.use(router);

app.mount("#app");
