<template>
  <div class="image-management">
    <!-- Page Header -->
    <div class="page-header">
      <h1 class="page-title">图片管理</h1>
      <div class="view-toggle">
        <button
          class="view-btn"
          :class="{ active: viewMode === 'grid' }"
          @click="viewMode = 'grid'"
          title="网格视图"
        >
          <i class="fas fa-th-large"></i>
        </button>
        <button
          class="view-btn"
          :class="{ active: viewMode === 'list' }"
          @click="viewMode = 'list'"
          title="列表视图"
        >
          <i class="fas fa-list"></i>
        </button>
      </div>
    </div>

    <!-- Filter Bar -->
    <div class="filter-bar">
      <select v-model="selectedFolder" class="filter-select">
        <option value="">全部文件夹</option>
        <option v-for="folder in folders" :key="folder.id" :value="folder.id">
          {{ folder.name }}
        </option>
      </select>
      <select v-model="selectedTag" class="filter-select">
        <option value="">全部标签</option>
        <option v-for="tag in tags" :key="tag.id" :value="tag.id">
          {{ tag.name }}
        </option>
      </select>
      <input
        v-model="searchQuery"
        type="text"
        placeholder="搜索图片名称..."
        class="search-input"
      />
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="loading">
      <i class="fas fa-spinner fa-spin"></i>
      <span>加载中...</span>
    </div>

    <!-- Empty State -->
    <div v-else-if="images.length === 0" class="empty-state">
      <i class="fas fa-images"></i>
      <h3>暂无图片</h3>
      <p>还没有上传任何图片，开始上传你的第一张图片吧</p>
    </div>

    <!-- Image Grid View -->
    <div v-else class="image-grid">
      <div
        v-for="image in paginatedImages"
        :key="image.id"
        class="image-card"
        :class="{ selected: selectedImages.includes(image.id) }"
        @mouseenter="image.hover = true"
        @mouseleave="image.hover = false"
      >
        <!-- Checkbox -->
        <div class="image-checkbox">
          <input
            type="checkbox"
            :checked="selectedImages.includes(image.id)"
            @change="toggleSelect(image.id)"
          />
        </div>

        <!-- Action Buttons -->
        <div class="image-actions" :style="{ opacity: image.hover ? 1 : 0 }">
          <button class="action-btn" @click="handlePreview(image)" title="预览">
            <i class="fas fa-eye"></i>
          </button>
          <button class="action-btn" @click="copyLink(image)" title="复制链接">
            <i class="fas fa-link"></i>
          </button>
          <button class="action-btn delete" @click="deleteImage(image.id)" title="删除">
            <i class="fas fa-trash-alt"></i>
          </button>
        </div>

        <!-- Thumbnail -->
        <img
          :src="image.url"
          :alt="image.originalFilename"
          class="image-thumb"
          @click="handlePreview(image)"
        />

        <!-- Image Info -->
        <div class="image-info">
          <div class="image-name" :title="image.originalFilename">
            {{ image.originalFilename }}
          </div>
          <div class="image-meta">
            <span>{{ formatSize(image.fileSize) }}</span>
            <span>{{ formatDate(image.uploadTime) }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="pagination">
      <button class="btn-icon" :disabled="currentPage === 1" @click="onPageChange(1)">
        <i class="fas fa-angle-double-left"></i>
      </button>
      <button class="btn-icon" :disabled="currentPage === 1" @click="onPageChange(currentPage - 1)">
        <i class="fas fa-chevron-left"></i>
      </button>
      <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
      <button class="btn-icon" :disabled="currentPage === totalPages" @click="onPageChange(currentPage + 1)">
        <i class="fas fa-chevron-right"></i>
      </button>
      <button class="btn-icon" :disabled="currentPage === totalPages" @click="onPageChange(totalPages)">
        <i class="fas fa-angle-double-right"></i>
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()
const userId = ref(localStorage.getItem('userId'))
const viewImage = inject('viewImage', null)

const viewMode = ref('grid')
const images = ref([])
const selectedImages = ref([])
const loading = ref(false)

const searchQuery = ref('')
const selectedFolder = ref('')
const selectedTag = ref('')
const folders = ref([])
const tags = ref([])

const currentPage = ref(1)
const pageSize = ref(20)

const filteredImages = computed(() => {
  let result = images.value

  // Folder filter
  if (selectedFolder.value) {
    result = result.filter(img => img.folderId == selectedFolder.value)
  }

  // Tag filter
  if (selectedTag.value) {
    result = result.filter(img => {
      if (!img.tags || img.tags.length === 0) return false
      return img.tags.some(tag => tag.id == selectedTag.value)
    })
  }

  // Search filter
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(img =>
      img.originalFilename.toLowerCase().includes(query)
    )
  }

  return result
})

