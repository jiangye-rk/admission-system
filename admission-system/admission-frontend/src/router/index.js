import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/store/user'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { public: true }
  },
  {
    path: '/',
    name: 'Layout',
    component: () => import('@/views/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/Dashboard.vue'),
        meta: { title: '数据概览' }
      },
      {
        path: 'query',
        name: 'Query',
        component: () => import('@/views/Query.vue'),
        meta: { title: '数据查询' }
      },
      {
        path: 'compare',
        name: 'Compare',
        component: () => import('@/views/Compare.vue'),
        meta: { title: '院校对比' }
      },
      {
        path: 'visualization',
        name: 'Visualization',
        component: () => import('@/views/Visualization.vue'),
        meta: { title: '可视化分析' }
      },
      {
        path: 'import',
        name: 'Import',
        component: () => import('@/views/Import.vue'),
        meta: { title: '数据导入', admin: true }
      },
      {
        path: 'users',
        name: 'UserManage',
        component: () => import('@/views/UserManage.vue'),
        meta: { title: '用户管理', admin: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const userStore = useUserStore()
  const token = userStore.token

  if (!to.meta.public && !token) {
    next('/login')
  } else if (to.meta.admin && userStore.role !== 'admin') {
    next('/')
  } else {
    next()
  }
})

export default router