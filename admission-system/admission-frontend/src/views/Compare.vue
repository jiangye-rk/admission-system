<template>
  <div class="compare-page">
    <el-card>
      <template #header>
        <span>院校对比分析</span>
      </template>

      <div class="compare-container">
        <!-- 数据年份 -->
        <div class="form-row">
          <div class="form-label">数据年份</div>
          <div class="form-content">
            <el-select v-model="selectedYear" placeholder="选择年份" @change="handleYearChange" style="width: 200px;">
              <el-option v-for="year in years" :key="year" :label="year" :value="year" />
            </el-select>
          </div>
        </div>

        <!-- 选择院校（多选可搜索） -->
        <div class="form-row">
          <div class="form-label">选择院校</div>
          <div class="form-content">
            <el-select
              v-model="selectedSchools"
              multiple
              filterable
              remote
              :remote-method="remoteSearch"
              :loading="searchLoading"
              placeholder="请输入院校名称搜索"
              value-key="yxdm"
              @change="handleSchoolChange"
              style="width: 100%;"
              clearable
            >
              <el-option
                v-for="item in schoolOptions"
                :key="item.yxdm"
                :label="item.yxmc"
                :value="item.yxdm"
              />
            </el-select>
            <div class="selected-schools-tip" v-if="selectedSchools.length > 0">
              已选 {{ selectedSchools.length }} 所院校
            </div>
          </div>
        </div>

        <!-- 选择专业 -->
        <div class="form-row">
          <div class="form-label">选择专业</div>
          <div class="form-content">
            <el-select
              v-model="selectedMajor"
              :disabled="commonMajors.length === 0"
              placeholder="请选择共同专业"
              filterable
              style="width: 100%;"
              @change="handleMajorChange"
              clearable
            >
              <el-option
                v-for="major in commonMajors"
                :key="major"
                :label="major"
                :value="major"
              />
            </el-select>
            <div v-if="commonMajors.length === 0 && selectedSchools.length >= 2" class="warning-text">
              所选院校无共同专业，无法对比
            </div>
          </div>
        </div>

        <!-- 按钮区域 -->
        <div class="form-row">
          <div class="form-label"></div>
          <div class="form-content">
            <el-button type="primary" @click="loadComparison" :disabled="!canCompare">
              开始对比
            </el-button>
            <el-button @click="reset">重置</el-button>
          </div>
        </div>
      </div>

      <!-- 图表区域 -->
      <div ref="compareChart" style="height: 400px; margin-top: 20px;"></div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import * as echarts from 'echarts'
import { dataApi } from '@/api/api'
import { ElMessage } from 'element-plus'

const years = [2025, 2024, 2023, 2022, 2021]
const selectedYear = ref(2024)
const selectedSchools = ref([])
const selectedMajor = ref('')
const compareChart = ref()
let chartInstance = null

const schoolOptions = ref([])
const allSchools = ref([])
const searchLoading = ref(false)
const commonMajors = ref([])

const canCompare = computed(() => {
  return selectedSchools.value.length >= 2 && selectedMajor.value
})

// 加载所有院校
const loadAllSchools = async () => {
  try {
    const res = await dataApi.getSchools(selectedYear.value)
    const unique = []
    const codeSet = new Set()
    res.forEach(item => {
      if (!codeSet.has(item.yxdm)) {
        codeSet.add(item.yxdm)
        unique.push(item)
      }
    })
    allSchools.value = unique
    schoolOptions.value = unique
    if (unique.length < 10) {
      ElMessage.info(`该年份仅有 ${unique.length} 所院校数据`)
    }
  } catch (error) {
    ElMessage.error('加载院校列表失败')
    console.error(error)
  }
}

// 远程搜索
const remoteSearch = (query) => {
  if (query) {
    searchLoading.value = true
    setTimeout(() => {
      schoolOptions.value = allSchools.value.filter(item =>
        item.yxmc.toLowerCase().includes(query.toLowerCase())
      )
      searchLoading.value = false
    }, 200)
  } else {
    schoolOptions.value = allSchools.value
  }
}

// 年份切换
const handleYearChange = () => {
  selectedSchools.value = []
  selectedMajor.value = ''
  commonMajors.value = []
  loadAllSchools()
}

