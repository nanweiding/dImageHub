<template>
  <div id="app">
    <header class="top-bar">
      <div class="content-width">
        <div class="logo">
          <span class="logo-icon">📷</span>
          <span class="logo-text">ImgHub</span>
        </div>

        <nav class="nav-links">
          <router-link to="/">首页</router-link>
          <router-link v-if="!isLoggedIn" to="/login">登录</router-link>
          <router-link v-if="!isLoggedIn" to="/register">注册</router-link>
          <span v-if="isLoggedIn" class="user-info">
            <i class="fas fa-user"></i> {{ username }}
          </span>
          <button v-if="isLoggedIn" @click="logout" class="btn-logout">
            <i class="fas fa-sign-out-alt"></i> 退出
          </button>
        </nav>
      </div>
    </header>

    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '')
const isLoggedIn = computed(() => !!localStorage.getItem('userId'))

const logout = () => {
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  username.value = ''
  router.push('/login')
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --body-background: #0f0f13;
  --body-empty: #1a1a21;
  --body-text: #e0e0e6;
  --body-text-subtle: #8888a0;
  --color-accent: #00d4aa;
  --color-accent-hover: #00f5c4;
  --button-default-bg: #1e1e28;
  --button-default-text: #c0c0d0;
  --input-bg: #16161e;
  --input-placeholder: #5a5a70;
  --top-bar-bg: #0a0a10;
  --border-color: #2a2a38;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background-color: var(--body-background);
  color: var(--body-text);
  min-height: 100vh;
  line-height: 1.6;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.top-bar {
  background: var(--top-bar-bg);
  border-bottom: 1px solid var(--border-color);
  padding: 0.75rem 0;
  position: sticky;
  top: 0;
  z-index: 100;
}

.content-width {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: 1.4rem;
  font-weight: 700;
  color: var(--color-accent);
  text-decoration: none;
}

.logo-icon { font-size: 1.6rem; }

.nav-links {
  display: flex;
  align-items: center;
  gap: 1.5rem;
}

.nav-links a {
  color: var(--body-text);
  text-decoration: none;
  font-size: 0.95rem;
  transition: color 0.2s;
}

.nav-links a:hover { color: var(--color-accent); }

.user-info {
  color: var(--body-text-subtle);
  font-size: 0.9rem;
}

.btn-logout {
  background: transparent;
  border: 1px solid var(--border-color);
  color: var(--body-text-subtle);
  padding: 0.4rem 1rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.2s;
}

.btn-logout:hover {
  border-color: var(--color-accent);
  color: var(--color-accent);
}

.main-content {
  flex: 1;
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1.5rem;
  width: 100%;
}

.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
  text-decoration: none;
}

.btn-primary {
  background: var(--color-accent);
  color: #000;
}

.btn-primary:hover {
  background: var(--color-accent-hover);
  transform: translateY(-1px);
}

.btn-secondary {
  background: var(--button-default-bg);
  color: var(--body-text);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover {
  border-color: var(--color-accent);
  color: var(--color-accent);
}

.form-group { margin-bottom: 1.25rem; }

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  color: var(--body-text);
  font-size: 0.9rem;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 0.75rem 1rem;
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

.card {
  background: var(--body-empty);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 1.5rem;
}

.error {
  color: #ff6b6b;
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9rem;
}

.success {
  color: var(--color-accent);
  margin-top: 1rem;
  text-align: center;
  font-size: 0.9rem;
}
</style>
