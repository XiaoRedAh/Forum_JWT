import { createApp } from 'vue'
import App from './App.vue'
import 'element-plus/dist/index.css'
import '@/style.css'
import router from '@/router'
import 'element-plus/theme-chalk/dark/css-vars.css'
import {createPinia} from 'pinia'

const app = createApp(App)
app.use(router)
app.use(createPinia())
app.mount('#app')



