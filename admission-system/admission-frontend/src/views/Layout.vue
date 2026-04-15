<template>
  <el-container class="layout-container">
    <el-aside width="200px" class="aside">
      <div class="logo">
        <el-icon size="32"><DataLine /></el-icon>
        <span>录取数据分析</span>
      </div>
      <el-menu
        :default-active="$route.path"
        router
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
      >
        <el-menu-item index="/dashboard"><el-icon><Odometer /></el-icon><span>数据概览</span></el-menu-item>
        <el-menu-item index="/query"><el-icon><Search /></el-icon><span>数据查询</span></el-menu-item>
        <el-menu-item index="/compare"><el-icon><TrendCharts /></el-icon><span>院校对比</span></el-menu-item>
        <el-menu-item index="/visualization"><el-icon><PieChart /></el-icon><span>可视化分析</span></el-menu-item>
        <el-menu-item index="/import" v-if="userStore.isAdmin"><el-icon><Upload /></el-icon><span>数据导入</span></el-menu-item>
        <el-menu-item index="/users" v-if="userStore.isAdmin"><el-icon><UserFilled /></el-icon><span>用户管理</span></el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div class="user-info-area" v-if="userStore.isLoggedIn">
          <span>考生：{{ userStore.ksxm || '' }} | 考生号：{{ userStore.ksh || '' }}</span>
          <span>分数：{{ userStore.fs || '' }} | 位次：{{ userStore.wc || '' }}</span>
          <span>选科：{{ userStore.xk || '' }}</span>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">{{ userStore.username || '游客' }}<el-icon class="el-icon--right"><arrow-down /></el-icon></span>
            <template #dropdown>
              <el-dropdown-menu><el-dropdown-item command="logout">退出登录</el-dropdown-item></el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main class="main"><router-view /></el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()

const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.clearUserInfo()
    ElMessage.success('已退出登录')
    router.push('/login')
  }
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.aside { background-color: #304156; }
.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  font-weight: bold;
  border-bottom: 1px solid #1f2d3d;
}
.logo .el-icon { margin-right: 10px; }
.header {
  background-color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0,21,41,0.08);
}
.user-info-area {
  font-size: 14px;
  color: #606266;
}
.user-info-area span {
  margin-right: 20px;
}
.user-info { cursor: pointer; color: #606266; }
.main { background-color: #f0f2f5; padding: 20px; overflow-y: auto; }
</style>
