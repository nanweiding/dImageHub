<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-brand">图片托管平台</div>
      <div class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link v-if="!isLoggedIn" to="/login">登录</router-link>
        <router-link v-if="!isLoggedIn" to="/register">注册</router-link>
        <span v-if="isLoggedIn">{{ username }}</span>
        <button v-if="isLoggedIn" @click="logout">退出</button>
      </div>
    </nav>
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
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: #1890ff;
  color: white;
}
.nav-brand { font-size: 1.5rem; font-weight: bold; }
.nav-links { display: flex; gap: 1rem; align-items: center; }
.nav-links a { color: white; text-decoration: none; }
.nav-links button { padding: 0.5rem 1rem; cursor: pointer; }
.main-content { padding: 2rem; max-width: 1200px; margin: 0 auto; }
</style>
