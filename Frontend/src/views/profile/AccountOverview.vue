<template>
  <div class="overview-container">
    <div class="main-card">
      <div class="card-row">
        <!-- Left Column: Avatar Section -->
        <div class="avatar-section">
          <div class="avatar-wrapper" @click="goToAccountEdit">
            <img v-if="userInfo.avatar" :src="userInfo.avatar" alt="avatar" />
            <i v-else class="fas fa-user-circle"></i>
            <div class="avatar-overlay">
              <i class="fas fa-camera"></i>
            </div>
          </div>
          <div class="user-info">
            <div class="username">{{ userInfo.username || '未登录' }}</div>
            <span class="member-badge">{{ memberLevelText }}</span>
          </div>
          <button class="edit-btn" @click="goToAccountEdit">
            <i class="fas fa-edit"></i> 编辑资料
          </button>
        </div>

        <!-- Right Column: Stats Section -->
        <div class="stats-section">
          <div class="stats-grid">
            <!-- Total Images Card -->
            <div class="stat-card">
              <div class="stat-icon blue">
                <i class="fas fa-images"></i>
              </div>
              <div class="stat-value">{{ storageStats.totalImages || 0 }}</div>
              <div class="stat-label">图片总数</div>
            </div>

            <!-- Storage Used Card -->
            <div class="stat-card">
              <div class="stat-icon green">
                <i class="fas fa-database"></i>
              </div>
              <div class="stat-value">{{ formattedStorage }}</div>
              <div class="stat-label">已用存储</div>
            </div>

            <!-- Upload Count Today Card -->
            <div class="stat-card">
              <div class="stat-icon orange">
                <i class="fas fa-upload"></i>
              </div>
              <div class="stat-value">{{ storageStats.todayUploads || 0 }}</div>
              <div class="stat-label">今日上传</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Quick Actions -->
      <div class="quick-actions">
        <h3>快捷操作</h3>
        <div class="action-buttons">
          <router-link to="/profile/images" class="action-btn">
            <i class="fas fa-cloud-upload-alt"></i> 上传图片
          </router-link>
          <router-link to="/profile/images" class="action-btn">
            <i class="fas fa-images"></i> 图片管理
          </router-link>
          <router-link to="/profile/security" class="action-btn">
            <i class="fas fa-shield-alt"></i> 安全设置
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api, { userAPI } from '../../api'

const router = useRouter()

const userId = localStorage.getItem('userId') || ''

const userInfo = ref({
  username: '',
  avatar: '',
  memberLevel: 'free'
})

const storageStats = ref({
  totalImages: 0,
  usedStorage: 0,
  todayUploads: 0,
  storageLimit: 10
})

const loading = ref(true)
const error = ref(null)

const memberLevelText = computed(() => {
  const levelMap = {
    free: '免费用户',
    basic: '基础用户',
    premium: '高级用户',
    vip: 'VIP会员'
  }
  return levelMap[userInfo.value.memberLevel] || '免费用户'
})

const formattedStorage = computed(() => {
  const gb = storageStats.value.usedStorage || 0
  return `${gb.toFixed(2)} GB`
})

const fetchUserInfo = async () => {
  if (!userId) return

  try {
    const response = await userAPI.getUserInfo(userId)
    userInfo.value = response.data
  } catch (err) {
    console.warn('Failed to fetch user info, using mock data')
    // Mock data for development
    userInfo.value = {
      username: localStorage.getItem('username') || '用户',
      avatar: localStorage.getItem('avatar') || '',
      memberLevel: 'free'
    }
  }
}

const fetchStorageStats = async () => {
  if (!userId) return

  try {
    const response = await storageAPI.getStorageStats(userId)
    storageStats.value = response.data
  } catch (err) {
    console.warn('Failed to fetch storage stats, using mock data')
    // Mock data for development
    storageStats.value = {
      totalImages: 42,
      usedStorage: 1.58,
      todayUploads: 3,
      storageLimit: 10
    }
  }
}

const goToAccountEdit = () => {
  router.push('/profile/account')
}

onMounted(async () => {
  try {
    await Promise.all([fetchUserInfo(), fetchStorageStats()])
  } catch (err) {
    error.value = '加载数据失败'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.overview-container {
  max-width: 1200px;
  margin: 0 auto;
}

.main-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  border: 1px solid #E2E8F0;
}

.card-row {
  display: flex;
  gap: 32px;
}

.avatar-section {
  width: 30%;
  text-align: center;
  padding: 20px;
  border-right: 1px solid #E2E8F0;
}

.avatar-wrapper {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  margin: 0 auto 16px;
  position: relative;
  cursor: pointer;
  overflow: hidden;
}

.avatar-wrapper img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-wrapper i {
  font-size: 48px;
  color: #94A3B8;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.avatar-wrapper:hover .avatar-overlay {
  opacity: 1;
}

.avatar-overlay i {
  color: #fff;
  font-size: 20px;
}

.user-info {
  margin-top: 16px;
}

.username {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 8px;
}

.member-badge {
  display: inline-block;
  padding: 4px 12px;
  background: rgba(59, 125, 221, 0.1);
  color: #3B7DDD;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
}

.edit-btn {
  margin-top: 16px;
  padding: 10px 20px;
  background: #3B7DDD;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: background 0.3s;
}

.edit-btn:hover {
  background: #1E3A5F;
}

.edit-btn i {
  margin-right: 6px;
}

.stats-section {
  width: 70%;
  padding: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.stat-card {
  background: #F5F7FA;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  margin: 0 auto 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-icon.blue {
  background: rgba(59, 125, 221, 0.15);
  color: #3B7DDD;
}

.stat-icon.green {
  background: rgba(34, 197, 94, 0.15);
  color: #22C55E;
}

.stat-icon.orange {
  background: rgba(245, 158, 11, 0.15);
  color: #F59E0B;
}

.stat-icon i {
  font-size: 20px;
}

.stat-value {
  font-size: 1.75rem;
  font-weight: 700;
  color: #1E293B;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.85rem;
  color: #64748B;
}

.quick-actions {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #E2E8F0;
}

.quick-actions h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 16px;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.action-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: #F5F7FA;
  border: 1px solid #E2E8F0;
  border-radius: 8px;
  color: #1E293B;
  text-decoration: none;
  font-size: 0.9rem;
  transition: all 0.3s;
}

.action-btn:hover {
  border-color: #3B7DDD;
  color: #3B7DDD;
}

.action-btn i {
  color: #3B7DDD;
}
</style>
