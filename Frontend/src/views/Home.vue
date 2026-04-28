<template>
  <div class="home">
    <div v-if="!isLoggedIn" class="welcome">
      <h1>欢迎使用图片托管平台</h1>
      <p>请先登录或注册账户</p>
      <div class="auth-buttons">
        <router-link to="/login"><button>登录</button></router-link>
        <router-link to="/register"><button>注册</button></router-link>
      </div>
    </div>

    <div v-if="isLoggedIn" class="upload-section">
      <h2>上传图片</h2>
      <div class="upload-area">
        <input type="file" @change="handleFileSelect" accept="image/*" ref="fileInput" />
        <button @click="uploadImage" :disabled="!selectedFile || uploading">
          {{ uploading ? '上传中...' : '上传' }}
        </button>
      </div>
      <p v-if="uploadMessage" :class="{ success: uploadSuccess, error: !uploadSuccess }">
        {{ uploadMessage }}
      </p>
    </div>

    <div v-if="isLoggedIn" class="images-section">
      <h2>我的图片</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="images.length === 0" class="no-images">暂无图片</div>
      <div v-else class="image-list">
        <div v-for="image in images" :key="image.id" class="image-item">
          <div class="image-info">
            <span>{{ image.originalFilename }}</span>
            <span>{{ formatSize(image.fileSize) }}</span>
          </div>
          <button @click="deleteImage(image.id)" class="delete-btn">删除</button>
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
    uploadMessage.value = '上传失败'
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
.welcome { text-align: center; padding: 3rem 0; }
.auth-buttons { display: flex; gap: 1rem; justify-content: center; margin-top: 1rem; }
.auth-buttons button { padding: 0.75rem 2rem; background: #1890ff; color: white; border: none; cursor: pointer; }
.upload-section, .images-section { margin-top: 2rem; }
.upload-area { display: flex; gap: 1rem; align-items: center; }
.upload-area input { flex: 1; }
.upload-area button { padding: 0.5rem 1.5rem; background: #52c41a; color: white; border: none; cursor: pointer; }
.success { color: green; margin-top: 1rem; }
.error { color: red; margin-top: 1rem; }
.image-list { display: grid; gap: 1rem; margin-top: 1rem; }
.image-item { display: flex; justify-content: space-between; align-items: center; padding: 1rem; border: 1px solid #ddd; border-radius: 4px; }
.delete-btn { padding: 0.5rem 1rem; background: #ff4d4f; color: white; border: none; cursor: pointer; }
.loading, .no-images { text-align: center; padding: 2rem; color: #999; }
</style>
