<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-header">
        <div class="auth-icon">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M15 3h4a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-4"/>
            <polyline points="10 17 15 12 10 7"/>
            <line x1="15" y1="12" x2="3" y2="12"/>
          </svg>
        </div>
        <h1>登录</h1>
        <p>欢迎回来！请输入您的账户信息</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <div class="form-group">
          <label>用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="输入用户名"
            required
          />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="输入密码"
            required
          />
        </div>
        <p v-if="error" class="error">
          <i class="fas fa-exclamation-circle"></i>
          {{ error }}
        </p>
        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          <i v-if="loading" class="fas fa-spinner fa-spin"></i>
          <span v-else>登录</span>
        </button>
      </form>

      <div class="auth-footer">
        <p>还没有账户？ <router-link to="/register">立即注册</router-link></p>
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

const handleLogin = async () => {
  if (form.value.username.length < 3 || form.value.password.length < 6) {
    error.value = '用户名至少3字符，密码至少6字符'
    return
  }
  loading.value = true
  error.value = ''
  try {
    const response = await authAPI.login(form.value.username, form.value.password)
    if (response.data.success) {
      localStorage.setItem('userId', response.data.user.id)
      localStorage.setItem('username', response.data.user.username)
      router.push('/')
    } else {
      error.value = response.data.message
    }
  } catch (e) {
    error.value = '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-page {
  background: linear-gradient(135deg, #1E3A5F 0%, #3B7DDD 100%);
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 60px 24px;
}

.auth-container {
  background: var(--bg-card);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
  width: 100%;
  max-width: 400px;
}

.auth-header {
  text-align: center;
  margin-bottom: 40px;
}

.auth-icon {
  background: linear-gradient(135deg, rgba(59, 125, 221, 0.15) 0%, rgba(74, 158, 255, 0.15) 100%);
  color: var(--color-secondary);
}

.auth-header h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 8px;
  letter-spacing: -0.02em;
}

.auth-header p {
  color: var(--text-secondary);
  font-size: 1rem;
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--text-secondary);
  font-size: 0.85rem;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  color: var(--text-primary);
  font-size: 1rem;
  transition: all 0.3s;
}

.form-group input:focus {
  outline: none;
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(41, 151, 255, 0.15);
}

.form-group input::placeholder { color: var(--text-tertiary); }

.btn-block {
  width: 100%;
  padding: 16px;
  font-size: 1.1rem;
  margin-top: 8px;
}

.error {
  color: var(--error);
  text-align: center;
  font-size: 0.9rem;
  padding: 14px;
  background: rgba(255, 69, 58, 0.1);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.auth-footer {
  margin-top: 32px;
  text-align: center;
  color: var(--text-secondary);
  font-size: 0.9rem;
}

.auth-footer a {
  color: var(--accent);
  text-decoration: none;
  font-weight: 500;
}

.auth-footer a:hover { text-decoration: underline; }
</style>