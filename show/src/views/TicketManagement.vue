<template>
  <el-card class="panel" shadow="never">
    <template #header>
      <div class="panel-header">
        <div>
          <h3>票务管理</h3>
          <p>管理员可以按发车时间发售或停止发售指定车次。</p>
        </div>
      </div>
    </template>

    <el-tabs v-model="activeTab">
      <el-tab-pane label="发售车票" name="release">
        <el-form :model="releaseForm" label-position="top" class="ticket-form">
          <el-form-item label="车次 ID">
            <el-input v-model="releaseForm.trainId" placeholder="请输入车次 ID" />
          </el-form-item>
          <el-form-item label="发车时间">
            <el-input v-model="releaseForm.departureTime" placeholder="格式：HH:mm MM-DD，例如 08:00 06-15" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleRelease" :loading="loading">发售</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>

      <el-tab-pane label="停止发售" name="expire">
        <el-form :model="expireForm" label-position="top" class="ticket-form">
          <el-form-item label="车次 ID">
            <el-input v-model="expireForm.trainId" placeholder="请输入车次 ID" />
          </el-form-item>
          <el-form-item label="发车时间">
            <el-input v-model="expireForm.departureTime" placeholder="格式：HH:mm MM-DD，例如 08:00 06-15" />
          </el-form-item>
          <el-form-item>
            <el-button type="danger" @click="handleExpire" :loading="loading">停止发售</el-button>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
  </el-card>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useStore } from '../store'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const store = useStore()

const activeTab = ref('release')
const loading = ref(false)

const releaseForm = reactive({
  trainId: '',
  departureTime: ''
})

const expireForm = reactive({
  trainId: '',
  departureTime: ''
})

const handleRelease = async () => {
  if (!releaseForm.trainId || !releaseForm.departureTime) {
    ElMessage.warning('请填写完整信息')
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/ticket/release', {
      trainId: releaseForm.trainId,
      departureTime: releaseForm.departureTime
    }, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })

    if (response.data.code === 200) {
      ElMessage.success('发售成功')
      releaseForm.trainId = ''
      releaseForm.departureTime = ''
    } else {
      ElMessage.error(response.data.message || '发售失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '发售失败')
  } finally {
    loading.value = false
  }
}

const handleExpire = async () => {
  if (!expireForm.trainId || !expireForm.departureTime) {
    ElMessage.warning('请填写完整信息')
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/ticket/expire', {
      trainId: expireForm.trainId,
      departureTime: expireForm.departureTime
    }, {
      headers: {
        Authorization: `Bearer ${store.sessionId}`
      }
    })

    if (response.data.code === 200) {
      ElMessage.success('停止发售成功')
      expireForm.trainId = ''
      expireForm.departureTime = ''
    } else {
      ElMessage.error(response.data.message || '停止发售失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '停止发售失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.panel {
  border: none;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.82);
}

.panel-header h3 {
  margin: 0;
}

.panel-header p {
  margin: 8px 0 0;
  color: #64748b;
}

.ticket-form {
  max-width: 420px;
}
</style>