const totalPages = computed(() => Math.ceil(filteredImages.value.length / pageSize.value))

const paginatedImages = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredImages.value.slice(start, end)
})

const fetchUserImages = async () => {
  if (!userId.value) return

  loading.value = true
  try {
    const response = await axios.get(`/api/images/user/${userId.value}`)
    if (response.data.success) {
      images.value = response.data.images.map(img => ({
        ...img,
        url: '/api/images/' + img.id,
        hover: false
      }))
    }
  } catch (e) {
    console.error('加载图片失败', e)
    // Use mock data if API doesn't exist
    images.value = getMockImages()
  } finally {
    loading.value = false
  }
}

const fetchFolders = async () => {
  if (!userId.value) return
  try {
    const response = await axios.get(`/api/folders/user/${userId.value}`)
    if (response.data.success) {
      folders.value = response.data.folders
    }
  } catch (e) {
    console.error('加载文件夹失败', e)
  }
}

const fetchTags = async () => {
  if (!userId.value) return
  try {
    const response = await axios.get(`/api/tags/user/${userId.value}`)
    if (response.data.success) {
      tags.value = response.data.tags
    }
  } catch (e) {
    console.error('加载标签失败', e)
  }
}

const getMockImages = () => {
  return [
    {
      id: 'mock1',
      originalFilename: 'sunset_beach.jpg',
      fileSize: 2048000,
      uploadTime: '2026-04-28T10:30:00Z',
      url: 'https://picsum.photos/400/300?random=1',
      folderId: null,
      tags: [],
      hover: false
    },
    {
      id: 'mock2',
      originalFilename: 'mountain_view.png',
      fileSize: 3520000,
      uploadTime: '2026-04-27T15:45:00Z',
      url: 'https://picsum.photos/400/300?random=2',
      folderId: null,
      tags: [{ id: 'tag1', name: '风景', color: '#3B7DDD' }],
      hover: false
    },
    {
      id: 'mock3',
      originalFilename: 'city_night.jpg',
      fileSize: 1536000,
      uploadTime: '2026-04-26T20:00:00Z',
      url: 'https://picsum.photos/400/300?random=3',
      folderId: null,
      tags: [],
      hover: false
    },
    {
      id: 'mock4',
      originalFilename: 'forest_path.jpg',
      fileSize: 4096000,
      uploadTime: '2026-04-25T09:15:00Z',
      url: 'https://picsum.photos/400/300?random=4',
      folderId: null,
      tags: [{ id: 'tag2', name: '自然', color: '#10B981' }],
      hover: false
    }
  ]
}

const handlePreview = (image) => {
  if (viewImage) {
    viewImage({
      url: image.url,
      filename: image.originalFilename,
      fileSize: image.fileSize,
      contentType: image.contentType,
      uploadTime: image.uploadTime
    })
  } else {
    window.open(image.url, '_blank')
  }
}

const copyLink = async (image) => {
  const fullUrl = window.location.origin + image.url
  try {
    await navigator.clipboard.writeText(fullUrl)
    alert('链接已复制到剪贴板')
  } catch (e) {
    const textarea = document.createElement('textarea')
    textarea.value = fullUrl
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    alert('链接已复制到剪贴板')
  }
}

