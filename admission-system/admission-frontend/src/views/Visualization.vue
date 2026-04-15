<template>
  <div class="score-extremes-page">
    <el-card>
      <template #header>
        <span>分数可视化分析</span>
      </template>

      <div class="form-container">
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
              :max-collapse-tags="5"
            >
              <el-option
                v-for="item in schoolOptions"
                :key="item.yxdm"
                :label="item.yxmc"
                :value="item.yxdm"
              />
            </el-select>
            <div class="selected-schools-tip" v-if="selectedSchools.length > 0">
              已选 {{ selectedSchools.length }} 所院校（最多5所）
            </div>
          </div>
        </div>

        <div class="form-row">
          <div class="form-label"></div>
          <div class="form-content">
            <el-button type="primary" @click="loadData" :disabled="selectedSchools.length < 2">
              查询
            </el-button>
            <el-button @click="reset">重置</el-button>
          </div>
        </div>
      </div>

      <div class="chart-card">
        <div class="chart-title">历年最低录取分数（所有专业中的最低分）</div>
        <div ref="minChart" style="height: 400px; width: 100%;"></div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { dataApi } from '@/api/api'
import { ElMessage } from 'element-plus'

const selectedSchools = ref([])
const schoolOptions = ref([])
const allSchools = ref([])
const searchLoading = ref(false)

const minChart = ref()
let minInstance = null

const loadAllSchools = async () => {
  try {
    const res = await dataApi.getSchools(2024)
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
  } catch (error) {
    ElMessage.error('加载院校列表失败')
    console.error(error)
  }
}

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

const handleSchoolChange = () => {
  if (selectedSchools.value.length > 5) {
    selectedSchools.value = selectedSchools.value.slice(0, 5)
    ElMessage.warning('最多只能选择5所院校')
  }
}

const reset = () => {
  selectedSchools.value = []
  if (minInstance) minInstance.clear()
}

const loadData = async () => {
  if (selectedSchools.value.length < 2) {
    ElMessage.warning('请至少选择2所院校')
    return
  }

  try {
    // 把院校代码和你选择的正确名称一起传给后端
    const sendData = selectedSchools.value.map(yxdm => {
      const school = allSchools.value.find(s => s.yxdm === yxdm)
      return {
        yxdm: yxdm,
        yxmc: school ? school.yxmc : yxdm
      }
    })
    const res = await dataApi.schoolMinScore(sendData)
    console.log('后端返回的完整数据：', res)

    if (!res || !res.minSeries || res.minSeries.length === 0) {
      ElMessage.warning('暂无对比数据')
      return
    }

    renderMinChart(res.years, res.minSeries)
  } catch (error) {
    ElMessage.error('加载数据失败')
    console.error(error)
  }
}

const renderMinChart = (years, seriesList) => {
  if (!minInstance) {
    minInstance = echarts.init(minChart.value)
  } else {
    minInstance.clear()
  }

  // 统计院校名称出现次数，用于区分同名院校
  const nameCount = new Map()
  seriesList.forEach(series => {
    const name = series.name
    nameCount.set(name, (nameCount.get(name) || 0) + 1)
  })

  const series = seriesList.map(series => {
    let displayName = series.name
    // 如果名称重复且后端提供了 yxdm，则添加代码后缀
    if (nameCount.get(series.name) > 1 && series.yxdm) {
      displayName = `${series.name} (${series.yxdm})`
    }

    const seriesData = years.map(year => {
      const detail = series.details?.find(d => d.year === year)
      if (detail) {
        return {
          value: detail.score,
          major: detail.major
        }
      }
      return null
    })

    return {
      name: displayName,
      type: 'line',
      data: seriesData,
      connectNulls: false,
      symbol: 'circle',
      symbolSize: 8,
      label: {
        show: true,
        position: 'top',
        formatter: (params) => params.value ?? ''
      }
    }
  })

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: (params) => {
        let html = `<div>${params[0].axisValue}年</div>`
        params.forEach(item => {
          if (item.value) {
            let major = item.data.major || ''
            if (major.includes('、')) {
              const parts = major.split('、')
              if (parts.length > 3) {
                major = parts.slice(0, 3).join('、') + '等'
              }
            }
            html += `
              <div style="display: flex; align-items: center; margin: 4px 0;">
                <span style="display: inline-block; width: 10px; height: 10px; background: ${item.color}; border-radius: 50%; margin-right: 6px;"></span>
                <span>${item.seriesName}：${item.value}分，专业：${major}</span>
              </div>
            `
          } else {
            html += `
              <div style="display: flex; align-items: center; margin: 4px 0; color: #999;">
                <span>${item.seriesName}：暂无数据</span>
              </div>
            `
          }
        })
        return html
      }
    },
    legend: {
      data: series.map(s => s.name),
      top: 10,
      left: 'center'
    },
    xAxis: {
      type: 'category',
      data: years,
      name: '年份'
    },
    yAxis: {
      type: 'value',
      name: '分数',
      min: (value) => Math.floor(value.min - 10),
      max: (value) => Math.ceil(value.max + 10)
    },
    series: series,
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    }
  }

  minInstance.setOption(option, true)
  minInstance.resize()
}

window.addEventListener('resize', () => {
  minInstance?.resize()
})

onMounted(() => {
  loadAllSchools()
  minInstance = echarts.init(minChart.value)
})
</script>

<style scoped>
.score-extremes-page {
  padding: 20px;
}
.form-container {
  max-width: 800px;
  margin: 0 auto;
}
.form-row {
  display: flex;
  align-items: center;
  justify-content: center;
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
  max-width: 600px;
}
:deep(.el-select__input) {
  text-align: center !important;
}
:deep(.el-select__placeholder) {
  text-align: center !important;
  position: absolute !important;
  left: 50% !important;
  top: 50% !important;
  transform: translate(-50%, -50%) !important;
  right: auto !important;
  color: #c0c4cc !important;
}
.selected-schools-tip {
  font-size: 12px;
  margin-top: 6px;
  text-align: center;
  color: #909399;
}
.chart-card {
  margin-top: 20px;
  padding: 20px;
  background: #fff;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
.chart-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 16px;
}
</style>