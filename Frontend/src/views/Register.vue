<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-header">
        <div class="auth-icon">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/>
            <circle cx="8.5" cy="7" r="4"/>
            <line x1="20" y1="8" x2="20" y2="14"/>
            <line x1="23" y1="11" x2="17" y2="11"/>
          </svg>
        </div>
        <h1>创建账户</h1>
        <p>加入我们，开始使用图片托管服务</p>
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <div class="form-group">
          <label>用户名</label>
          <input
            v-model="form.username"
            type="text"
            placeholder="输入用户名（至少3字符）"
            required
          />
        </div>
        <div class="form-group">
          <label>密码</label>
          <input
            v-model="form.password"
            type="password"
            placeholder="输入密码（至少6字符）"
            required
          />
        </div>
        <p v-if="error" class="error">
          <i class="fas fa-exclamation-circle"></i>
          {{ error }}
        </p>
        <p v-if="success" class="success">
          <i class="fas fa-check-circle"></i>
          {{ success }}
        </p>
        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          <i v-if="loading" class="fas fa-spinner fa-spin"></i>
          <span v-else>注册</span>
        </button>
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

.success {
  color: var(--success);
  text-align: center;
  font-size: 0.9rem;
  padding: 14px;
  background: rgba(48, 209, 88, 0.1);
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