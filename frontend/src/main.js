import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/css/global.css'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { websocketClient } from './utils/websocket'

const app = createApp(App)

// 全局错误处理
app.config.errorHandler = (err, vm, info) => {
    console.error('Vue错误:', err)
    console.error('错误信息:', info)
}

// 初始化WebSocket连接
// app.config.globalProperties.$ws = websocketClient
// websocketClient.connect()

app.use(router)
app.use(ElementPlus, {
    locale:zhCn,
})
app.mount('#app')

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}
