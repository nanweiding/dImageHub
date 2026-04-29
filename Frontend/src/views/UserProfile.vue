<template>
  <div class="profile-layout">
    <aside class="profile-sidebar">
      <div class="sidebar-header">
        <div class="user-avatar" @click="goToAccount">
          <img v-if="userInfo.avatar" :src="userInfo.avatar" alt="avatar" />
          <i v-else class="fas fa-user-circle"></i>
        </div>
        <div class="user-name">{{ userInfo.username }}</div>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/profile" class="nav-item" exact>
          <i class="fas fa-chart-line"></i> 账号概览
        </router-link>
        <router-link to="/profile/account" class="nav-item">
          <i class="fas fa-user"></i> 账号信息
        </router-link>
        <router-link to="/profile/images" class="nav-item">
          <i class="fas fa-images"></i> 图片管理
        </router-link>
        <router-link to="/profile/quota" class="nav-item">
          <i class="fas fa-database"></i> 存储配额
        </router-link>
        <router-link to="/profile/logs" class="nav-item">
          <i class="fas fa-list-alt"></i> 上传记录
        </router-link>
        <router-link to="/profile/security" class="nav-item">
          <i class="fas fa-shield-alt"></i> 安全隐私
        </router-link>
        <router-link to="/profile/preferences" class="nav-item">
          <i class="fas fa-cog"></i> 偏好设置
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button @click="logout" class="btn-logout">
          <i class="fas fa-sign-out-alt"></i> 退出登录
        </button>
      </div>
    </aside>
    <main class="profile-content">
      <div class="content-header">
        <h1>{{ pageTitle }}</h1>
      </div>
      <div class="content-body">
        <router-view></router-view>
      </div>
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

const userInfo = ref({
  userId: '',
  username: '',
  avatar: ''
})

onMounted(() => {
  userInfo.value.userId = localStorage.getItem('userId') || ''
  userInfo.value.username = localStorage.getItem('username') || ''
  userInfo.value.avatar = localStorage.getItem('avatar') || ''
})

const pageTitle = computed(() => {
  const routeMap = {
    '/profile': '账号概览',
    '/profile/overview': '账号概览',
    '/profile/account': '账号信息',
    '/profile/images': '图片管理',
    '/profile/quota': '存储配额',
    '/profile/logs': '上传记录',
    '/profile/security': '安全隐私',
    '/profile/preferences': '偏好设置'
  }
  return routeMap[route.path] || '用户中心'
})

const goToAccount = () => {
  router.push('/profile/account')
}

const logout = () => {
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  localStorage.removeItem('avatar')
  router.push('/')
}
</script>

<style scoped>
.profile-layout {
  display: flex;
  min-height: 100vh;
}

.profile-sidebar {
  width: 240px;
  background: #1E3A5F;
  position: fixed;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.sidebar-header {
  padding: 24px;
  text-align: center;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  margin: 0 auto 12px;
  overflow: hidden;
  cursor: pointer;
}

.user-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-avatar i {
  font-size: 48px;
  color: rgba(255, 255, 255, 0.6);
}

.user-name {
  color: #fff;
  font-size: 1rem;
  font-weight: 600;
}

.sidebar-nav {
  flex: 1;
  padding: 16px 0;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 24px;
  color: rgba(255, 255, 255, 0.7);
  text-decoration: none;
  transition: all 0.3s;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

.nav-item.router-link-active {
  color: #fff;
  background: rgba(255, 255, 255, 0.15);
  border-right: 3px solid #3B7DDD;
}

.sidebar-footer {
  padding: 16px 24px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.sidebar-footer .btn-logout {
  width: 100%;
  padding: 12px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  border-radius: 8px;
  cursor: pointer;
}

.sidebar-footer .btn-logout:hover {
  background: rgba(255, 255, 255, 0.2);
}

.profile-content {
  flex: 1;
  margin-left: 240px;
  background: #F5F7FA;
  min-height: 100vh;
}

.content-header {
  background: #fff;
  padding: 20px 32px;
  border-bottom: 1px solid #E2E8F0;
}

.content-header h1 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #1E293B;
  margin: 0;
}

.content-body {
  padding: 32px;
}
</style>
