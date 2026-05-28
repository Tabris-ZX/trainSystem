<template>
  <el-card class="panel" shadow="never">
    <template #header>
      <div class="panel-header">
        <div>
          <h3>余票查询</h3>
          <p>按车次、出发站和发车时间查询剩余车票。</p>
        </div>
        <el-tag type="success" effect="dark">实时查询</el-tag>
      </div>
    </template>

    <el-form label-position="top" class="query-grid">
      <el-form-item label="车次">
        <el-select
          v-model="queryForm.trainId"
          placeholder="请选择车次"
          filterable
          clearable
          @change="handleTrainChange"
        >
          <el-option
            v-for="train in trains"
            :key="train.trainId"
            :label="formatTrainLabel(train)"
            :value="train.trainId"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="出发站">
        <el-select
          v-model="queryForm.departureStation"
          placeholder="请选择出发站"
          filterable
          clearable
        >
          <el-option
            v-for="station in availableStations"
            :key="station"
            :label="station"
            :value="station"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="发车日期时间">
        <el-date-picker
          v-model="departureDateTime"
          type="datetime"
          placeholder="请选择发车日期时间"
          format="YYYY-MM-DD HH:mm"
          value-format="YYYY-MM-DD HH:mm"
        />
      </el-form-item>
    </el-form>

    <div class="action-row">
      <el-button type="primary" :loading="loading" @click="handleQuery">查询余票</el-button>
    </div>

    <el-alert
      v-if="selectedTrain"
      class="train-tip"
      type="info"
      :closable="false"
      :title="`当前路线：${selectedTrain.stations.join(' -> ')}`"
    />

    <el-result
      v-if="remaining !== null"
      icon="success"
      :title="`余票数量：${remaining}`"
      sub-title="结果基于当前选择的车次、出发站和发车时间。"
    />
  </el-card>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import axios from 'axios'
import { ElMessage } from 'element-plus'
import { useStore } from '../store'

const store = useStore()

const trains = ref([])
const loading = ref(false)
const remaining = ref(null)
const departureDateTime = ref('')

const queryForm = reactive({
  trainId: '',
  departureStation: '',
  departureTime: ''
})

const selectedTrain = computed(() => {
  return trains.value.find(train => train.trainId === queryForm.trainId) || null
})

const availableStations = computed(() => {
  if (!selectedTrain.value?.stations?.length) {
    return []
  }
  return selectedTrain.value.stations.slice(0, -1)
})

const formatDepartureTime = (dateTime) => {
  if (!dateTime) {
    return ''
  }
  const [datePart, timePart] = dateTime.split(' ')
  const [, month, day] = datePart.split('-')
  return `${timePart} ${month}-${day}`
}

const formatTrainLabel = (train) => {
  const stationText = train.stations?.length ? train.stations.join(' -> ') : '暂无站点信息'
  return `${train.trainId} | ${train.startTime} | ${stationText}`
}

const loadTrains = async () => {
  try {
    const response = await axios.get('/api/train/list', {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })

    if (response.data.code === 200) {
      trains.value = response.data.data || []
      return
    }

    ElMessage.error(response.data.message || '加载车次失败')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '加载车次失败')
  }
}

const handleTrainChange = () => {
  queryForm.departureStation = ''
  remaining.value = null
}

const handleQuery = async () => {
  queryForm.departureTime = formatDepartureTime(departureDateTime.value)

  if (!queryForm.trainId || !queryForm.departureStation || !queryForm.departureTime) {
    ElMessage.warning('请完整选择车次、出发站和发车日期时间')
    return
  }

  loading.value = true
  remaining.value = null
  try {
    const response = await axios.post('/api/ticket/remaining', queryForm, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })

    if (response.data.code === 200) {
      remaining.value = response.data.data
      return
    }

    ElMessage.error(response.data.message || '查询余票失败')
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '查询余票失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadTrains()
})
</script>

<style scoped>
.panel {
  border: none;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.82);
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.panel-header h3 {
  margin: 0;
  font-size: 24px;
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

.action-row {
  margin-top: 8px;
}

.train-tip {
  margin-top: 20px;
}
</style>
