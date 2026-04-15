<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="title">天津高校录取数据分析系统</h2>
      <el-tabs v-model="activeTab" stretch>
        <el-tab-pane label="登录" name="login">
          <el-form :model="loginForm" :rules="loginRules" ref="loginRef">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="用户名" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="loginForm.password" type="password" placeholder="密码" :prefix-icon="Lock" @keyup.enter="handleLogin" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleLogin" :loading="loading" style="width: 100%">登录</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="注册" name="register">
          <el-form :model="registerForm" :rules="registerRules" ref="registerRef">
            <el-form-item prop="username">
              <el-input v-model="registerForm.username" placeholder="用户名" :prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input v-model="registerForm.password" type="password" placeholder="密码" :prefix-icon="Lock" />
            </el-form-item>
            <el-form-item prop="ksxm">
              <el-input v-model="registerForm.ksxm" placeholder="考生姓名" />
            </el-form-item>
            <el-form-item prop="ksh">
              <el-input v-model="registerForm.ksh" placeholder="14位考生号" maxlength="14" />
            </el-form-item>
            <el-form-item prop="sjh">
              <el-input v-model="registerForm.sjh" placeholder="手机号" maxlength="11" />
            </el-form-item>
            <el-form-item prop="fs">
              <el-input-number v-model="registerForm.fs" :min="0" :max="750" placeholder="高考分数" controls-position="right" style="width: 100%;" />
            </el-form-item>
            <el-form-item label="选考科目" prop="xk">
              <el-checkbox-group v-model="registerForm.xk" :min="3" :max="3">
                <el-checkbox label="物理">物理</el-checkbox>
                <el-checkbox label="化学">化学</el-checkbox>
                <el-checkbox label="生物">生物</el-checkbox>
                <el-checkbox label="思想政治">思想政治</el-checkbox>
                <el-checkbox label="历史">历史</el-checkbox>
                <el-checkbox label="地理">地理</el-checkbox>
              </el-checkbox-group>
              <div class="tip" style="font-size:12px; color:#999;">请选择3门科目</div>
            </el-form-item>
            <el-form-item prop="gzxx">
              <el-input v-model="registerForm.gzxx" placeholder="高中学校" />
            </el-form-item>
            <el-form-item>
              <el-button type="success" @click="handleRegister" :loading="loading" style="width: 100%">注册</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
      </el-tabs>
      <div class="guest-tip">
        <el-link type="info" @click="guestLogin">游客访问（仅查询功能）</el-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { userApi } from '@/api/api'
import { useUserStore } from '@/store/user'

const router = useRouter()
const userStore = useUserStore()
const activeTab = ref('login')
const loading = ref(false)
const loginRef = ref()
const registerRef = ref()

const loginForm = reactive({ username: '', password: '' })
const loginRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerForm = reactive({
  username: '',
  password: '',
  ksxm: '',
  ksh: '',
  sjh: '',
  fs: undefined,
  xk: [],
  gzxx: ''
})

const registerRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  ksxm: [{ required: true, message: '请输入考生姓名', trigger: 'blur' }],
  ksh: [
    { required: true, message: '请输入14位考生号', trigger: 'blur' },
    { len: 14, message: '考生号必须为14位', trigger: 'blur' },
    { pattern: /^\d{14}$/, message: '考生号必须为数字', trigger: 'blur' }
  ],
  sjh: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  fs: [{ required: true, message: '请输入高考分数', trigger: 'blur' }],
  xk: [
    { type: 'array', required: true, min: 3, max: 3, message: '请选择3门科目', trigger: 'change' }
  ],
  gzxx: [{ required: true, message: '请输入高中学校', trigger: 'blur' }]
}

const handleLogin = async () => {
  await loginRef.value.validate()
  loading.value = true
  try {
    const res = await userApi.login(loginForm)
    userStore.setUserInfo(res)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}

const handleRegister = async () => {
  await registerRef.value.validate()
  
  // 检查分数是否已填写
  if (registerForm.fs === undefined || registerForm.fs === null || registerForm.fs === '') {
    ElMessage.error('请输入高考分数')
    return
  }
  
  const postData = {
    username: registerForm.username,
    password: registerForm.password,
    ksxm: registerForm.ksxm,
    ksh: registerForm.ksh,
    sjh: registerForm.sjh,
    fs: registerForm.fs,
    xk: registerForm.xk.join(','),
    gzxx: registerForm.gzxx
  }
  
  loading.value = true
  try {
    await userApi.register(postData)
    ElMessage.success('注册成功，请登录')
    activeTab.value = 'login'
    // 清空注册表单
    registerForm.username = ''
    registerForm.password = ''
    registerForm.ksxm = ''
    registerForm.ksh = ''
    registerForm.sjh = ''
    registerForm.fs = undefined
    registerForm.xk = []
    registerForm.gzxx = ''
  } catch (error) {
    ElMessage.error(error.response?.data?.msg || '注册失败')
  } finally {
    loading.value = false
  }
}

const guestLogin = () => {
  ElMessage.info('游客模式：仅可查询数据，无法导出和对比')
  router.push('/')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}
.login-box {
  width: 450px;
  padding: 40px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.1);
}
.title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
}
.guest-tip {
  text-align: center;
  margin-top: 20px;
}
</style>
