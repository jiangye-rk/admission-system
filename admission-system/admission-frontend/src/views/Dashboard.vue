<template>
  <div class="dashboard">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error-container">
      <el-result icon="error" title="数据加载失败" sub-title="请检查网络连接或稍后重试">
        <template #extra>
          <el-button type="primary" @click="loadAllData">重新加载</el-button>
        </template>
      </el-result>
    </div>

    <!-- 正常显示 -->
    <div v-else>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card>
            <div class="stat-item">
              <div class="stat-icon" style="background: #409EFF;">
                <el-icon><School /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.schoolCount || 0 }}</div>
                <div class="stat-label">院校数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card>
            <div class="stat-item">
              <div class="stat-icon" style="background: #67C23A;">
                <el-icon><Collection /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.majorCount || 0 }}</div>
                <div class="stat-label">专业数量</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card>
            <div class="stat-item">
              <div class="stat-icon" style="background: #E6A23C;">
                <el-icon><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.studentCount || 0 }}</div>
                <div class="stat-label">录取人数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card>
            <div class="stat-item">
              <div class="stat-icon" style="background: #F56C6C;">
                <el-icon><DataLine /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ stats.avgScore || '0.00' }}</div>
                <div class="stat-label">平均分数</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>热门专业TOP10</span>
            </template>
            <div ref="hotChart" style="height: 400px;"></div>
          </el-card>
        </el-col>
        <el-col :span="12">
          <el-card>
            <template #header>
              <span>分数段分布</span>
            </template>
            <div ref="distChart" style="height: 400px;"></div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { dataApi } from '@/api/api'
import { ElMessage } from 'element-plus'

const stats = ref({
  schoolCount: 0,
  majorCount: 0,
  studentCount: 0,
  avgScore: 0
})

const hotChart = ref()
const distChart = ref()

const loading = ref(true)
const error = ref(false)

const loadAllData = async () => {
  loading.value = true
  error.value = false
  
  try {
    // 并行加载所有数据，减少总等待时间
    const [statisticsData, hotMajorsData, distributionData] = await Promise.all([
      dataApi.getStatistics(2024),
      dataApi.getHotMajors(2024, 10),
      dataApi.getDistribution(2024)
    ])
    
    // 更新统计数据
    stats.value = statisticsData
    
    // 初始化热门专业图表 - 使用setTimeout确保DOM已渲染
    setTimeout(() => {
      console.log('hotChart ref:', hotChart.value)
      console.log('hotMajorsData:', hotMajorsData)
      
      if (hotChart.value && hotMajorsData && hotMajorsData.length > 0) {
        try {
          const hotChartInstance = echarts.init(hotChart.value)
          const option = {
            tooltip: { trigger: 'axis' },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '15%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              data: hotMajorsData.map(item => item.zymc || '未知'),
              axisLabel: { 
                rotate: 45,
                interval: 0
              }
            },
            yAxis: { type: 'value', name: '录取人数' },
            series: [{
              data: hotMajorsData.map(item => item.total_lqs || 0),
              type: 'bar',
              itemStyle: { color: '#409EFF' }
            }]
          }
          console.log('Hot chart option:', option)
          hotChartInstance.setOption(option)
          window.addEventListener('resize', () => hotChartInstance.resize())
        } catch (err) {
          console.error('热门专业图表初始化失败:', err)
        }
      } else {
        console.warn('热门专业图表无法初始化: 缺少DOM元素或数据')
      }
    }, 100)
    
    // 初始化分数段分布图表 - 使用setTimeout确保DOM已渲染
    setTimeout(() => {
      console.log('distChart ref:', distChart.value)
      console.log('distributionData:', distributionData)
      
      if (distChart.value && distributionData && distributionData.length > 0) {
        try {
          const distChartInstance = echarts.init(distChart.value)
          const chartData = Array.isArray(distributionData) ? distributionData : []
          const option = {
            tooltip: { trigger: 'axis' },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              data: chartData.map(item => item.range || ''),
              axisLabel: { interval: 0 }
            },
            yAxis: { type: 'value', name: '人数' },
            series: [{
              data: chartData.map(item => item.count || 0),
              type: 'bar',
              itemStyle: { color: '#67C23A' }
            }]
          }
          console.log('Dist chart option:', option)
          distChartInstance.setOption(option)
          window.addEventListener('resize', () => distChartInstance.resize())
        } catch (err) {
          console.error('分数段分布图表初始化失败:', err)
        }
      } else {
        console.warn('分数段分布图表无法初始化: 缺少DOM元素或数据')
      }
    }, 100)
    
  } catch (error) {
    console.error('数据加载失败', error)
    ElMessage.error('数据加载失败，请稍后重试')
    error.value = true
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAllData()
})
</script>

<style scoped>
.stat-item {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.loading-container {
  padding: 20px;
}

.error-container {
  text-align: center;
  padding: 40px 20px;
}
</style>