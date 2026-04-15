<template>
  <div class="user-manage-page">
    <el-card>
      <template #header>
        <span>用户管理</span>
      </template>

      <el-table :data="userList" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="ksxm" label="考生姓名" width="100" />
        <el-table-column prop="ksh" label="考生号" width="150" />
        <el-table-column prop="sjh" label="手机号" width="120" />
        <el-table-column prop="fs" label="分数" width="80" />
        <el-table-column prop="wc" label="位次" width="100" />
        <el-table-column prop="xk" label="选科" width="150" />
        <el-table-column prop="gzxx" label="高中学校" min-width="150" />
        <el-table-column prop="role" label="角色" width="80">
          <template #default="{ row }">
            <el-tag :type="row.role === 'admin' ? 'danger' : 'info'">
              {{ row.role === 'admin' ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button 
              v-if="row.role !== 'admin'" 
              type="primary" 
              size="small" 
              @click="handleResetPassword(row)"
            >
              重置密码
            </el-button>
            <el-button 
              v-if="row.role !== 'admin'" 
              :type="row.status === 1 ? 'danger' : 'success'" 
              size="small" 
              @click="handleToggleStatus(row)"
            >
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 重置密码对话框 -->
    <el-dialog v-model="resetDialogVisible" title="重置密码" width="400px">
      <el-form :model="resetForm" :rules="resetRules" ref="resetFormRef">
        <el-form-item label="用户名">
          <el-input v-model="resetForm.username" disabled />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="resetForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmResetPassword" :loading="resetLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { userApi } from '@/api/api'

const userList = ref([])
const loading = ref(false)
const resetDialogVisible = ref(false)
const resetLoading = ref(false)
const resetFormRef = ref()
const resetForm = ref({
  userId: null,
  username: '',
  newPassword: ''
})

const resetRules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' }
  ]
}

const loadUserList = async () => {
  loading.value = true
  try {
    const res = await userApi.getUserList()
    userList.value = res
  } catch (error) {
    ElMessage.error('获取用户列表失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleResetPassword = (row) => {
  resetForm.value = {
    userId: row.id,
    username: row.username,
    newPassword: ''
  }
  resetDialogVisible.value = true
}

const confirmResetPassword = async () => {
  await resetFormRef.value.validate()
  resetLoading.value = true
  try {
    await userApi.resetPassword({
      userId: resetForm.value.userId,
      newPassword: resetForm.value.newPassword
    })
    ElMessage.success('密码重置成功')
    resetDialogVisible.value = false
  } catch (error) {
    ElMessage.error('密码重置失败')
  } finally {
    resetLoading.value = false
  }
}

const handleToggleStatus = async (row) => {
  const action = row.status === 1 ? '禁用' : '启用'
  try {
    await ElMessageBox.confirm(`确定要${action}用户 "${row.username}" 吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await userApi.updateStatus({
      userId: row.id,
      status: row.status === 1 ? 0 : 1
    })
    ElMessage.success(`${action}成功`)
    loadUserList()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error(`${action}失败`)
    }
  }
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.user-manage-page {
  padding: 20px;
}
</style>
