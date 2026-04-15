<template>
    <div class="import-page">
      <el-card>
        <template #header>
          <span>Excel数据导入</span>
        </template>
  
        <el-upload
          class="upload-demo"
          drag
          action="#"
          :http-request="handleUpload"
          :before-upload="beforeUpload"
          :file-list="fileList"
          accept=".xls,.xlsx"
        >
          <el-icon class="el-icon--upload"><upload-filled /></el-icon>
          <div class="el-upload__text">
            将文件拖到此处，或<em>点击上传</em>
          </div>
          <template #tip>
            <div class="el-upload__tip">
              仅支持 .xls 或 .xlsx 格式，文件大小不超过 10MB
            </div>
          </template>
        </el-upload>
  
        <el-progress v-if="uploading" :percentage="uploadProgress" style="margin-top: 20px;" />
  
        <el-alert
          v-if="resultMessage"
          :title="resultMessage"
          :type="resultType"
          :closable="false"
          style="margin-top: 20px;"
        />
      </el-card>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
  import { UploadFilled } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus'
  import { excelApi } from '@/api/api'
  
  const fileList = ref([])
  const uploading = ref(false)
  const uploadProgress = ref(0)
  const resultMessage = ref('')
  const resultType = ref('info')
  
  const beforeUpload = (file) => {
    const isExcel = file.type === 'application/vnd.ms-excel' || 
                    file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    const isLt10M = file.size / 1024 / 1024 < 10
    if (!isExcel) {
      ElMessage.error('只能上传 Excel 文件!')
      return false
    }
    if (!isLt10M) {
      ElMessage.error('文件大小不能超过 10MB!')
      return false
    }
    return true
  }
  
  const handleUpload = async (options) => {
    const file = options.file
    uploading.value = true
    uploadProgress.value = 0
    resultMessage.value = ''
  
    try {
      // 模拟进度（实际无法获取上传进度，只是UI效果）
      const interval = setInterval(() => {
        if (uploadProgress.value < 90) {
          uploadProgress.value += 10
        }
      }, 300)
  
      const res = await excelApi.import(file)
      clearInterval(interval)
      uploadProgress.value = 100
      resultMessage.value = res || '导入成功'
      resultType.value = 'success'
      fileList.value = []
    } catch (error) {
      uploadProgress.value = 0
      resultMessage.value = error.message || '导入失败'
      resultType.value = 'error'
    } finally {
      uploading.value = false
      setTimeout(() => {
        uploadProgress.value = 0
      }, 2000)
    }
  }
  </script>
  
  <style scoped>
  .import-page {
    padding: 20px;
  }
  .upload-demo {
    max-width: 500px;
    margin: 0 auto;
  }
  </style>