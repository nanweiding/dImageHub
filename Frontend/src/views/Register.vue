<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-header">
        <i class="fas fa-user-plus auth-icon"></i>
        <h1>创建账户</h1>
        <p>加入我们，开始使用图片托管服务</p>
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <label><i class="fas fa-user"></i> 用户名</label>
          <input v-model="form.username" type="text" placeholder="输入用户名（至少3字符）" required />
        </div>
        <div class="form-group">
          <label><i class="fas fa-lock"></i> 密码</label>
          <input v-model="form.password" type="password" placeholder="输入密码（至少6字符）" required />
        </div>
        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          <i v-if="loading" class="fas fa-spinner fa-spin"></i>
          <span v-else><i class="fas fa-user-plus"></i> 注册</span>
        </button>
        <p v-if="error" class="error"><i class="fas fa-exclamation-circle"></i> {{ error }}</p>
        <p v-if="success" class="success"><i class="fas fa-check-circle"></i> {{ success }}</p>
      </form>

      <div class="auth-footer">
        <p>已有账户？ <router-link to="/login">立即登录</router-link></p>
      </div>
    </div>
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
      success.value = '注册成功！正在跳转到登录页面...'
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
.auth-page {
  min-height: calc(100vh - 80px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.auth-card {
  background: var(--body-empty);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 2.5rem;
  width: 100%;
  max-width: 400px;
}

.auth-header { text-align: center; margin-bottom: 2rem; }

.auth-icon { font-size: 3rem; color: var(--color-accent); margin-bottom: 1rem; }

.auth-header h1 { font-size: 1.5rem; margin-bottom: 0.5rem; }

.auth-header p { color: var(--body-text-subtle); font-size: 0.9rem; }

.auth-form { display: flex; flex-direction: column; gap: 1.25rem; }

.form-group label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  margin-bottom: 0.5rem;
  color: var(--body-text);
  font-size: 0.9rem;
  font-weight: 500;
}

.form-group label .fa-user, .form-group label .fa-lock { color: var(--color-accent); }

.form-group input {
  width: 100%;
  padding: 0.875rem 1rem;
  background: var(--input-bg);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  color: var(--body-text);
  font-size: 1rem;
  transition: border-color 0.2s;
}

.form-group input:focus {
  outline: none;
  border-color: var(--color-accent);
}

.form-group input::placeholder { color: var(--input-placeholder); }

.btn-block { width: 100%; padding: 0.875rem; font-size: 1rem; margin-top: 0.5rem; }

.error {
  color: #ff6b6b;
  text-align: center;
  font-size: 0.9rem;
  padding: 0.75rem;
  background: rgba(255, 107, 107, 0.1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.success {
  color: var(--color-accent);
  text-align: center;
  font-size: 0.9rem;
  padding: 0.75rem;
  background: rgba(0, 212, 170, 0.1);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
}

.auth-footer {
  margin-top: 1.5rem;
  text-align: center;
  color: var(--body-text-subtle);
  font-size: 0.9rem;
}

.auth-footer a { color: var(--color-accent); text-decoration: none; }
.auth-footer a:hover { text-decoration: underline; }
</style>
