<template>
  <div class="auth-container">
    <h2>注册</h2>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label>用户名</label>
        <input v-model="form.username" type="text" required />
      </div>
      <div class="form-group">
        <label>密码</label>
        <input v-model="form.password" type="password" required />
      </div>
      <button type="submit" :disabled="loading">{{ loading ? '注册中...' : '注册' }}</button>
      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="success">{{ success }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')
const success = ref('')

const handleRegister = async () => {
  if (form.value.username.length < 3 || form.value.password.length < 6) {
    error.value = '用户名至少3字符，密码至少6字符'
    return
  }
  loading.value = true
  error.value = ''
  success.value = ''
  try {
    const response = await authAPI.register(form.value.username, form.value.password)
    if (response.data.success) {
      success.value = '注册成功，即将跳转到登录页面...'
      setTimeout(() => router.push('/login'), 1500)
    } else {
      error.value = response.data.message
    }
  } catch (e) {
    error.value = '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container { max-width: 400px; margin: 0 auto; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; }
.form-group input { width: 100%; padding: 0.5rem; font-size: 1rem; }
button { width: 100%; padding: 0.75rem; background: #1890ff; color: white; border: none; cursor: pointer; }
.error { color: red; margin-top: 1rem; text-align: center; }
.success { color: green; margin-top: 1rem; text-align: center; }
</style>
