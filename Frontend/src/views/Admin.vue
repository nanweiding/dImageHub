<template>
  <div class="admin-page">
    <h1><i class="fas fa-cog"></i> 管理后台</h1>

    <!-- Stats Cards -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">
          <i class="fas fa-images"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalImages || 0 }}</span>
          <span class="stat-label">图片总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon users">
          <i class="fas fa-users"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalUsers || 0 }}</span>
          <span class="stat-label">用户总数</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon storage">
          <i class="fas fa-hdd"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ formatSize(stats.totalStorage || 0) }}</span>
          <span class="stat-label">存储空间</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon trash">
          <i class="fas fa-trash-alt"></i>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.totalTrash || 0 }}</span>
          <span class="stat-label">回收站</span>
        </div>
      </div>
    </div>

    <!-- Users Section -->
    <div class="section">
      <h2><i class="fas fa-users"></i> 用户管理</h2>
      <div v-if="loading" class="loading">
        <i class="fas fa-spinner fa-spin"></i> 加载中...
      </div>
      <div v-else class="user-list">
        <div v-for="user in users" :key="user.id" class="user-card">
          <div class="user-info">
            <i class="fas fa-user-circle"></i>
            <span class="username">{{ user.username }}</span>
            <span class="user-role" :class="user.role.toLowerCase()">{{ user.role === 'ADMIN' ? '管理员' : '用户' }}</span>
          </div>
          <div class="user-meta">
            <span>注册于 {{ formatDate(user.createdAt) }}</span>
          </div>
          <button v-if="user.role !== 'ADMIN'" @click="deleteUser(user.id)" class="btn-delete-user">
            <i class="fas fa-trash-alt"></i> 删除
          </button>
        </div>
      </div>
    </div>

    <!-- All Images Section -->
    <div class="section">
      <h2><i class="fas fa-images"></i> 所有图片</h2>
      <div v-if="allImages.length === 0" class="empty-state">
        <i class="far fa-images"></i>
        <p>暂无图片</p>
      </div>
      <div v-else class="image-grid">
        <div v-for="image in allImages" :key="image.id" class="image-card">
          <div class="image-preview">
            <img :src="'/api/images/' + image.id" :alt="image.originalFilename" />
          </div>
          <div class="image-meta">
            <span class="image-name" :title="image.originalFilename">{{ image.originalFilename }}</span>
            <span class="image-size">{{ formatSize(image.fileSize) }}</span>
            <span class="image-user">用户ID: {{ image.userId }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { adminAPI, imageAPI } from '../api'

const loading = ref(false)
const users = ref([])
const allImages = ref([])
const stats = ref({})

const loadData = async () => {
  loading.value = true
  try {
    const [usersRes, statsRes, imagesRes] = await Promise.all([
      adminAPI.getUsers(),
      adminAPI.getStats(),
      imageAPI.getAllImages()
    ])
    if (usersRes.data.success) users.value = usersRes.data.users
    if (statsRes.data.success) stats.value = statsRes.data.stats
    if (imagesRes.data.success) allImages.value = imagesRes.data.images
  } catch (e) {
    console.error('加载数据失败', e)
  } finally {
    loading.value = false
  }
}

const deleteUser = async (userId) => {
  if (!confirm('确定要删除该用户吗？')) return
  try {
    const response = await adminAPI.deleteUser(userId)
    if (response.data.success) {
      loadData()
    }
  } catch (e) {
    alert('删除失败')
  }
}

const formatSize = (bytes) => {
  if (!bytes) return '0 B'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.admin-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
}

.admin-page h1 {
  font-size: 2rem;
  margin-bottom: 32px;
  display: flex;
  align-items: center;
  gap: 12px;
}

.admin-page h1 i { color: var(--accent); }

/* Stats Grid */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 48px;
}

.stat-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 14px;
  background: linear-gradient(135deg, rgba(59, 125, 221, 0.15) 0%, rgba(74, 158, 255, 0.15) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-secondary);
  font-size: 1.5rem;
}

.stat-icon.users {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.15) 0%, rgba(74, 158, 255, 0.15) 100%);
  color: var(--success);
}
.stat-icon.storage {
  background: linear-gradient(135deg, rgba(255, 149, 0, 0.15) 0%, rgba(239, 68, 68, 0.15) 100%);
  color: #ff9500;
}
.stat-icon.trash {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.15) 0%, rgba(255, 149, 0, 0.15) 100%);
  color: var(--error);
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: var(--text-primary);
}

.stat-label {
  font-size: 0.85rem;
  color: var(--text-tertiary);
}

/* Sections */
.section {
  margin-bottom: 48px;
}

.section h2 {
  font-size: 1.5rem;
  margin-bottom: 24px;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section h2 i { color: var(--accent); }

/* User List */
.user-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.user-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 12px;
  padding: 16px 20px;
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.user-info i { font-size: 1.5rem; color: var(--text-tertiary); }

.username {
  font-weight: 600;
  color: var(--text-primary);
}

.user-role {
  font-size: 0.75rem;
  padding: 4px 10px;
  border-radius: 980px;
  background: rgba(59, 125, 221, 0.1);
  color: var(--color-secondary);
}

.user-role.admin {
  background: rgba(255, 149, 0, 0.1);
  color: #ff9500;
}

.user-meta {
  color: var(--text-tertiary);
  font-size: 0.85rem;
}

.btn-delete-user {
  padding: 8px 16px;
  border-radius: 8px;
  border: 1px solid var(--border);
  background: transparent;
  color: var(--error);
  cursor: pointer;
  font-size: 0.85rem;
  transition: all 0.3s;
}

.btn-delete-user:hover {
  background: var(--error);
  color: #fff;
  border-color: var(--error);
}

/* Image Grid */
.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.image-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  overflow: hidden;
}

.image-preview {
  aspect-ratio: 1;
  overflow: hidden;
  background: var(--bg-secondary);
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-meta {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.image-name {
  font-size: 0.85rem;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-size, .image-user {
  font-size: 0.75rem;
  color: var(--text-tertiary);
}

.loading, .empty-state {
  text-align: center;
  padding: 48px;
  color: var(--text-tertiary);
}

.loading i { font-size: 2rem; color: var(--accent); margin-bottom: 16px; display: block; }

.empty-state i {
  font-size: 3rem;
  opacity: 0.5;
  margin-bottom: 16px;
}
</style>