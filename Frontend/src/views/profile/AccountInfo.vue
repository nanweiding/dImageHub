<template>
  <div class="account-info-page">
    <div class="info-grid">
      <!-- Left Column -->
      <div class="main-column">
        <!-- Account Info Section -->
        <div class="section-card">
          <h2 class="section-title">账号信息</h2>
          <div class="avatar-upload">
            <div class="avatar-preview" @click="triggerFileInput">
              <img v-if="formData.avatar" :src="formData.avatar" alt="avatar" />
              <i v-else class="fas fa-user-circle"></i>
            </div>
            <p class="avatar-hint">点击头像更换图片</p>
            <input
              type="file"
              ref="fileInput"
              accept="image/*"
              @change="handleAvatarChange"
              style="display: none"
            />
          </div>

          <div class="form-group">
            <label>昵称</label>
            <input
              type="text"
              v-model="formData.nickname"
              placeholder="请输入昵称"
              :class="{ 'error': errors.nickname }"
            />
            <span v-if="errors.nickname" class="error-text">{{ errors.nickname }}</span>
          </div>

          <div class="form-group">
            <label>签名</label>
            <textarea
              v-model="formData.bio"
              placeholder="介绍一下自己"
              maxlength="200"
              :class="{ 'error': errors.bio }"
            ></textarea>
            <div class="char-count">{{ formData.bio.length }}/200</div>
            <span v-if="errors.bio" class="error-text">{{ errors.bio }}</span>
          </div>
        </div>

        <!-- Account Status Section -->
        <div class="section-card">
          <h2 class="section-title">账号状态</h2>
          <div class="info-row">
            <span class="info-label">状态</span>
            <span :class="['status-badge', userInfo.status]">
              <i :class="statusIcon"></i>
              {{ statusText }}
            </span>
          </div>
          <div class="info-row">
            <span class="info-label">会员等级</span>
            <span class="info-value">{{ memberLevelText }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">注册时间</span>
            <span class="info-value">{{ formatDate(userInfo.createdAt) }}</span>
          </div>
        </div>
      </div>

      <!-- Right Column -->
      <div class="side-column">
        <div class="section-card">
          <button
            class="btn-save"
            @click="saveProfile"
            :disabled="saving"
          >
            {{ saving ? '保存中...' : '保存修改' }}
          </button>

          <div v-if="message.text" :class="['message', message.type]">
            {{ message.text }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const userId = localStorage.getItem('userId') || ''

const formData = ref({
  username: '',
  avatar: '',
  nickname: '',
  bio: '',
  email: '',
  memberLevel: 'free',
  status: 'normal',
  createdAt: ''
})

const userInfo = ref({
  username: '',
  avatar: '',
  bio: '',
  email: '',
  memberLevel: 'free',
  status: 'normal',
  createdAt: ''
})

const errors = ref({
  nickname: '',
  bio: ''
})

const saving = ref(false)
const message = ref({ type: '', text: '' })
const fileInput = ref(null)

const statusText = computed(() => {
  const statusMap = {
    normal: '正常',
    frozen: '已冻结',
    expired: '已过期'
  }
  return statusMap[userInfo.value.status] || '正常'
})

const statusIcon = computed(() => {
  const iconMap = {
    normal: 'fas fa-check-circle',
    frozen: 'fas fa-snowflake',
    expired: 'fas fa-clock'
  }
  return iconMap[userInfo.value.status] || 'fas fa-check-circle'
})

const memberLevelText = computed(() => {
  const levelMap = {
    free: '免费用户',
    basic: '基础用户',
    premium: '高级用户',
    vip: 'VIP会员'
  }
  return levelMap[userInfo.value.memberLevel] || '免费用户'
})

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const handleAvatarChange = (event) => {
  const file = event.target.files?.[0]
  if (!file) return

  // Validate file type
  if (!file.type.startsWith('image/')) {
    errors.value.avatar = '请选择图片文件'
    return
  }

  // Create preview
  const reader = new FileReader()
  reader.onload = (e) => {
    formData.value.avatar = e.target?.result || ''
  }
  reader.readAsDataURL(file)
}

const validateForm = () => {
  let isValid = true
  errors.value = { nickname: '', bio: '' }

  // Validate nickname (3-20 chars)
  const nickname = formData.value.nickname?.trim() || ''
  if (!nickname) {
    errors.value.nickname = '请输入昵称'
    isValid = false
  } else if (nickname.length < 3 || nickname.length > 20) {
    errors.value.nickname = '昵称长度应为3-20个字符'
    isValid = false
  }

  // Validate bio (max 200 chars)
  const bio = formData.value.bio || ''
  if (bio.length > 200) {
    errors.value.bio = '签名不能超过200个字符'
    isValid = false
  }

  return isValid
}

const saveProfile = async () => {
  if (!validateForm()) return

  saving.value = true
  message.value = { type: '', text: '' }

  try {
    // Simulate API call for now
    await new Promise(resolve => setTimeout(resolve, 800))

    // Mock success - in real implementation would call:
    // await userAPI.updateUserInfo(userId, formData.value)

    message.value = {
      type: 'success',
      text: '保存成功！'
    }

    // Update localStorage
    localStorage.setItem('username', formData.value.nickname)
    if (formData.value.avatar) {
      localStorage.setItem('avatar', formData.value.avatar)
    }

    // Clear success message after 3 seconds
    setTimeout(() => {
      message.value = { type: '', text: '' }
    }, 3000)
  } catch (error) {
    message.value = {
      type: 'error',
      text: '保存失败，请重试'
    }
  } finally {
    saving.value = false
  }
}

const fetchUserInfo = async () => {
  if (!userId) {
    // Mock data for development
    userInfo.value = {
      username: localStorage.getItem('username') || '用户',
      avatar: localStorage.getItem('avatar') || '',
      bio: '这个人很懒，什么都没写',
      email: 'user@example.com',
      memberLevel: 'free',
      status: 'normal',
      createdAt: '2024-01-15'
    }
    formData.value = { ...userInfo.value, nickname: userInfo.value.username }
    return
  }

  try {
    // Mock API call - in real implementation:
    // const response = await userAPI.getUserInfo(userId)
    // userInfo.value = response.data

    // Mock response for development
    userInfo.value = {
      username: localStorage.getItem('username') || '用户',
      avatar: localStorage.getItem('avatar') || '',
      bio: '这个人很懒，什么都没写',
      email: 'user@example.com',
      memberLevel: 'free',
      status: 'normal',
      createdAt: '2024-01-15'
    }
    formData.value = {
      ...userInfo.value,
      nickname: userInfo.value.username
    }
  } catch (error) {
    console.warn('Failed to fetch user info')
  }
}

onMounted(() => {
  fetchUserInfo()
})
</script>

<style scoped>
.account-info-page {
  max-width: 1200px;
  margin: 0 auto;
}

.info-grid {
  display: flex;
  gap: 32px;
}

.main-column {
  flex: 1;
}

.side-column {
  width: 320px;
}

.section-card {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  border: 1px solid #E2E8F0;
  margin-bottom: 24px;
}

.section-title {
  font-size: 1.1rem;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #E2E8F0;
}

.avatar-upload {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
}

.avatar-preview {
  width: 96px;
  height: 96px;
  border-radius: 50%;
  overflow: hidden;
  cursor: pointer;
  position: relative;
  background: #F5F7FA;
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-preview i {
  font-size: 48px;
  color: #94A3B8;
}

.avatar-hint {
  font-size: 0.8rem;
  color: #64748B;
  margin: 0;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #64748B;
  font-size: 0.85rem;
  font-weight: 500;
}

.form-group input,
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3B7DDD;
  box-shadow: 0 0 0 3px rgba(59, 125, 221, 0.15);
}

.form-group textarea {
  resize: vertical;
  min-height: 100px;
}

.form-group input.error,
.form-group textarea.error {
  border-color: #EF4444;
}

.error-text {
  display: block;
  color: #EF4444;
  font-size: 0.8rem;
  margin-top: 4px;
}

.char-count {
  text-align: right;
  font-size: 0.8rem;
  color: #94A3B8;
  margin-top: 4px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.status-badge.normal {
  background: rgba(34, 197, 94, 0.1);
  color: #22C55E;
}

.status-badge.frozen {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
}

.status-badge.expired {
  background: rgba(245, 158, 11, 0.1);
  color: #F59E0B;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 12px 0;
  border-bottom: 1px solid #F5F7FA;
}

.info-row:last-child {
  border-bottom: none;
}

.info-label {
  color: #64748B;
  font-size: 0.9rem;
}

.info-value {
  color: #1E293B;
  font-size: 0.9rem;
  font-weight: 500;
}

.btn-save {
  width: 100%;
  padding: 14px;
  background: #3B7DDD;
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-save:hover:not(:disabled) {
  background: #2563EB;
  transform: translateY(-1px);
}

.btn-save:disabled {
  background: #94A3B8;
  cursor: not-allowed;
}

.message {
  padding: 14px;
  border-radius: 12px;
  margin-top: 16px;
  font-size: 0.9rem;
}

.message.success {
  background: rgba(34, 197, 94, 0.1);
  color: #22C55E;
}

.message.error {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
}
</style>