// 院校变化，计算共同专业
const handleSchoolChange = async () => {
  if (selectedSchools.value.length < 2) {
    commonMajors.value = []
    selectedMajor.value = ''
    return
  }

  try {
    const promises = selectedSchools.value.map(code =>
      dataApi.getMajors(code, selectedYear.value)
    )
    const results = await Promise.all(promises)
    console.log('院校代码:', selectedSchools.value)
    console.log('年份:', selectedYear.value)
    console.log('专业列表结果:', results)
    const intersection = results.reduce((acc, curr) => {
      if (!Array.isArray(curr)) return []
      return acc.filter(item => curr.includes(item))
    }, results[0] || [])
    console.log('共同专业:', intersection)
    commonMajors.value = intersection
    if (intersection.length === 0) {
      ElMessage.warning('所选院校无共同专业')
    }
    selectedMajor.value = ''
  } catch (error) {
    ElMessage.error('获取专业列表失败')
    console.error(error)
  }
}

const handleMajorChange = () => {}

const reset = () => {
  selectedSchools.value = []
  selectedMajor.value = ''
  commonMajors.value = []
  if (chartInstance) chartInstance.clear()
}

const loadComparison = async () => {
  if (!canCompare) return
  try {
    const res = await dataApi.compare({
      yxdms: selectedSchools.value,
      zymc: selectedMajor.value,
      year: selectedYear.value
    })

    if (!res || !res.series || res.series.length === 0) {
      ElMessage.warning('所选院校暂无该专业对比数据')
      return
    }

    const hasData = res.series.some(s => s.data.some(v => v != null))
    if (!hasData) {
      ElMessage.warning('所选专业在所选院校中均无数据')
      if (chartInstance) chartInstance.clear()
      return
    }

    if (!chartInstance) {
      chartInstance = echarts.init(compareChart.value)
    }

    const option = {
      title: { text: `${selectedMajor.value} - 历年分数线对比` },
      tooltip: { trigger: 'axis' },
      legend: { data: res.legend },
      xAxis: { type: 'category', data: res.years, name: '年份' },
      yAxis: { type: 'value', name: '最低分' },
      series: res.series.map(s => ({
        name: s.name,
        type: 'line',
        data: s.data,
        connectNulls: true,
        symbol: 'circle',
        symbolSize: 8,
        label: { show: true, position: 'top', formatter: (params) => params.value ?? '' }
      }))
    }
    chartInstance.setOption(option)
    chartInstance.resize()
  } catch (error) {
    ElMessage.error('对比失败')
    console.error(error)
  }
}

window.addEventListener('resize', () => {
  chartInstance?.resize()
})

onMounted(() => {
  loadAllSchools()
})
</script>

<style scoped>
.compare-page {
  padding: 20px;
}

.compare-container {
  max-width: 800px;
  margin: 0 auto; /* 保留整个表单居中的布局 */
}

.form-row {
  display: flex;
  align-items: center;
  justify-content: center; /* 保持表单项整体居中 */
  margin-bottom: 20px;
}

.form-label {
  width: 100px;
  text-align: right;
  padding-right: 12px;
  font-weight: 500;
  color: #606266;
  font-size: 14px;
}

.form-content {
  flex: 1;
  min-width: 0;
  max-width: 600px; /* 限制输入框宽度 */
}

/* 强制让输入框内的文字（比如2024）居中显示 */
:deep(.el-select__input) {
  text-align: center !important;
}

/* 让提示文字在输入框内居中，替换原来的右对齐 */
:deep(.el-select__placeholder) {
  text-align: center !important;
  position: absolute !important;
  left: 50% !important;
  top: 50% !important;
  transform: translate(-50%, -50%) !important;
  right: auto !important;
  color: #c0c4cc !important;
}

/* 多选框的标签样式 */
:deep(.el-select__tags-text) {
  color: #606266;
}

/* 提示文字保持居中 */
.selected-schools-tip,
.warning-text {
  font-size: 12px;
  margin-top: 6px;
  text-align: center;
  color: #909399;
}

.warning-text {
  color: #e6a23c;
}
</style>