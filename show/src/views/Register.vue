<template>
  <div class="register-container">
    <el-card class="register-card" shadow="never">
      <template #header>
        <div>
          <h2>新用户注册</h2>
          <p>注册时无需填写用户 ID，系统会自动分配 6 位编号。</p>
        </div>
      </template>
      <el-form ref="registerFormRef" :model="registerForm" :rules="rules" label-position="top">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item class="actions">
          <el-button type="primary" @click="handleRegister" :loading="loading">注册</el-button>
          <el-button @click="router.push('/login')">返回登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'
import { ElMessage } from 'element-plus'

const router = useRouter()

const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  const valid = await registerFormRef.value?.validate().catch(() => false)
  if (!valid) {
    return
  }

  loading.value = true
  try {
    const response = await axios.post('/api/user/register', registerForm)

    if (response.data.code === 200) {
      const user = response.data.data
      ElMessage.success(`注册成功，你的用户 ID 是 ${user.userId}`)
      router.push('/login')
    } else {
      ElMessage.error(response.data.message || '注册失败')
    }
  } catch (error) {
    ElMessage.error(error.response?.data?.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-container {
  min-height: 72vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.register-card {
  width: 440px;
  border: none;
  border-radius: 28px;
  background: rgba(255, 255, 255, 0.82);
}

.register-card h2 {
  margin: 0;
  font-size: 24px;
}

.register-card p {
  margin: 8px 0 0;
  color: #64748b;
}

.actions :deep(.el-form-item__content) {
  display: flex;
  gap: 12px;
}
</style>
