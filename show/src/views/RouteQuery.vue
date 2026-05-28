<template>
  <el-card class="panel" shadow="never">
    <template #header>
      <div class="panel-header">
        <div>
          <h3>路线查询</h3>
          <p>支持连通性、全部路径和最优路径查询。</p>
        </div>
      </div>
    </template>
    <el-form :model="queryForm" label-position="top" class="query-grid">
      <el-form-item label="起点站">
        <el-select v-model="queryForm.departureStation" placeholder="请选择起点站" filterable>
          <el-option
            v-for="station in stations"
            :key="station"
            :label="station"
            :value="station"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="终点站">
        <el-select v-model="queryForm.arrivalStation" placeholder="请选择终点站" filterable>
          <el-option
            v-for="station in stations"
            :key="station"
            :label="station"
            :value="station"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="查询类型">
        <el-radio-group v-model="queryForm.type">
          <el-radio label="accessibility">连通性查询</el-radio>
          <el-radio label="all">全部路径</el-radio>
          <el-radio label="best">最优路径</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item v-if="queryForm.type === 'best'" label="优先策略">
        <el-radio-group v-model="queryForm.preference">
          <el-radio label="time">时间优先</el-radio>
          <el-radio label="price">价格优先</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>

    <el-button type="primary" @click="handleQuery" :loading="loading">查询</el-button>

    <div v-if="result !== null" class="result-container">
      <el-card shadow="never" class="result-card">
        <template #header>
          <h4>查询结果</h4>
        </template>
        <pre class="result-text">{{ result }}</pre>
      </el-card>
    </div>
  </el-card>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const queryForm = reactive({
  departureStation: '',
  arrivalStation: '',
  type: 'accessibility',
  preference: 'time'
})

const stations = ref([])
const result = ref(null)
const loading = ref(false)

const loadStations = async () => {
  try {
    const response = await axios.get('/api/route/stations')
    if (response.data.code === 200) {
      stations.value = response.data.data
    }
  } catch (error) {
    ElMessage.error('加载站点列表失败')
  }
}

const handleQuery = async () => {
  if (!queryForm.departureStation || !queryForm.arrivalStation) {
    ElMessage.warning('请选择起点和终点')
    return
  }

  loading.value = true
  result.value = null

  try {
    if (queryForm.type === 'accessibility') {
      const response = await axios.post('/api/route/accessibility', {
        departureStation: queryForm.departureStation,
        arrivalStation: queryForm.arrivalStation
      })

      if (response.data.code === 200) {
        result.value = response.data.data ? '两站之间可以连通' : '两站之间无法连通'
      } else {
        ElMessage.error(response.data.message || '查询失败')
      }
    } else if (queryForm.type === 'all') {
      const response = await axios.post('/api/route/findAll', {
        departureStation: queryForm.departureStation,
        arrivalStation: queryForm.arrivalStation
      })

      if (response.data.code === 200) {
        result.value = response.data.data || '未找到路线'
      } else {
        ElMessage.error(response.data.message || '查询失败')
      }
    } else if (queryForm.type === 'best') {
      const response = await axios.post('/api/route/best', {
        departureStation: queryForm.departureStation,
        arrivalStation: queryForm.arrivalStation,
        preference: queryForm.preference
      })

      if (response.data.code === 200) {
        result.value = response.data.data || '未找到路线'
      } else {
        ElMessage.error(response.data.message || '查询失败')
      }
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '查询失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStations()
})
</script>

<style scoped>
.panel {
  border: none;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.82);
}

.panel-header h3,
.result-card h4 {
  margin: 0;
}

.panel-header p {
  margin: 8px 0 0;
  color: #64748b;
}

.query-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 18px;
}

.result-container {
  margin-top: 20px;
}

.result-card {
  border-radius: 20px;
  background: #f8fafc;
}

.result-text {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: 'Courier New', monospace;
  line-height: 1.6;
  margin: 0;
}
</style>
