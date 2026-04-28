<template>
  <div class="home">
    <div v-if="!isLoggedIn" class="hero">
      <div class="hero-bg"></div>
      <div class="hero-content">
        <h1>上传与分享您的照片</h1>
        <p class="hero-desc">简单快速的图片托管服务，支持拖拽上传，最大 10MB 图片大小</p>
        <div class="hero-buttons">
          <router-link to="/register" class="btn btn-primary btn-lg">
            <i class="fas fa-cloud-upload-alt"></i> 开始上传
          </router-link>
          <router-link to="/login" class="btn btn-secondary btn-lg">
            登录账户
          </router-link>
        </div>
      </div>
    </div>

    <div v-if="isLoggedIn" class="dashboard">
      <div class="upload-zone" @dragover.prevent @drop.prevent="handleDrop">
        <div class="upload-zone-content">
          <i class="fas fa-cloud-upload-alt upload-icon"></i>
          <p>拖拽图片到这里，或点击选择</p>
          <input type="file" @change="handleFileSelect" accept="image/*" ref="fileInput" class="file-input" />
          <button @click="$refs.fileInput.click()" class="btn btn-primary" :disabled="uploading">
            <i class="fas fa-folder-open"></i> 选择图片
          </button>
        </div>
        <div v-if="selectedFile" class="selected-file">
          <i class="fas fa-image"></i> {{ selectedFile.name }}
          <button @click="uploadImage" class="btn btn-primary" :disabled="uploading">
            {{ uploading ? '上传中...' : '上传' }}
          </button>
        </div>
      </div>

      <p v-if="uploadMessage" :class="['upload-message', uploadSuccess ? 'success' : 'error']">
        {{ uploadMessage }}
      </p>

      <div class="images-section">
        <h2><i class="fas fa-images"></i> 我的图片</h2>
        <div v-if="loading" class="loading">
          <i class="fas fa-spinner fa-spin"></i> 加载中...
        </div>
        <div v-else-if="images.length === 0" class="empty-state">
          <i class="far fa-images"></i>
          <p>暂无图片，上传第一张图片开始吧</p>
        </div>
        <div v-else class="image-list">
          <div v-for="image in images" :key="image.id" class="image-item">
            <div class="image-info">
              <i class="fas fa-image"></i>
              <span class="filename">{{ image.originalFilename }}</span>
              <span class="filesize">{{ formatSize(image.fileSize) }}</span>
            </div>
            <button @click="deleteImage(image.id)" class="btn-delete" title="删除">
              <i class="fas fa-trash-alt"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { imageAPI } from '../api'

const userId = computed(() => localStorage.getItem('userId'))
const isLoggedIn = computed(() => !!userId.value)

const selectedFile = ref(null)
const uploading = ref(false)
const uploadMessage = ref('')
const uploadSuccess = ref(false)
const loading = ref(false)
const images = ref([])
const fileInput = ref(null)

const handleFileSelect = (event) => {
  selectedFile.value = event.target.files[0]
  uploadMessage.value = ''
}

const handleDrop = (event) => {
  const files = event.dataTransfer.files
  if (files.length > 0 && files[0].type.startsWith('image/')) {
    selectedFile.value = files[0]
    uploadMessage.value = ''
  }
}

const uploadImage = async () => {
  if (!selectedFile.value) return
  uploading.value = true
  uploadMessage.value = ''
  try {
    const response = await imageAPI.uploadImage(selectedFile.value, userId.value)
    if (response.data.success) {
      uploadMessage.value = '上传成功！'
      uploadSuccess.value = true
      selectedFile.value = null
      fileInput.value.value = ''
      loadImages()
    } else {
      uploadMessage.value = response.data.message
      uploadSuccess.value = false
    }
  } catch (e) {
    uploadMessage.value = '上传失败，请重试'
    uploadSuccess.value = false
  } finally {
    uploading.value = false
  }
}

