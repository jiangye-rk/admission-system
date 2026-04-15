<template>
  <div class="query-page">
    <el-card>
      <template #header><span>录取数据查询</span></template>
      <el-form :inline="true" :model="queryForm" class="query-form">
        <el-form-item label="年份">
          <el-select v-model="queryForm.year" placeholder="请选择" style="width: 120px; height: 32px;" class="query-select">
            <el-option label="2024" value="2024" />
            <el-option label="2023" value="2023" />
            <el-option label="2022" value="2022" />
            <el-option label="2021" value="2021" />
            <el-option label="2020" value="2020" />
          </el-select>
        </el-form-item>
        <el-form-item label="院校名称">
          <el-input v-model="queryForm.yxmc" placeholder="请输入院校名称" clearable />
        </el-form-item>
        <el-form-item label="专业名称">
          <el-input v-model="queryForm.zymc" placeholder="请输入专业名称" clearable />
        </el-form-item>
        <el-form-item label="批次">
          <el-select v-model="queryForm.pcmc" placeholder="请选择" clearable style="width: 180px; height: 32px;" class="query-select">
            <el-option label="普通类本科批A阶段" value="普通类本科批A阶段" />
            <el-option label="普通类本科批B阶段" value="普通类本科批B阶段" />
            <el-option label="普通类提前本科批A阶段" value="普通类提前本科批A阶段" />
            <el-option label="普通类提前本科批B阶段" value="普通类提前本科批B阶段" />
            <el-option label="普通类高职高专批" value="普通类高职高专批" />
            <el-option label="普通类提前高职高专批" value="普通类提前高职高专批" />
          </el-select>
        </el-form-item>
        <el-form-item label="分数范围">
          <el-input-number v-model="queryForm.minScore" :min="0" :max="750" placeholder="最低分" controls-position="right" />
          <span style="margin: 0 10px;">-</span>
          <el-input-number v-model="queryForm.maxScore" :min="0" :max="750" placeholder="最高分" controls-position="right" />
        </el-form-item>
        <el-form-item label="位次范围">
          <el-input-number v-model="queryForm.minRank" :min="1" placeholder="最低位次" controls-position="right" />
          <span style="margin: 0 10px;">-</span>
          <el-input-number v-model="queryForm.maxRank" :min="1" placeholder="最高位次" controls-position="right" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>查询</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleExportSelected" :disabled="selectedRows.length === 0" v-if="userStore.isLoggedIn">导出选中</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" @selection-change="handleSelectionChange" v-loading="loading" border stripe size="small">
        <el-table-column type="selection" width="55" fixed="left" />
        <el-table-column prop="nf" label="年份" width="70" fixed="left" />
        <el-table-column prop="pcmc" label="批次名称" width="150" />
        <el-table-column prop="yxdm" label="院校代码" width="90" />
        <el-table-column prop="yxmc" label="院校名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="kskmyq" label="选考科目" width="120" show-overflow-tooltip />
        <el-table-column prop="zydm" label="专业代码" width="90" />
        <el-table-column prop="zymc" label="专业名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="zgf" label="组最高分" width="90" sortable />
        <el-table-column prop="zdf" label="组最低分" width="90" sortable />
        <el-table-column prop="pjf" label="组平均分" width="90" />
        <el-table-column prop="zpjwc" label="组平均位次" width="100" />
        <el-table-column prop="zjhs" label="专业计划" width="90" />
        <el-table-column prop="lqs" label="专业录取" width="90" />
        <el-table-column prop="zyzdf" label="专业最低分" width="100" sortable />
        <el-table-column prop="zypjf" label="专业平均分" width="100" />
      </el-table>

      <el-pagination
        v-model:current-page="pageNum"
        v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        style="margin-top: 20px; justify-content: flex-end;"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { dataApi } from '@/api/api'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const userStore = useUserStore()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const selectedRows = ref([])

const queryForm = reactive({
  year: '2024',
  yxmc: '',
  zymc: '',
  pcmc: '',
  minScore: null,
  maxScore: null,
  minRank: null,
  maxRank: null
})

const handleSelectionChange = (val) => {
  selectedRows.value = val
}

const loadData = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      year: queryForm.year ? parseInt(queryForm.year) : null,
      yxmc: queryForm.yxmc || null,
      zymc: queryForm.zymc || null,
      pcmc: queryForm.pcmc || null,
      minScore: queryForm.minScore,
      maxScore: queryForm.maxScore,
      minRank: queryForm.minRank,
      maxRank: queryForm.maxRank
    }
    const res = await dataApi.query(params)
    tableData.value = res.list
    total.value = res.total
  } catch (error) {
    ElMessage.error('查询失败')
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  pageNum.value = 1
  loadData()
}

const handleReset = () => {
  queryForm.year = '2024'
  queryForm.yxmc = ''
  queryForm.zymc = ''
  queryForm.pcmc = ''
  queryForm.minScore = null
  queryForm.maxScore = null
  queryForm.minRank = null
  queryForm.maxRank = null
  handleQuery()
}

const handleExportSelected = async () => {
  const ids = selectedRows.value.map(row => row.id)
  if (ids.length === 0) {
    ElMessage.warning('请先选择要导出的数据')
    return
  }
  try {
    const blob = await dataApi.export(ids)
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    const filename = `${userStore.username}_${new Date().toISOString().slice(0,19).replace(/:/g, '')}.xls`
    link.download = filename
    link.click()
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    ElMessage.error('导出失败')
  }
}

const handleSizeChange = (val) => {
  pageSize.value = val
  loadData()
}
const handleCurrentChange = (val) => {
  pageNum.value = val
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.query-form {
  margin-bottom: 20px;
}
.query-form :deep(.el-form-item) {
  margin-bottom: 18px;
}
.query-form :deep(.el-input__inner) {
  height: 32px;
  line-height: 32px;
}
.query-form :deep(.query-select .el-input__inner) {
  height: 32px;
  line-height: 32px;
}
.query-form :deep(.query-select .el-input) {
  height: 32px;
}
.query-form :deep(.el-form-item__label) {
  line-height: 32px;
}
.query-form :deep(.el-form-item__content) {
  line-height: 32px;
}
</style>
