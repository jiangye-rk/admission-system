<template>
  <div class="recommendation-container">
    <el-card class="search-card">
      <template #header>
        <div class="card-header">
          <span>智能志愿推荐</span>
          <el-tag type="info" v-if="userStore.fs && userStore.wc">
            我的分数：{{ userStore.fs }} | 位次：{{ userStore.wc }}
          </el-tag>
        </div>
      </template>

      <el-form :model="searchForm" label-width="100px" class="search-form">
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="分数">
              <el-input-number
                v-model="searchForm.score"
                :min="0"
                :max="750"
                placeholder="请输入分数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="位次">
              <el-input-number
                v-model="searchForm.rank"
                :min="1"
                placeholder="请输入位次"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="年份">
              <el-select v-model="searchForm.year" placeholder="选择年份" style="width: 100%">
                <el-option label="2025" :value="2025" />
                <el-option label="2024" :value="2024" />
                <el-option label="2023" :value="2023" />
                <el-option label="2022" :value="2022" />
                <el-option label="2021" :value="2021" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item label="批次">
              <el-select v-model="searchForm.pcmc" placeholder="选择批次" clearable style="width: 100%">
                <el-option label="普通类一段" value="普通类一段" />
                <el-option label="普通类二段" value="普通类二段" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="6">
            <el-form-item label="风险等级">
              <el-select v-model="searchForm.riskLevel" placeholder="选择风险等级" style="width: 100%">
                <el-option label="全部" :value="0" />
                <el-option label="冲刺" :value="1" />
                <el-option label="稳妥" :value="2" />
                <el-option label="保底" :value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="6">
            <el-form-item>
              <el-button type="primary" @click="generateRecommendations" :loading="loading">
                <el-icon><Search /></el-icon>生成推荐
              </el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider />

      <div class="legend-area" v-if="recommendations.length > 0">
        <el-tag type="danger" effect="dark">冲刺</el-tag>
        <span class="legend-desc">分数/位次略低于往年，有一定风险但有机会</span>
        <el-tag type="warning" effect="dark">稳妥</el-tag>
        <span class="legend-desc">分数/位次与往年匹配，录取机会较大</span>
        <el-tag type="success" effect="dark">保底</el-tag>
        <span class="legend-desc">分数/位次明显高于往年，录取概率很高</span>
      </div>
    </el-card>

    <el-card class="result-card" v-if="recommendations.length > 0">
      <template #header>
        <div class="card-header">
          <span>推荐结果</span>
          <div>
            <el-radio-group v-model="filterType" size="small">
              <el-radio-button :label="0">全部</el-radio-button>
              <el-radio-button :label="1">冲刺</el-radio-button>
              <el-radio-button :label="2">稳妥</el-radio-button>
              <el-radio-button :label="3">保底</el-radio-button>
            </el-radio-group>
          </div>
        </div>
      </template>

      <el-table
        :data="filteredRecommendations"
        style="width: 100%"
        v-loading="loading"
        @row-click="handleRowClick"
        highlight-current-row
      >
        <el-table-column type="index" width="50" />
        <el-table-column label="推荐类型" width="100">
          <template #default="{ row }">
            <el-tag
              :type="getRecommendTypeTag(row.recommendType)"
              effect="dark"
            >
              {{ getRecommendTypeText(row.recommendType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="匹配度" width="100">
          <template #default="{ row }">
            <el-progress
              :percentage="Math.round(row.matchScore)"
              :color="getMatchColor(row.matchScore)"
              :stroke-width="8"
            />
          </template>
        </el-table-column>
        <el-table-column prop="yxmc" label="院校名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="zymc" label="专业名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="nf" label="年份" width="80" />
        <el-table-column prop="pcmc" label="批次" width="120" />
        <el-table-column label="专业最低分" width="100">
          <template #default="{ row }">
            {{ row.zyzdf || row.zdf || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="专业最低位次" width="110">
          <template #default="{ row }">
            {{ row.zyzdfwc || row.zdfwc || '-' }}
          </template>
        </el-table-column>
        <el-table-column label="分数差" width="90">
          <template #default="{ row }">
            <span :class="getScoreDiffClass(row.scoreDiff)">
              {{ row.scoreDiff > 0 ? '+' : '' }}{{ row.scoreDiff }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="位次差" width="100">
          <template #default="{ row }">
            <span :class="getRankDiffClass(row.rankDiff)">
              {{ row.rankDiff > 0 ? '+' : '' }}{{ row.rankDiff }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="标签" width="120">
          <template #default="{ row }">
            <el-tag v-if="row.sf985" type="danger" size="small">985</el-tag>
            <el-tag v-if="row.sf211" type="warning" size="small">211</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              size="small"
              @click.stop="analyzeProbability(row)"
            >
              概率分析
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-area">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <el-empty v-if="!loading && recommendations.length === 0 && hasSearched" description="暂无推荐数据，请调整条件后重新生成" />

    <el-dialog
      v-model="probabilityDialogVisible"
      title="录取概率分析"
      width="600px"
    >
      <div v-if="probabilityData" class="probability-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="院校">{{ probabilityData.yxmc }}</el-descriptions-item>
          <el-descriptions-item label="专业">{{ probabilityData.zymc }}</el-descriptions-item>
          <el-descriptions-item label="您的分数">{{ probabilityData.userScore }}</el-descriptions-item>
          <el-descriptions-item label="您的位次">{{ probabilityData.userRank }}</el-descriptions-item>
          <el-descriptions-item label="专业最低分">{{ probabilityData.minScore }}</el-descriptions-item>
          <el-descriptions-item label="专业最低位次">{{ probabilityData.minRank }}</el-descriptions-item>
          <el-descriptions-item label="分数差">{{ probabilityData.scoreDiff }}</el-descriptions-item>
          <el-descriptions-item label="位次差">{{ probabilityData.rankDiff }}</el-descriptions-item>
        </el-descriptions>

        <div class="probability-chart">
          <h4>录取概率</h4>
          <el-progress
            type="dashboard"
            :percentage="Math.round(probabilityData.probability * 100)"
            :color="getProbabilityColor(probabilityData.probability)"
          />
          <p class="probability-suggestion">{{ probabilityData.suggestion }}</p>
        </div>

        <div class="history-trend" v-if="probabilityData.historyData && probabilityData.historyData.length > 0">
          <h4>历年录取数据</h4>
          <el-timeline>
            <el-timeline-item
              v-for="item in probabilityData.historyData"
              :key="item.id"
              :timestamp="item.nf + '年'"
            >
              最低分: {{ item.zyzdf || item.zdf }} | 位次: {{ item.zyzdfwc || item.zdfwc }} | 录取: {{ item.lqs }}人
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { useUserStore } from '@/store/user'
import { generateRecommendations as apiGenerateRecommendations, analyzeProbability as apiAnalyzeProbability } from '@/api/api'

const userStore = useUserStore()
const loading = ref(false)
const hasSearched = ref(false)
const recommendations = ref([])
const pageNum = ref(1)
const pageSize = ref(20)
const total = ref(0)
const filterType = ref(0)
const probabilityDialogVisible = ref(false)
const probabilityData = ref(null)

const searchForm = reactive({
  score: null,
  rank: null,
  year: 2024,
  pcmc: '',
  riskLevel: 0
})

onMounted(() => {
  if (userStore.fs) {
    searchForm.score = parseInt(userStore.fs)
  }
  if (userStore.wc) {
    searchForm.rank = parseInt(userStore.wc)
  }
})

const filteredRecommendations = computed(() => {
  let result = recommendations.value
  if (filterType.value !== 0) {
    result = result.filter(item => item.recommendType === filterType.value)
  }
  return result
})

const generateRecommendations = async () => {
  if (!searchForm.score || !searchForm.rank) {
    ElMessage.warning('请输入分数和位次')
    return
  }

  loading.value = true
  hasSearched.value = true

  try {
    console.log('发送推荐请求:', {
      score: searchForm.score,
      rank: searchForm.rank,
      year: searchForm.year,
      pcmc: searchForm.pcmc || null
    })
    const res = await apiGenerateRecommendations({
      score: searchForm.score,
      rank: searchForm.rank,
      year: searchForm.year,
      pcmc: searchForm.pcmc || null
    })
    console.log('推荐响应:', res)

    // request.js 拦截器直接返回了 res.data，所以 res 就是数据数组
    if (Array.isArray(res)) {
      recommendations.value = res
      total.value = recommendations.value.length
      ElMessage.success(`成功生成 ${recommendations.value.length} 条推荐`)
    } else if (res.code === 200) {
      recommendations.value = res.data || []
      total.value = recommendations.value.length
      ElMessage.success(`成功生成 ${recommendations.value.length} 条推荐`)
    } else {
      ElMessage.error(res.msg || '生成推荐失败')
    }
  } catch (error) {
    console.error('推荐请求错误:', error)
    ElMessage.error('生成推荐失败：' + (error.message || '未知错误'))
  } finally {
    loading.value = false
  }
}

const resetForm = () => {
  searchForm.score = userStore.fs ? parseInt(userStore.fs) : null
  searchForm.rank = userStore.wc ? parseInt(userStore.wc) : null
  searchForm.year = 2024
  searchForm.pcmc = ''
  searchForm.riskLevel = 0
  recommendations.value = []
  hasSearched.value = false
  filterType.value = 0
}

const getRecommendTypeTag = (type) => {
  const map = { 1: 'danger', 2: 'warning', 3: 'success' }
  return map[type] || 'info'
}

const getRecommendTypeText = (type) => {
  const map = { 1: '冲刺', 2: '稳妥', 3: '保底' }
  return map[type] || '未知'
}

const getMatchColor = (score) => {
  if (score >= 80) return '#67C23A'
  if (score >= 60) return '#E6A23C'
  return '#F56C6C'
}

const getScoreDiffClass = (diff) => {
  if (diff >= 20) return 'score-high'
  if (diff >= 0) return 'score-normal'
  return 'score-low'
}

const getRankDiffClass = (diff) => {
  if (diff >= 5000) return 'rank-high'
  if (diff >= 0) return 'rank-normal'
  return 'rank-low'
}

const getProbabilityColor = (probability) => {
  if (probability >= 0.7) return '#67C23A'
  if (probability >= 0.4) return '#E6A23C'
  return '#F56C6C'
}

const analyzeProbability = async (row) => {
  try {
    const res = await apiAnalyzeProbability({
      yxdm: row.yxdm,
      zymc: row.zymc,
      year: row.nf
    })

    // request.js 拦截器直接返回了 res.data
    if (res && typeof res === 'object') {
      probabilityData.value = {
        ...res,
        yxmc: row.yxmc,
        zymc: row.zymc,
        minScore: row.zyzdf || row.zdf,
        minRank: row.zyzdfwc || row.zdfwc
      }
      probabilityDialogVisible.value = true
    } else {
      ElMessage.error('分析失败')
    }
  } catch (error) {
    ElMessage.error('分析失败：' + error.message)
  }
}

const handleRowClick = (row) => {
}

const handleSizeChange = (val) => {
  pageSize.value = val
}

const handleCurrentChange = (val) => {
  pageNum.value = val
}
</script>

<style scoped>
.recommendation-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-form {
  margin-top: 10px;
}

.legend-area {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.legend-desc {
  color: #666;
  font-size: 14px;
  margin-right: 20px;
}

.result-card {
  margin-top: 20px;
}

.pagination-area {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.score-high, .rank-high {
  color: #67C23A;
  font-weight: bold;
}

.score-normal, .rank-normal {
  color: #E6A23C;
}

.score-low, .rank-low {
  color: #F56C6C;
}

.probability-content {
  padding: 10px;
}

.probability-chart {
  text-align: center;
  margin: 20px 0;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.probability-chart h4 {
  margin-bottom: 15px;
  color: #303133;
}

.probability-suggestion {
  margin-top: 15px;
  color: #606266;
  font-size: 14px;
}

.history-trend {
  margin-top: 20px;
}

.history-trend h4 {
  margin-bottom: 15px;
  color: #303133;
}
</style>