const loadImages = async () => {
  if (!userId.value) return
  loading.value = true
  try {
    const response = await imageAPI.getUserImages(userId.value)
    if (response.data.success) {
      images.value = response.data.images
    }
  } catch (e) {
    console.error('加载图片失败', e)
  } finally {
    loading.value = false
  }
}

const deleteImage = async (imageId) => {
  if (!confirm('确定要删除这张图片吗？')) return
  try {
    const response = await imageAPI.deleteImage(imageId, userId.value)
    if (response.data.success) {
      loadImages()
    } else {
      alert(response.data.message)
    }
  } catch (e) {
    alert('删除失败')
  }
}

const formatSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

onMounted(() => {
  if (isLoggedIn.value) {
    loadImages()
  }
})
</script>

<style scoped>
.hero {
  position: relative;
  padding: 4rem 2rem;
  text-align: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #0f0f13 0%, #1a1a28 100%);
  z-index: -1;
}

.hero-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: repeating-linear-gradient(45deg, rgba(255,255,255,0.03) 0px, rgba(255,255,255,0.03) 2px, transparent 2px, transparent 70px);
}

.hero-content { max-width: 600px; margin: 0 auto; }

.hero h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  background: linear-gradient(135deg, #fff 0%, #00d4aa 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 1.1rem;
  color: var(--body-text-subtle);
  margin-bottom: 2rem;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn-lg { padding: 1rem 2rem; font-size: 1.1rem; }

.dashboard { max-width: 800px; margin: 0 auto; }

.upload-zone {
  background: var(--body-empty);
  border: 2px dashed var(--border-color);
  border-radius: 16px;
  padding: 3rem;
  text-align: center;
  position: relative;
  transition: border-color 0.3s;
}

.upload-zone:hover { border-color: var(--color-accent); }

.upload-zone-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
}

.upload-icon { font-size: 4rem; color: var(--color-accent); }

.upload-zone p { color: var(--body-text-subtle); }

.file-input { display: none; }

.selected-file {
  margin-top: 1.5rem;
  padding: 1rem;
  background: var(--input-bg);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.selected-file .fa-image { color: var(--color-accent); }
.selected-file .filename { flex: 1; text-align: left; color: var(--body-text); }

.upload-message {
  text-align: center;
  margin-top: 1rem;
  padding: 0.75rem;
  border-radius: 8px;
}

.upload-message.success {
  background: rgba(0, 212, 170, 0.1);
  color: var(--color-accent);
}

.upload-message.error {
  background: rgba(255, 107, 107, 0.1);
  color: #ff6b6b;
}

.images-section { margin-top: 3rem; }

.images-section h2 {
  font-size: 1.3rem;
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.images-section h2 .fa-images { color: var(--color-accent); }

.loading, .empty-state {
  text-align: center;
  padding: 3rem;
  color: var(--body-text-subtle);
}

.empty-state { display: flex; flex-direction: column; align-items: center; gap: 1rem; }
.empty-state .fa-images { font-size: 3rem; opacity: 0.5; }

.image-list { display: flex; flex-direction: column; gap: 0.75rem; }

.image-item {
  background: var(--body-empty);
  border: 1px solid var(--border-color);
  border-radius: 10px;
  padding: 1rem 1.25rem;
  display: flex;
  align-items: center;
  justify-content: space-between;
  transition: border-color 0.2s;
}

.image-item:hover { border-color: var(--color-accent); }

.image-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex: 1;
  min-width: 0;
}

.image-info .fa-image { color: var(--color-accent); font-size: 1.2rem; }
.image-info .filename { flex: 1; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.image-info .filesize { color: var(--body-text-subtle); font-size: 0.85rem; white-space: nowrap; }

.btn-delete {
  background: transparent;
  border: none;
  color: var(--body-text-subtle);
  cursor: pointer;
  padding: 0.5rem;
  border-radius: 6px;
  transition: all 0.2s;
}

.btn-delete:hover {
  background: rgba(255, 107, 107, 0.1);
  color: #ff6b6b;
}
</style>