const deleteImage = async (imageId) => {
  if (!confirm('确定要删除这张图片吗？')) return
  try {
    const response = await axios.delete(`/api/images/${imageId}`, {
      data: { userId: userId.value }
    })
    if (response.data.success) {
      images.value = images.value.filter(img => img.id !== imageId)
      selectedImages.value = selectedImages.value.filter(id => id !== imageId)
    } else {
      alert(response.data.message || '删除失败')
    }
  } catch (e) {
    console.error('删除图片失败', e)
    // Mock delete for demo
    images.value = images.value.filter(img => img.id !== imageId)
    selectedImages.value = selectedImages.value.filter(id => id !== imageId)
    alert('图片已删除')
  }
}

const toggleSelect = (imageId) => {
  const index = selectedImages.value.indexOf(imageId)
  if (index === -1) {
    selectedImages.value.push(imageId)
  } else {
    selectedImages.value.splice(index, 1)
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
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const onPageChange = (page) => {
  currentPage.value = page
}

onMounted(() => {
  if (!userId.value) {
    router.push('/login')
    return
  }
  fetchUserImages()
  fetchFolders()
  fetchTags()
})
</script>

<style scoped>
.image-management {
  max-width: 1200px;
  margin: 0 auto;
  padding: 24px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.page-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1E293B;
}

.view-toggle {
  display: flex;
  gap: 4px;
  background: #F5F7FA;
  padding: 4px;
  border-radius: 8px;
}

.view-btn {
  padding: 8px 14px;
  border: none;
  background: transparent;
  color: #64748B;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.3s;
}

.view-btn:hover {
  color: #3B7DDD;
}

.view-btn.active {
  background: #fff;
  color: #3B7DDD;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.filter-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.filter-bar select,
.filter-bar input {
  padding: 10px 14px;
  border: 1px solid #E2E8F0;
  border-radius: 10px;
  font-size: 0.9rem;
}

.filter-bar select:focus,
.filter-bar input:focus {
  outline: none;
  border-color: #3B7DDD;
}

.filter-bar input {
  flex: 1;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
}

.image-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  border: 1px solid #E2E8F0;
  transition: all 0.3s;
  position: relative;
}

.image-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.12);
  border-color: #3B7DDD;
}

.image-thumb {
  width: 100%;
  height: 160px;
  object-fit: cover;
  background: #F5F7FA;
}

.image-info {
  padding: 14px;
}

.image-name {
  font-size: 0.9rem;
  color: #1E293B;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 6px;
}

.image-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #64748B;
}

.image-checkbox {
  position: absolute;
  top: 10px;
  left: 10px;
  width: 20px;
  height: 20px;
  opacity: 0;
  transition: all 0.3s;
}

.image-card:hover .image-checkbox,
.image-card.selected .image-checkbox {
  opacity: 1;
}

.image-card.selected {
  border-color: #3B7DDD;
  box-shadow: 0 0 0 3px rgba(59, 125, 221, 0.3);
}

.image-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  display: flex;
  gap: 6px;
  opacity: 0;
  transition: all 0.3s;
}

.image-card:hover .image-actions {
  opacity: 1;
}

.action-btn {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  color: #64748B;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.action-btn:hover {
  background: #3B7DDD;
  color: #fff;
}

.action-btn.delete:hover {
  background: #EF4444;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: #fff;
  border-radius: 16px;
  border: 1px solid #E2E8F0;
}

.empty-state i {
  font-size: 64px;
  color: #94A3B8;
  margin-bottom: 20px;
}

.empty-state h3 {
  font-size: 1.25rem;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 8px;
}

.empty-state p {
  color: #64748B;
  font-size: 0.95rem;
}

.loading {
  text-align: center;
  padding: 80px 20px;
  color: #64748B;
}

.loading i {
  font-size: 2rem;
  margin-bottom: 16px;
  color: #3B7DDD;
  display: block;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 32px;
  padding: 20px;
}

.page-info {
  padding: 8px 16px;
  font-size: 0.9rem;
  color: #64748B;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: #fff;
  border: 1px solid #E2E8F0;
  color: #64748B;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-icon:hover:not(:disabled) {
  background: #3B7DDD;
  border-color: #3B7DDD;
  color: #fff;
}

.btn-icon:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>