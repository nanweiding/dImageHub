<template>
  <div id="app">
    <header class="top-nav">
      <div class="nav-container">
        <router-link to="/" class="brand">
          <span class="brand-icon">
            <svg width="28" height="28" viewBox="0 0 28 28" fill="none">
              <defs>
                <linearGradient id="iconGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#0071E3"/>
                  <stop offset="100%" stop-color="#00D4AA"/>
                </linearGradient>
              </defs>
              <rect x="2" y="2" width="24" height="24" rx="6" fill="url(#iconGrad)"/>
              <path d="M8 18L11 13L14 16L18 10L20 12" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="10" cy="9" r="2" fill="white"/>
            </svg>
          </span>
          <span class="brand-text">ImgHub</span>
        </router-link>

        <nav class="nav-links">
          <router-link to="/" class="nav-link">首页</router-link>
          <router-link v-if="!isLoggedIn" to="/login" class="nav-link">登录</router-link>
          <router-link v-if="!isLoggedIn" to="/register" class="nav-link">注册</router-link>
          <router-link v-if="isLoggedIn" to="/admin" class="nav-link admin-link">
            <i class="fas fa-cog"></i> 管理
          </router-link>
          <div v-if="isLoggedIn" class="user-menu">
            <span class="user-name">
              <i class="fas fa-user-circle"></i>
              {{ username }}
            </span>
            <button @click="logout" class="btn-logout">
              <i class="fas fa-sign-out-alt"></i>
              退出
            </button>
          </div>
        </nav>
      </div>
    </header>

    <main class="main-content">
      <router-view></router-view>
    </main>

    <!-- Image Viewer Modal -->
    <div v-if="viewingImage" class="image-viewer" @click.self="closeViewer">
      <button class="viewer-close" @click="closeViewer">
        <i class="fas fa-times"></i>
      </button>

      <div class="viewer-content">
        <img
          :src="viewingImage.url"
          :alt="viewingImage.filename || 'Preview'"
          class="viewer-image"
          :style="previewStyle"
          @wheel="handleWheel"
          ref="viewerImgRef"
          @load="onImageLoad"
        />

        <!-- Image Info Panel -->
        <div v-if="imageInfo.naturalWidth" class="viewer-info">
          <div class="info-item">
            <span class="info-label">尺寸</span>
            <span class="info-value">{{ imageInfo.naturalWidth }} x {{ imageInfo.naturalHeight }}</span>
          </div>
          <div class="info-item" v-if="imageInfo.fileSize">
            <span class="info-label">大小</span>
            <span class="info-value">{{ formatSize(imageInfo.fileSize) }}</span>
          </div>
          <div class="info-item" v-if="imageInfo.contentType">
            <span class="info-label">格式</span>
            <span class="info-value">{{ imageInfo.contentType.split('/')[1]?.toUpperCase() || imageInfo.contentType }}</span>
          </div>
          <div class="info-item" v-if="imageInfo.uploadTime">
            <span class="info-label">上传时间</span>
            <span class="info-value">{{ formatDate(imageInfo.uploadTime) }}</span>
          </div>
        </div>
      </div>

      <!-- Controls -->
      <div class="viewer-controls">
        <button class="control-btn" @click="zoomOut" title="缩小">
          <i class="fas fa-search-minus"></i>
        </button>
        <span class="zoom-level">{{ Math.round(scale * 100) }}%</span>
        <button class="control-btn" @click="zoomIn" title="放大">
          <i class="fas fa-search-plus"></i>
        </button>
        <div class="control-divider"></div>
        <button class="control-btn" @click="rotateLeft" title="逆时针旋转">
          <i class="fas fa-undo"></i>
        </button>
        <button class="control-btn" @click="rotateRight" title="顺时针旋转">
          <i class="fas fa-redo"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, provide } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '')
const isLoggedIn = computed(() => !!localStorage.getItem('userId'))
const viewingImage = ref(null)
const scale = ref(1)
const rotation = ref(0)
const imageInfo = ref({})
const viewerImgRef = ref(null)

provide('viewImage', (imageData) => {
  viewingImage.value = imageData
  scale.value = 1
  rotation.value = 0
  imageInfo.value = {}
  if (imageData && imageData.fileSize) {
    imageInfo.value = {
      fileSize: imageData.fileSize,
      contentType: imageData.contentType,
      uploadTime: imageData.uploadTime
    }
  }
})
provide('closeViewer', () => { viewingImage.value = null })

const logout = () => {
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  username.value = ''
  router.push('/')
}

const closeViewer = () => {
  viewingImage.value = null
  scale.value = 1
  rotation.value = 0
}

const zoomIn = () => { scale.value = Math.min(scale.value + 0.25, 5) }
const zoomOut = () => { scale.value = Math.max(scale.value - 0.25, 0.25) }
const rotateLeft = () => { rotation.value -= 90 }
const rotateRight = () => { rotation.value += 90 }

const handleWheel = (e) => {
  e.preventDefault()
  if (e.deltaY < 0) {
    zoomIn()
  } else {
    zoomOut()
  }
}

const previewStyle = computed(() => ({
  transform: `scale(${scale.value}) rotate(${rotation.value}deg)`,
  transition: 'transform 0.3s ease'
}))

const onImageLoad = (e) => {
  if (e.target) {
    imageInfo.value = {
      ...imageInfo.value,
      naturalWidth: e.target.naturalWidth,
      naturalHeight: e.target.naturalHeight
    }
  }
}

const formatSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style>
@import url('https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

:root {
  --color-primary: #1E3A5F;
  --color-secondary: #3B7DDD;
  --color-accent: #4A9EFF;
  --color-hover: #2563EB;
  --bg-main: #F5F7FA;
  --bg-secondary: #F5F7FA;
  --bg-card: #FFFFFF;
  --bg-elevated: #FFFFFF;
  --border-color: #E2E8F0;
  --border-subtle: #E2E8F0;
  --text-primary: #1E293B;
  --text-secondary: #64748B;
  --text-tertiary: #94A3B8;
  --accent: #3B7DDD;
  --accent-hover: #2563EB;
  --success: #22C55E;
  --error: #EF4444;
  --gradient-accent: linear-gradient(135deg, #3B7DDD 0%, #4A9EFF 100%);
}

body {
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'SF Pro Display', 'Segoe UI', Roboto, sans-serif;
  background-color: var(--bg-secondary);
  color: var(--text-primary);
  min-height: 100vh;
  line-height: 1.5;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

/* Navigation */
.top-nav {
  background: var(--color-primary);
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
  border-bottom: none;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 980px;
  margin: 0 auto;
  padding: 0 22px;
  height: 48px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.brand {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-primary);
  text-decoration: none;
  font-weight: 600;
  font-size: 1.1rem;
  transition: opacity 0.3s;
}

.brand:hover { opacity: 0.85; }

.brand-icon {
  display: flex;
  align-items: center;
}

.brand-text {
  letter-spacing: -0.02em;
  background: linear-gradient(135deg, #fff 0%, rgba(255,255,255,0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-links {
  display: flex;
  align-items: center;
  gap: 32px;
}

.nav-link {
  color: rgba(255, 255, 255, 0.8);
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 400;
  transition: color 0.3s;
}

.nav-link:hover { color: #fff; }
.nav-link.router-link-active { color: #fff; }
.admin-link {
  color: var(--accent) !important;
}
.admin-link i { margin-right: 4px; }

.user-menu {
  display: flex;
  align-items: center;
  gap: 16px;
}

.user-name {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.9);
  font-size: 0.85rem;
}

.user-name i { font-size: 1rem; }

.btn-logout {
  display: flex;
  align-items: center;
  gap: 6px;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  padding: 6px 14px;
  border-radius: 980px;
  font-size: 0.8rem;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-logout:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}

/* Main Content */
.main-content {
  flex: 1;
  padding-top: 48px;
}

/* Buttons */
.btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  border-radius: 980px;
  font-size: 0.95rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
  border: none;
  text-decoration: none;
}

.btn-primary {
  background: var(--accent);
  color: #fff;
}

.btn-primary:hover {
  background: var(--accent-hover);
  transform: translateY(-1px);
}

.btn-secondary {
  background: var(--bg-elevated);
  color: var(--text-primary);
  border: 1px solid var(--border);
}

.btn-secondary:hover {
  border-color: var(--border-subtle);
  background: var(--bg-card);
}

/* Cards */
.card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 18px;
  padding: 24px;
}

/* Form Elements */
.form-group { margin-bottom: 20px; }

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--text-secondary);
  font-size: 0.85rem;
  font-weight: 500;
}

.form-group input {
  width: 100%;
  padding: 14px 16px;
  background: var(--bg-secondary);
  border: 1px solid var(--border);
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

/* Messages */
.error {
  color: var(--error);
  text-align: center;
  font-size: 0.9rem;
  padding: 14px;
  background: rgba(255, 69, 58, 0.1);
  border-radius: 12px;
}

.success {
  color: var(--success);
  text-align: center;
  font-size: 0.9rem;
  padding: 14px;
  background: rgba(48, 209, 88, 0.1);
  border-radius: 12px;
}

/* Image Viewer Modal */
.image-viewer {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(30, 58, 95, 0.95);
  backdrop-filter: blur(30px);
  -webkit-backdrop-filter: blur(30px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.viewer-close {
  position: absolute;
  top: 24px;
  right: 24px;
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 1.2rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.viewer-close:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: scale(1.1);
}

.viewer-image {
  max-width: 90vw;
  max-height: 75vh;
  object-fit: contain;
  border-radius: 12px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.15);
  cursor: grab;
}

.viewer-image:active {
  cursor: grabbing;
}

.viewer-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

/* Image Info Panel */
.viewer-info {
  display: flex;
  gap: 24px;
  background: rgba(30, 58, 95, 0.9);
  padding: 12px 24px;
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.info-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.info-label {
  font-size: 0.75rem;
  color: var(--text-tertiary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 0.9rem;
  color: var(--text-primary);
  font-weight: 500;
}

/* Viewer Controls */
.viewer-controls {
  position: absolute;
  bottom: 24px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  align-items: center;
  gap: 12px;
  background: rgba(30, 58, 95, 0.9);
  padding: 12px 24px;
  border-radius: 980px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.control-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #fff;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s ease;
}

.control-btn:hover {
  background: var(--color-secondary);
  border-color: var(--color-secondary);
  color: white;
  transform: scale(1.1);
}

.zoom-level {
  min-width: 50px;
  text-align: center;
  font-size: 0.85rem;
  font-weight: 500;
  color: var(--text-primary);
}

.control-divider {
  width: 1px;
  height: 24px;
  background: var(--border);
}
</style>