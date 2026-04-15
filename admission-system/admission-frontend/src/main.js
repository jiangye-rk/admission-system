import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

// 强制显示下拉框选中文本（解决 Element Plus 隐藏问题）
const style = document.createElement('style')
style.innerHTML = `
  .el-select .el-select__selected-item.is-hidden,
  .el-select .el-select__selected-item {
    display: block !important;
    visibility: visible !important;
    opacity: 1 !important;
    color: #333 !important;
    background: #fff !important;
    position: static !important;
    width: auto !important;
    height: auto !important;
    overflow: visible !important;
  }
  .el-select .el-input__inner {
    color: #333 !important;
  }
  .el-select .el-select__input-wrapper {
    display: flex !important;
    align-items: center !important;
  }
`
document.head.appendChild(style)

app.mount('#app')