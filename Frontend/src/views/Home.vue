<template>
  <div class="home">
    <!-- Hero Section -->
    <section v-if="!isLoggedIn" class="hero">
      <div class="hero-content">
        <h1 class="hero-headline">
          <span class="headline-icon">
            <svg width="64" height="64" viewBox="0 0 64 64" fill="none">
              <defs>
                <linearGradient id="heroGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stop-color="#0071E3"/>
                  <stop offset="100%" stop-color="#00D4AA"/>
                </linearGradient>
              </defs>
              <rect x="4" y="4" width="56" height="56" rx="14" fill="url(#heroGrad)"/>
              <path d="M18 42L26 30L32 38L44 22L48 28" stroke="white" stroke-width="4" stroke-linecap="round" stroke-linejoin="round"/>
              <circle cx="24" cy="20" r="5" fill="white"/>
            </svg>
          </span>
          你的照片，<br/>安全存储，随时访问
        </h1>
        <p class="hero-subheadline">
          简单、快速、安全的图片托管服务<br/>
          支持高达 10MB 的高清图片
        </p>
        <div class="hero-cta">
          <router-link to="/register" class="btn btn-primary btn-large">
            <i class="fas fa-rocket"></i>
            开始使用
          </router-link>
          <router-link to="/login" class="btn btn-secondary btn-large">
            登录账户
          </router-link>
        </div>
      </div>
      <div class="hero-visual">
        <div class="floating-card card-1">
          <div class="card-image"></div>
          <span>风景照片</span>
        </div>
        <div class="floating-card card-2">
          <div class="card-image"></div>
          <span>家庭聚会</span>
        </div>
        <div class="floating-card card-3">
          <div class="card-image"></div>
          <span>旅行记录</span>
        </div>
      </div>
    </section>

    <!-- Dashboard -->
    <div v-if="isLoggedIn" class="dashboard">
      <!-- Upload Zone -->
      <div class="upload-section">
        <div
          class="upload-zone"
          @dragover.prevent="isDragging = true"
          @dragleave="isDragging = false"
          @drop.prevent="handleDrop"
          :class="{ 'drag-over': isDragging }"
        >
          <div class="upload-content">
            <div class="upload-icon">
              <i class="fas fa-cloud-upload-alt"></i>
            </div>
            <p class="upload-title">拖拽图片到此处，或点击选择</p>
            <p class="upload-subtitle">支持 JPG、PNG、GIF、WebP，单张最大 10MB，可多选</p>
            <input
              type="file"
              @change="handleFileSelect"
              accept="image/*"
              ref="fileInput"
              class="file-input"
              multiple
            />
            <button @click="$refs.fileInput.click()" class="btn btn-primary" :disabled="uploading">
              <i class="fas fa-folder-open"></i>
              选择图片
            </button>
          </div>

          <div v-if="selectedFiles.length > 0" class="selected-files">
            <div class="selected-files-header">
              <span>已选择 {{ selectedFiles.length }} 张图片</span>
              <button @click="clearSelection" class="btn-clear">清除</button>
            </div>
            <div class="selected-files-list">
              <div v-for="(file, index) in selectedFiles" :key="index" class="selected-file-item">
                <i class="fas fa-image"></i>
                <span class="file-name">{{ file.name }}</span>
                <span class="file-size">{{ formatSize(file.size) }}</span>
              </div>
            </div>
            <button @click="uploadImages" class="btn btn-primary" :disabled="uploading">
              <i v-if="uploading" class="fas fa-spinner fa-spin"></i>
              <span v-else><i class="fas fa-upload"></i> 上传全部</span>
            </button>
          </div>
        </div>

        <p v-if="uploadMessage" :class="['upload-message', uploadSuccess ? 'success' : 'error']">
          <i :class="uploadSuccess ? 'fas fa-check-circle' : 'fas fa-exclamation-circle'"></i>
          {{ uploadMessage }}
        </p>
      </div>

      <!-- Folder Section -->
      <div class="folders-section">
        <div class="section-header">
          <h2><i class="fas fa-folder"></i> 文件夹</h2>
          <button @click="showCreateFolderModal = true" class="btn btn-secondary btn-small">
            <i class="fas fa-plus"></i> 新建文件夹
          </button>
        </div>

        <div v-if="folders.length === 0" class="empty-folders">
          <i class="fas fa-folder-open"></i>
          <p>还没有文件夹，创建一个来整理你的图片</p>
        </div>

        <div v-else class="folder-grid">
          <div
            v-for="folder in folders"
            :key="folder.id"
            class="folder-card"
            :class="{ active: selectedFolderId === folder.id }"
            @click="selectFolder(folder)"
          >
            <div class="folder-icon">
              <i class="fas fa-folder"></i>
            </div>
            <div class="folder-info">
              <span class="folder-name">{{ folder.name }}</span>
              <span class="folder-count">{{ folder.imageCount }} 张图片</span>
            </div>
            <button @click.stop="deleteFolder(folder.id)" class="folder-delete">
              <i class="fas fa-trash-alt"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Tag Section -->
      <div class="tags-section">
        <div class="section-header">
          <h2><i class="fas fa-tags"></i> 标签</h2>
          <button @click="showCreateTagModal = true" class="btn btn-secondary btn-small">
            <i class="fas fa-plus"></i> 新建标签
          </button>
        </div>

        <div v-if="tags.length === 0" class="empty-tags">
          <i class="fas fa-tags"></i>
          <p>还没有标签，创建一个来分类你的图片</p>
        </div>

        <div v-else class="tag-list">
          <div
            v-for="tag in tags"
            :key="tag.id"
            class="tag-item"
            :class="{ active: selectedTagIds.includes(tag.id) }"
            @click="toggleTagFilter(tag.id)"
          >
            <span class="tag-color" :style="{ backgroundColor: tag.color }"></span>
            <span class="tag-name">{{ tag.name }}</span>
            <button @click.stop="deleteTag(tag.id)" class="tag-delete">
              <i class="fas fa-times"></i>
            </button>
          </div>
          <button v-if="selectedTagIds.length > 0" @click="clearTagFilter" class="btn btn-secondary btn-small clear-filter">
            <i class="fas fa-times"></i> 清除筛选
          </button>
        </div>
      </div>

      <!-- Images Section -->
      <div class="images-section">
        <div class="section-header">
          <div class="section-title-row">
            <h2>
              <i class="fas fa-images"></i> {{ selectedFolder ? selectedFolder.name : '所有图片' }}
              <span class="image-count">({{ filteredImages.length }})</span>
            </h2>
            <label v-if="filteredImages.length > 0" class="select-all-checkbox">
              <input type="checkbox" :checked="isAllSelected" @change="toggleSelectAll" />
              全选
            </label>
          </div>
          <!-- Batch Selection Toolbar -->
          <div v-if="selectedImages.length > 0" class="batch-toolbar">
            <span class="batch-count">已选择 {{ selectedImages.length }} 张图片</span>
            <button @click="clearBatchSelection" class="btn btn-secondary btn-small">
              <i class="fas fa-times"></i> 取消选择
            </button>
            <button @click="batchDelete" class="btn btn-secondary btn-small">
              <i class="fas fa-trash-alt"></i> 批量删除
            </button>
            <button @click="showBatchMoveModal = true" class="btn btn-secondary btn-small">
              <i class="fas fa-folder"></i> 批量移动
            </button>
            <button @click="batchCopyLinks" class="btn btn-secondary btn-small">
              <i class="fas fa-link"></i> 复制链接
            </button>
            <button @click="batchDownload" class="btn btn-secondary btn-small">
              <i class="fas fa-download"></i> 下载ZIP
            </button>
          </div>
          <div class="section-actions">
            <div class="search-box">
              <i class="fas fa-search"></i>
              <input
                v-model="searchQuery"
                type="text"
                placeholder="搜索图片名称..."
              />
            </div>
            <!-- Time Range Filter -->
            <select v-model="timeRange" class="filter-select">
              <option value="all">全部时间</option>
              <option value="today">今日</option>
              <option value="yesterday">昨日</option>
              <option value="week">近7天</option>
              <option value="month">近30天</option>
            </select>
            <!-- Format Filter -->
            <select v-model="formatFilter" class="filter-select">
              <option value="all">全部格式</option>
              <option value="jpg">JPG</option>
              <option value="png">PNG</option>
              <option value="gif">GIF</option>
              <option value="webp">WebP</option>
            </select>
            <!-- Size Filter -->
            <select v-model="sizeFilter" class="filter-select">
              <option value="all">全部大小</option>
              <option value="small">≤1MB</option>
              <option value="medium">1-5MB</option>
              <option value="large">5-10MB</option>
              <option value="xlarge">>10MB</option>
            </select>
            <select v-model="sortBy" class="sort-select" @change="onSortChange">
              <option value="uploadTime">按上传时间</option>
              <option value="fileSize">按文件大小</option>
              <option value="viewCount">按访问次数</option>
            </select>
            <button class="btn-icon" @click="toggleSortOrder" :title="sortOrder === 'desc' ? '降序' : '升序'">
              <i :class="sortOrder === 'desc' ? 'fas fa-arrow-down' : 'fas fa-arrow-up'"></i>
            </button>
            <button class="btn-icon" @click="viewMode = viewMode === 'grid' ? 'list' : 'grid'" :title="viewMode === 'grid' ? '列表视图' : '网格视图'">
              <i :class="viewMode === 'grid' ? 'fas fa-list' : 'fas fa-th-large'"></i>
            </button>
            <div v-if="selectedFolder" class="folder-actions">
                <button @click="moveImageToFolder(null)" class="btn btn-secondary btn-small">
                  <i class="fas fa-arrow-left"></i> 移至全部
                </button>
                <button @click="showMoveModal = true" class="btn btn-secondary btn-small" :disabled="!selectedImage">
                  <i class="fas fa-folder"></i> 移动到
                </button>
              </div>
          </div>
        </div>

        <div v-if="loading" class="loading">
          <i class="fas fa-spinner fa-spin"></i>
          <span>加载中...</span>
        </div>

        <div v-else-if="filteredImages.length === 0" class="empty-state">
          <div class="empty-icon">
            <i class="far fa-images"></i>
          </div>
          <p>{{ selectedFolder ? '此文件夹暂无图片' : '还没有上传任何图片' }}</p>
          <router-link v-if="!selectedFolder" to="/register" class="btn btn-primary">
            上传第一张图片
          </router-link>
        </div>

        <div v-else class="image-grid" :class="{ 'list-view': viewMode === 'list' }">
          <div
            v-for="image in paginatedImages"
            :key="image.id"
            class="image-card"
            :class="{ selected: selectedImage === image.id || selectedImages.includes(image.id), 'list-item': viewMode === 'list' }"
            @click="toggleImageSelection(image.id)"
          >
            <div class="image-checkbox" @click.stop>
              <input
                type="checkbox"
                :checked="selectedImages.includes(image.id)"
                @change="toggleImageSelection(image.id)"
              />
            </div>
            <div class="image-preview">
              <img :src="image.url" :alt="image.originalFilename" @click.stop="openViewer(image)" />
            </div>
            <div class="image-meta">
              <span class="image-name" :title="image.originalFilename">{{ image.originalFilename }}</span>
              <span class="image-size">{{ formatSize(image.fileSize) }}</span>
              <span class="image-views"><i class="fas fa-eye"></i> {{ image.viewCount || 0 }}</span>
              <div v-if="image.tags && image.tags.length > 0" class="image-tags">
                <span
                  v-for="tag in image.tags.slice(0, 3)"
                  :key="tag.id"
                  class="image-tag"
                  :style="{ backgroundColor: tag.color + '20', color: tag.color, borderColor: tag.color }"
                >
                  {{ tag.name }}
                </span>
                <span v-if="image.tags.length > 3" class="image-tag more">+{{ image.tags.length - 3 }}</span>
              </div>
            </div>
            <div class="image-actions">
              <button @click.stop="openImageTagModal(image)" class="image-action" title="管理标签">
                <i class="fas fa-tags"></i>
              </button>
              <button @click.stop="copyLink(image.url)" class="image-action" title="复制链接">
                <i class="fas fa-link"></i>
              </button>
              <button @click.stop="downloadImage(image)" class="image-action" title="下载">
                <i class="fas fa-download"></i>
              </button>
              <button @click.stop="deleteImage(image.id)" class="image-action delete" title="删除">
                <i class="fas fa-trash-alt"></i>
              </button>
              <button @click.stop="openEditModal(image)" class="image-action" title="编辑">
                <i class="fas fa-edit"></i>
              </button>
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
    </div>

    <!-- Create Folder Modal -->
    <div v-if="showCreateFolderModal" class="modal-overlay" @click.self="showCreateFolderModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>新建文件夹</h3>
          <button @click="showCreateFolderModal = false" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>文件夹名称</label>
            <input
              v-model="newFolderName"
              type="text"
              placeholder="输入文件夹名称"
              @keyup.enter="createFolder"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showCreateFolderModal = false" class="btn btn-secondary">取消</button>
          <button @click="createFolder" class="btn btn-primary">创建</button>
        </div>
      </div>
    </div>

    <!-- Batch Move Image Modal -->
    <div v-if="showBatchMoveModal" class="modal-overlay" @click.self="showBatchMoveModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>批量移动图片到文件夹</h3>
          <button @click="showBatchMoveModal = false" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="folder-list">
            <div class="folder-option" @click="batchMoveToFolder(null)">
              <i class="fas fa-images"></i>
              <span>移至全部图片</span>
            </div>
            <div
              v-for="folder in folders"
              :key="folder.id"
              class="folder-option"
              @click="batchMoveToFolder(folder.id)"
            >
              <i class="fas fa-folder"></i>
              <span>{{ folder.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Move Image Modal -->
    <div v-if="showMoveModal" class="modal-overlay" @click.self="showMoveModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>移动图片到文件夹</h3>
          <button @click="showMoveModal = false" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="folder-list">
            <div
              v-for="folder in folders"
              :key="folder.id"
              class="folder-option"
              @click="moveImageToFolder(folder.id)"
            >
              <i class="fas fa-folder"></i>
              <span>{{ folder.name }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Tag Modal -->
    <div v-if="showCreateTagModal" class="modal-overlay" @click.self="showCreateTagModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>新建标签</h3>
          <button @click="showCreateTagModal = false" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>标签名称</label>
            <input
              v-model="newTagName"
              type="text"
              placeholder="输入标签名称"
              @keyup.enter="createTag"
            />
          </div>
          <div class="form-group">
            <label>标签颜色</label>
            <div class="color-picker">
              <input v-model="newTagColor" type="color" class="color-input" />
              <span class="color-value">{{ newTagColor }}</span>
            </div>
          </div>
          <div class="form-group">
            <label>预览</label>
            <span
              class="tag-preview"
              :style="{ backgroundColor: newTagColor + '20', color: newTagColor, borderColor: newTagColor }"
            >
              {{ newTagName || '标签名称' }}
            </span>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showCreateTagModal = false" class="btn btn-secondary">取消</button>
          <button @click="createTag" class="btn btn-primary">创建</button>
        </div>
      </div>
    </div>

    <!-- Image Tag Modal -->
    <div v-if="showImageTagModal" class="modal-overlay" @click.self="showImageTagModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>管理图片标签</h3>
          <button @click="showImageTagModal = false" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="tags.length === 0" class="empty-tags-modal">
            <p>还没有标签，请先创建标签</p>
          </div>
          <div v-else class="tag-manage-list">
            <div
              v-for="tag in tags"
              :key="tag.id"
              class="tag-manage-item"
            >
              <div class="tag-manage-info">
                <span class="tag-color" :style="{ backgroundColor: tag.color }"></span>
                <span class="tag-name">{{ tag.name }}</span>
              </div>
              <button
                v-if="!hasImageTag(tag.id)"
                @click="addTagToImage(tag.id)"
                class="btn btn-secondary btn-small"
              >
                <i class="fas fa-plus"></i> 添加
              </button>
              <button
                v-else
                @click="removeTagFromImage(tag.id)"
                class="btn btn-secondary btn-small remove"
              >
                <i class="fas fa-minus"></i> 移除
              </button>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showImageTagModal = false" class="btn btn-secondary">关闭</button>
        </div>
      </div>
    </div>

    <!-- Edit Image Modal -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="showEditModal = false">
      <div class="modal">
        <div class="modal-header">
          <h3>编辑图片</h3>
          <button @click="showEditModal = false" class="modal-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>图片名称</label>
            <input
              v-model="editImageData.filename"
              type="text"
              placeholder="输入图片名称"
            />
          </div>
          <div class="form-group">
            <label>备注</label>
            <textarea
              v-model="editImageData.remark"
              placeholder="输入备注信息"
              rows="3"
            ></textarea>
          </div>
          <div class="form-group">
            <label>文件夹</label>
            <select v-model="editImageData.folderId" class="sort-select">
              <option :value="null">移至全部</option>
              <option v-for="folder in folders" :key="folder.id" :value="folder.id">
                {{ folder.name }}
              </option>
            </select>
          </div>
          <div class="form-group">
            <label>标签</label>
            <div v-if="tags.length === 0" class="empty-tags-modal">
              <p>还没有标签，请先创建标签</p>
            </div>
            <div v-else class="tag-manage-list">
              <div
                v-for="tag in tags"
                :key="tag.id"
                class="tag-manage-item"
              >
                <div class="tag-manage-info">
                  <span class="tag-color" :style="{ backgroundColor: tag.color }"></span>
                  <span class="tag-name">{{ tag.name }}</span>
                </div>
                <label class="checkbox-label">
                  <input
                    type="checkbox"
                    :checked="editImageData.tagIds.includes(tag.id)"
                    @change="toggleEditImageTag(tag.id)"
                  />
                </label>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showEditModal = false" class="btn btn-secondary">取消</button>
          <button @click="saveImageEdit" class="btn btn-primary">保存</button>
        </div>
      </div>
    </div>

    <!-- Trash Section -->
    <div v-if="isLoggedIn" class="trash-section">
      <div class="section-header">
        <h2><i class="fas fa-trash-alt"></i> 回收站</h2>
        <div class="trash-actions">
          <button v-if="trashImages.length > 0" @click="emptyTrash" class="btn btn-secondary btn-small">
            <i class="fas fa-trash-alt"></i> 清空回收站
          </button>
          <button @click="showTrash = !showTrash" class="btn btn-secondary btn-small">
            <i :class="showTrash ? 'fas fa-chevron-up' : 'fas fa-chevron-down'"></i>
            {{ showTrash ? '收起' : '显示' }} ({{ trashImages.length }})
          </button>
        </div>
      </div>

      <div v-if="showTrash && trashImages.length > 0" class="trash-grid">
        <div v-for="image in trashImages" :key="image.id" class="trash-card">
          <div class="trash-preview">
            <img :src="image.url" :alt="image.originalFilename" />
          </div>
          <div class="trash-info">
            <span class="trash-name">{{ image.originalFilename }}</span>
            <span class="trash-date">{{ formatDate(image.deletedAt) }} 删除</span>
          </div>
          <div class="trash-actions-btns">
            <button @click="restoreImage(image.id)" class="btn-action restore" title="恢复">
              <i class="fas fa-undo"></i>
            </button>
            <button @click="permanentDelete(image.id)" class="btn-action delete" title="彻底删除">
              <i class="fas fa-trash-alt"></i>
            </button>
          </div>
        </div>
      </div>
      <div v-else-if="showTrash && trashImages.length === 0" class="empty-trash">
        <i class="fas fa-trash-alt"></i>
        <p>回收站是空的</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, inject, watch } from 'vue'
import { imageAPI, folderAPI, tagAPI } from '../api'
import JSZip from 'jszip'

const userId = computed(() => localStorage.getItem('userId'))
const isLoggedIn = computed(() => !!userId.value)
const viewImage = inject('viewImage')

const selectedFiles = ref([])
const uploading = ref(false)
const uploadMessage = ref('')
const uploadSuccess = ref(false)
const isDragging = ref(false)
const fileInput = ref(null)
const uploadProgress = ref(0)

const loading = ref(false)
const images = ref([])
const folders = ref([])
const selectedFolderId = ref(null)
const selectedImage = ref(null)
const searchQuery = ref('')

const showCreateFolderModal = ref(false)
const newFolderName = ref('')
const showMoveModal = ref(false)
const showBatchMoveModal = ref(false)
const showTrash = ref(false)
const trashImages = ref([])

// Tag management
const tags = ref([])
const showCreateTagModal = ref(false)
const newTagName = ref('')
const newTagColor = ref('#0071E3')
const showTagFilterModal = ref(false)
const selectedTagIds = ref([])
const showImageTagModal = ref(false)
const imageTagLoading = ref(false)

// Edit image
const showEditModal = ref(false)
const editImageData = ref({
  id: null,
  filename: '',
  remark: '',
  folderId: null,
  tagIds: []
})

// Batch selection
const selectedImages = ref([])

const sortBy = ref('uploadTime')
const sortOrder = ref('desc')
const viewMode = ref('grid')
const currentPage = ref(1)
const pageSize = ref(20)

// Filter states
const timeRange = ref('all')
const formatFilter = ref('all')
const sizeFilter = ref('all')

const selectedFolder = computed(() => {
  return folders.value.find(f => f.id === selectedFolderId.value) || null
})

const filteredImages = computed(() => {
  let result = images.value

  // Folder filtering
  if (selectedFolderId.value) {
    result = result.filter(img => img.folderId == selectedFolderId.value)
  }

  // Keyword search (name + remark)
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase()
    result = result.filter(img =>
      img.originalFilename.toLowerCase().includes(query) ||
      (img.remark && img.remark.toLowerCase().includes(query))
    )
  }

  // Tag filtering (AND - image must have all selected tags)
  if (selectedTagIds.value.length > 0) {
    result = result.filter(img => {
      if (!img.tags || img.tags.length === 0) return false
      const imageTagIds = img.tags.map(t => t.id)
      return selectedTagIds.value.every(tagId => imageTagIds.includes(tagId))
    })
  }

  // Time range filtering
  if (timeRange.value !== 'all') {
    const now = new Date()
    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    result = result.filter(img => {
      const uploadDate = new Date(img.uploadTime)
      switch (timeRange.value) {
        case 'today': return uploadDate >= today
        case 'yesterday': return uploadDate >= new Date(today - 86400000) && uploadDate < today
        case 'week': return uploadDate >= new Date(today - 7 * 86400000)
        case 'month': return uploadDate >= new Date(today - 30 * 86400000)
        default: return true
      }
    })
  }

  // Format filtering
  if (formatFilter.value !== 'all') {
    result = result.filter(img => {
      const ext = img.originalFilename.split('.').pop()?.toLowerCase()
      return ext === formatFilter.value
    })
  }

  // Size filtering
  if (sizeFilter.value !== 'all') {
    result = result.filter(img => {
      const size = img.fileSize || 0
      switch (sizeFilter.value) {
        case 'small': return size <= 1024 * 1024
        case 'medium': return size > 1024 * 1024 && size <= 5 * 1024 * 1024
        case 'large': return size > 5 * 1024 * 1024 && size <= 10 * 1024 * 1024
        case 'xlarge': return size > 10 * 1024 * 1024
        default: return true
      }
    })
  }

  // Sort
  result = [...result].sort((a, b) => {
    let cmp = 0
    switch (sortBy.value) {
      case 'fileSize':
        cmp = (a.fileSize || 0) - (b.fileSize || 0)
        break
      case 'viewCount':
        cmp = (a.viewCount || 0) - (b.viewCount || 0)
        break
      case 'uploadTime':
      default:
        cmp = new Date(a.uploadTime || 0) - new Date(b.uploadTime || 0)
        break
    }
    return sortOrder.value === 'asc' ? cmp : -cmp
  })
  return result
})

const totalPages = computed(() => Math.ceil(filteredImages.value.length / pageSize.value))

const isAllSelected = computed(() => {
  return paginatedImages.value.length > 0 && paginatedImages.value.every(img => selectedImages.value.includes(img.id))
})

const paginatedImages = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value
  const end = start + pageSize.value
  return filteredImages.value.slice(start, end)
})

const handleFileSelect = (event) => {
  const files = Array.from(event.target.files).filter(f => f.type.startsWith('image/'))
  if (files.length > 0) {
    selectedFiles.value = files
    uploadMessage.value = ''
  }
}

const handleDrop = (event) => {
  isDragging.value = false
  const files = Array.from(event.dataTransfer.files).filter(f => f.type.startsWith('image/'))
  if (files.length > 0) {
    selectedFiles.value = files
    uploadMessage.value = ''
  }
}

const clearSelection = () => {
  selectedFiles.value = []
  fileInput.value.value = ''
}

const uploadImages = async () => {
  if (selectedFiles.value.length === 0) return
  uploading.value = true
  uploadMessage.value = ''
  uploadProgress.value = 0
  let successCount = 0
  let failCount = 0

  for (let i = 0; i < selectedFiles.value.length; i++) {
    const file = selectedFiles.value[i]
    try {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('userId', userId.value)
      if (selectedFolderId.value) {
        formData.append('folderId', selectedFolderId.value)
      }
      const response = await imageAPI.uploadImage(formData)
      if (response.data.success) {
        successCount++
      } else {
        failCount++
      }
    } catch (e) {
      failCount++
    }
    uploadProgress.value = Math.round(((i + 1) / selectedFiles.value.length) * 100)
  }

  if (successCount > 0) {
    uploadMessage.value = `成功上传 ${successCount} 张图片${failCount > 0 ? `，${failCount} 张失败` : ''}`
    uploadSuccess.value = true
    selectedFiles.value = []
    fileInput.value.value = ''
    loadImages()
    loadFolders()
  } else {
    uploadMessage.value = '上传失败，请重试'
    uploadSuccess.value = false
  }
  uploading.value = false
}

const loadImages = async () => {
  if (!userId.value) return
  loading.value = true
  try {
    const response = await imageAPI.getUserImages(userId.value, sortBy.value, sortOrder.value, currentPage.value, pageSize.value)
    if (response.data.success) {
      images.value = response.data.images.map(img => ({
        ...img,
        url: '/api/images/' + img.id
      }))
    }
  } catch (e) {
    console.error('加载图片失败', e)
  } finally {
    loading.value = false
  }
}

const loadFolders = async () => {
  if (!userId.value) return
  try {
    const response = await folderAPI.getUserFolders(userId.value)
    if (response.data.success) {
      folders.value = response.data.folders.map(f => ({
        ...f,
        imageCount: images.value.filter(img => img.folderId === f.id).length
      }))
    }
  } catch (e) {
    console.error('加载文件夹失败', e)
  }
}

const selectFolder = (folder) => {
  selectedFolderId.value = selectedFolderId.value === folder.id ? null : folder.id
  selectedImage.value = null
}

const selectImage = (image) => {
  selectedImage.value = selectedImage.value === image.id ? null : image.id
}

const openViewer = (image) => {
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

const createFolder = async () => {
  if (!newFolderName.value.trim()) return
  try {
    const response = await folderAPI.createFolder(newFolderName.value, userId.value)
    if (response.data.success) {
      showCreateFolderModal.value = false
      newFolderName.value = ''
      loadFolders()
    }
  } catch (e) {
    console.error('创建文件夹失败', e)
  }
}

const deleteFolder = async (folderId) => {
  if (!confirm('确定要删除此文件夹吗？文件夹内的图片将移至全部。')) return
  try {
    const response = await folderAPI.deleteFolder(folderId, userId.value)
    if (response.data.success) {
      if (selectedFolderId.value === folderId) {
        selectedFolderId.value = null
      }
      loadFolders()
      loadImages()
    }
  } catch (e) {
    console.error('删除文件夹失败', e)
  }
}

const moveImageToFolder = async (folderId) => {
  if (!selectedImage.value) return
  try {
    const response = await imageAPI.moveToFolder(selectedImage.value, folderId, userId.value)
    if (response.data.success) {
      showMoveModal.value = false
      loadImages()
      loadFolders()
      selectedImage.value = null
    }
  } catch (e) {
    console.error('移动图片失败', e)
  }
}

const deleteImage = async (imageId) => {
  if (!confirm('确定要删除这张图片吗？')) return
  try {
    const response = await imageAPI.deleteImage(imageId, userId.value)
    if (response.data.success) {
      loadImages()
      loadFolders()
    } else {
      alert(response.data.message)
    }
  } catch (e) {
    alert('删除失败')
  }
}

const copyLink = async (url) => {
  const fullUrl = window.location.origin + url
  try {
    await navigator.clipboard.writeText(fullUrl)
    uploadMessage.value = '链接已复制到剪贴板'
    uploadSuccess.value = true
    setTimeout(() => { uploadMessage.value = '' }, 2000)
  } catch (e) {
    // Fallback for older browsers
    const textarea = document.createElement('textarea')
    textarea.value = fullUrl
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    uploadMessage.value = '链接已复制到剪贴板'
    uploadSuccess.value = true
    setTimeout(() => { uploadMessage.value = '' }, 2000)
  }
}

const downloadImage = async (image) => {
  const response = await fetch(image.url)
  const blob = await response.blob()
  const url = window.URL.createObjectURL(blob)
  const a = document.createElement('a')
  a.href = url
  a.download = image.originalFilename
  document.body.appendChild(a)
  a.click()
  window.URL.revokeObjectURL(url)
  document.body.removeChild(a)
}

const formatSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

const toggleSortOrder = () => {
  sortOrder.value = sortOrder.value === 'desc' ? 'asc' : 'desc'
  currentPage.value = 1
}

const onSortChange = () => {
  currentPage.value = 1
}

watch(searchQuery, () => { currentPage.value = 1 })
watch(selectedFolderId, () => { currentPage.value = 1 })

const onPageChange = (page) => {
  currentPage.value = page
  loadImages()
}

const loadTrash = async () => {
  if (!userId.value) return
  try {
    const response = await imageAPI.getTrashImages(userId.value)
    if (response.data.success) {
      trashImages.value = response.data.images.map(img => ({
        ...img,
        url: '/api/images/' + img.id
      }))
    }
  } catch (e) {
    console.error('加载回收站失败', e)
  }
}

const restoreImage = async (imageId) => {
  try {
    const response = await imageAPI.restoreImage(imageId, userId.value)
    if (response.data.success) {
      loadTrash()
      loadImages()
      loadFolders()
    }
  } catch (e) {
    console.error('恢复图片失败', e)
  }
}

const permanentDelete = async (imageId) => {
  if (!confirm('彻底删除后无法恢复，确定要删除吗？')) return
  try {
    const response = await imageAPI.permanentDeleteImage(imageId, userId.value)
    if (response.data.success) {
      loadTrash()
    }
  } catch (e) {
    console.error('彻底删除失败', e)
  }
}

const emptyTrash = async () => {
  if (!confirm('确定要清空回收站吗？此操作不可恢复！')) return
  try {
    const response = await imageAPI.emptyTrash(userId.value)
    if (response.data.success) {
      trashImages.value = []
    }
  } catch (e) {
    console.error('清空回收站失败', e)
  }
}

// Tag management
const loadTags = async () => {
  if (!userId.value) return
  try {
    const response = await tagAPI.getUserTags(userId.value)
    if (response.data.success) {
      tags.value = response.data.tags
    }
  } catch (e) {
    console.error('加载标签失败', e)
  }
}

const createTag = async () => {
  if (!newTagName.value.trim()) return
  try {
    const response = await tagAPI.createTag(userId.value, newTagName.value.trim(), newTagColor.value)
    if (response.data.success) {
      showCreateTagModal.value = false
      newTagName.value = ''
      newTagColor.value = '#0071E3'
      loadTags()
    } else {
      alert(response.data.message)
    }
  } catch (e) {
    console.error('创建标签失败', e)
  }
}

const deleteTag = async (tagId) => {
  if (!confirm('确定要删除此标签吗？')) return
  try {
    const response = await tagAPI.deleteTag(tagId, userId.value)
    if (response.data.success) {
      selectedTagIds.value = selectedTagIds.value.filter(id => id !== tagId)
      loadTags()
      loadImages()
    } else {
      alert(response.data.message)
    }
  } catch (e) {
    console.error('删除标签失败', e)
  }
}

const toggleTagFilter = (tagId) => {
  if (selectedTagIds.value.includes(tagId)) {
    selectedTagIds.value = selectedTagIds.value.filter(id => id !== tagId)
  } else {
    selectedTagIds.value.push(tagId)
  }
}

const clearTagFilter = () => {
  selectedTagIds.value = []
}

const openImageTagModal = (image) => {
  selectedImage.value = image.id
  showImageTagModal.value = true
}

const addTagToImage = async (tagId) => {
  if (!selectedImage.value) return
  try {
    const response = await tagAPI.addTagToImage(selectedImage.value, tagId, userId.value)
    if (response.data.success) {
      loadImages()
    } else {
      alert(response.data.message)
    }
  } catch (e) {
    console.error('添加标签失败', e)
  }
}

const removeTagFromImage = async (tagId) => {
  if (!selectedImage.value) return
  try {
    const response = await tagAPI.removeTagFromImage(selectedImage.value, tagId, userId.value)
    if (response.data.success) {
      loadImages()
    } else {
      alert(response.data.message)
    }
  } catch (e) {
    console.error('移除标签失败', e)
  }
}

const hasImageTag = (tagId) => {
  const image = images.value.find(img => img.id === selectedImage.value)
  if (!image || !image.tags) return false
  return image.tags.some(t => t.id === tagId)
}

// Edit image functions
const openEditModal = (image) => {
  selectedImage.value = image.id
  editImageData.value = {
    id: image.id,
    filename: image.originalFilename || '',
    remark: image.remark || '',
    folderId: image.folderId || null,
    tagIds: image.tags ? image.tags.map(t => t.id) : []
  }
  showEditModal.value = true
}

const toggleEditImageTag = (tagId) => {
  const index = editImageData.value.tagIds.indexOf(tagId)
  if (index === -1) {
    editImageData.value.tagIds.push(tagId)
  } else {
    editImageData.value.tagIds.splice(index, 1)
  }
}

const saveImageEdit = async () => {
  if (!editImageData.value.id) return
  try {
    const currentImageTags = images.value.find(img => img.id === editImageData.value.id)?.tags || []
    const currentTagIds = currentImageTags.map(t => t.id)
    const newTagIds = editImageData.value.tagIds

    // Update image info
    const response = await imageAPI.updateImage(
      editImageData.value.id,
      userId.value,
      editImageData.value.filename,
      editImageData.value.remark,
      editImageData.value.folderId
    )

    if (response.data.success) {
      // Handle tags
      const tagsToAdd = newTagIds.filter(id => !currentTagIds.includes(id))
      const tagsToRemove = currentTagIds.filter(id => !newTagIds.includes(id))

      for (const tagId of tagsToAdd) {
        await tagAPI.addTagToImage(editImageData.value.id, tagId, userId.value)
      }
      for (const tagId of tagsToRemove) {
        await tagAPI.removeTagFromImage(editImageData.value.id, tagId, userId.value)
      }

      showEditModal.value = false
      loadImages()
      loadFolders()
      uploadMessage.value = '图片信息已更新'
      uploadSuccess.value = true
      setTimeout(() => { uploadMessage.value = '' }, 2000)
    } else {
      alert(response.data.message)
    }
  } catch (e) {
    console.error('保存图片信息失败', e)
    alert('保存失败')
  }
}

// Batch selection functions
const toggleImageSelection = (imageId) => {
  const index = selectedImages.value.indexOf(imageId)
  if (index === -1) {
    selectedImages.value.push(imageId)
  } else {
    selectedImages.value.splice(index, 1)
  }
}

const toggleSelectAll = () => {
  if (isAllSelected.value) {
    // Deselect all visible images
    paginatedImages.value.forEach(img => {
      const index = selectedImages.value.indexOf(img.id)
      if (index !== -1) {
        selectedImages.value.splice(index, 1)
      }
    })
  } else {
    // Select all visible images
    paginatedImages.value.forEach(img => {
      if (!selectedImages.value.includes(img.id)) {
        selectedImages.value.push(img.id)
      }
    })
  }
}

const clearBatchSelection = () => {
  selectedImages.value = []
}

const batchDelete = async () => {
  if (selectedImages.value.length === 0) return
  if (!confirm(`确定要删除选中的 ${selectedImages.value.length} 张图片吗？`)) return
  try {
    const response = await imageAPI.batchDelete(selectedImages.value, userId.value)
    if (response.data.success) {
      selectedImages.value = []
      loadImages()
      loadFolders()
      uploadMessage.value = '批量删除成功'
      uploadSuccess.value = true
      setTimeout(() => { uploadMessage.value = '' }, 2000)
    } else {
      alert(response.data.message)
    }
  } catch (e) {
    alert('批量删除失败')
  }
}

const batchMoveToFolder = async (folderId) => {
  if (selectedImages.value.length === 0) return
  try {
    const response = await imageAPI.batchMove(selectedImages.value, folderId, userId.value)
    if (response.data.success) {
      showBatchMoveModal.value = false
      selectedImages.value = []
      loadImages()
      loadFolders()
      uploadMessage.value = '批量移动成功'
      uploadSuccess.value = true
      setTimeout(() => { uploadMessage.value = '' }, 2000)
    } else {
      alert(response.data.message)
    }
  } catch (e) {
    alert('批量移动失败')
  }
}

const batchCopyLinks = async () => {
  if (selectedImages.value.length === 0) return
  const links = selectedImages.value.map(id => {
    const image = images.value.find(img => img.id === id)
    return image ? window.location.origin + image.url : ''
  }).filter(Boolean)
  const text = links.join('\n')
  try {
    await navigator.clipboard.writeText(text)
    uploadMessage.value = `已复制 ${selectedImages.value.length} 个链接到剪贴板`
    uploadSuccess.value = true
    setTimeout(() => { uploadMessage.value = '' }, 3000)
  } catch (e) {
    const textarea = document.createElement('textarea')
    textarea.value = text
    document.body.appendChild(textarea)
    textarea.select()
    document.execCommand('copy')
    document.body.removeChild(textarea)
    uploadMessage.value = `已复制 ${selectedImages.value.length} 个链接到剪贴板`
    uploadSuccess.value = true
    setTimeout(() => { uploadMessage.value = '' }, 3000)
  }
}

const batchDownload = async () => {
  if (selectedImages.value.length === 0) return
  uploadMessage.value = '正在打包下载...'
  uploadSuccess.value = true

  try {
    const zip = new JSZip()
    const folder = zip.folder('images')

    for (const imageId of selectedImages.value) {
      const image = images.value.find(img => img.id === imageId)
      if (!image) continue

      try {
        const response = await fetch(image.url)
        const blob = await response.blob()
        // Use original filename or generate one based on id
        const ext = image.originalFilename.split('.').pop() || 'jpg'
        const filename = image.originalFilename || `image_${imageId}.${ext}`
        folder.file(filename, blob)
      } catch (e) {
        console.error(`Failed to fetch image ${imageId}:`, e)
      }
    }

    const content = await zip.generateAsync({ type: 'blob' })
    const url = window.URL.createObjectURL(content)
    const a = document.createElement('a')
    a.href = url
    a.download = `images_${Date.now()}.zip`
    document.body.appendChild(a)
    a.click()
    window.URL.revokeObjectURL(url)
    document.body.removeChild(a)

    uploadMessage.value = `已下载 ${selectedImages.value.length} 张图片`
    setTimeout(() => { uploadMessage.value = '' }, 2000)
  } catch (e) {
    console.error('Batch download failed:', e)
    uploadMessage.value = '批量下载失败'
    uploadSuccess.value = false
    setTimeout(() => { uploadMessage.value = '' }, 2000)
  }
}

onMounted(() => {
  if (isLoggedIn.value) {
    loadImages()
    loadFolders()
    loadTrash()
    loadTags()
  }
})
</script>

<style scoped>
/* Hero Section */
.hero {
  min-height: calc(100vh - 48px);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 80px 24px;
  background: linear-gradient(180deg, #ffffff 0%, #f5f5f7 50%, #ffffff 100%);
  position: relative;
  overflow: hidden;
}

.hero::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(ellipse 80% 50% at 50% -20%, rgba(0, 113, 227, 0.08), transparent);
  pointer-events: none;
}

.hero-content {
  text-align: center;
  max-width: 680px;
  position: relative;
  z-index: 1;
}

.hero-headline {
  font-size: 4rem;
  font-weight: 700;
  line-height: 1.05;
  letter-spacing: -0.02em;
  margin-bottom: 24px;
  color: var(--text-primary);
}

.headline-icon {
  display: block;
  margin-bottom: 24px;
  color: var(--accent);
}

.hero-subheadline {
  font-size: 1.5rem;
  color: var(--text-secondary);
  line-height: 1.4;
  margin-bottom: 40px;
}

.hero-cta {
  display: flex;
  gap: 16px;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-large {
  padding: 18px 36px;
  font-size: 1.1rem;
}

.hero-visual {
  position: absolute;
  top: 50%;
  right: 10%;
  transform: translateY(-50%);
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.floating-card {
  width: 160px;
  height: 120px;
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  display: flex;
  flex-direction: column;
  transition: transform 0.4s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.floating-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12);
}

.card-1 { animation: float1 6s ease-in-out infinite; }
.card-2 { animation: float2 6s ease-in-out infinite 0.5s; }
.card-3 { animation: float3 6s ease-in-out infinite 1s; }

@keyframes float1 {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-20px); }
}

@keyframes float2 {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-15px); }
}

@keyframes float3 {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-25px); }
}

.card-image {
  flex: 1;
  background: linear-gradient(135deg, #0071E3 0%, #00D4AA 100%);
  opacity: 0.8;
}

.floating-card span {
  padding: 8px 12px;
  font-size: 0.8rem;
  color: var(--text-secondary);
}

/* Dashboard */
.dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 40px 24px;
}

/* Upload Section */
.upload-section { margin-bottom: 48px; }

.upload-zone {
  background: var(--bg-card);
  border: 2px dashed var(--color-secondary);
  border-radius: 16px;
  padding: 60px 40px;
  text-align: center;
  position: relative;
  transition: all 0.4s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.upload-zone:hover { border-color: var(--color-hover); }
.upload-zone.drag-over {
  border-color: var(--color-hover);
  background: rgba(59, 125, 221, 0.05);
  transform: scale(1.01);
}

.upload-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
}

.upload-icon {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(59, 125, 221, 0.15) 0%, rgba(74, 158, 255, 0.15) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.upload-icon i {
  font-size: 2.5rem;
  color: var(--color-secondary);
}

.upload-title {
  font-size: 1.25rem;
  font-weight: 600;
  color: var(--text-primary);
}

.upload-subtitle {
  font-size: 0.9rem;
  color: var(--text-tertiary);
}

.file-input { display: none; }

.selected-file {
  margin-top: 32px;
  padding: 20px;
  background: var(--bg-elevated);
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.file-info {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
}

.file-info i { color: var(--accent); font-size: 1.2rem; }

.file-name {
  color: var(--text-primary);
  font-weight: 500;
  flex: 1;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.file-size {
  color: var(--text-tertiary);
  font-size: 0.85rem;
}

.file-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.btn-icon {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: transparent;
  border: 1px solid var(--border);
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-icon:hover {
  background: var(--error);
  border-color: var(--error);
  color: #fff;
}

.upload-message {
  text-align: center;
  margin-top: 20px;
  padding: 16px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 0.95rem;
}

.upload-message.success {
  background: rgba(48, 209, 88, 0.1);
  color: var(--success);
}

.upload-message.error {
  background: rgba(255, 69, 58, 0.1);
  color: var(--error);
}

/* Selected Files */
.selected-files {
  margin-top: 32px;
  padding: 20px;
  background: var(--bg-elevated);
  border-radius: 16px;
}

.selected-files-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.selected-files-header span {
  font-weight: 500;
  color: var(--text-primary);
}

.btn-clear {
  background: transparent;
  border: none;
  color: var(--error);
  font-size: 0.85rem;
  cursor: pointer;
}

.btn-clear:hover { text-decoration: underline; }

.selected-files-list {
  max-height: 200px;
  overflow-y: auto;
  margin-bottom: 16px;
}

.selected-file-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 0;
  border-bottom: 1px solid var(--border);
}

.selected-file-item:last-child { border-bottom: none; }

.selected-file-item i { color: var(--accent); }

.selected-file-item .file-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/* Folders Section */
.folders-section { margin-bottom: 48px; }

.section-header {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 24px;
}

.section-title-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.select-all-checkbox {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 0.9rem;
  color: var(--text-secondary);
  cursor: pointer;
  user-select: none;
}

.select-all-checkbox input {
  width: 18px;
  height: 18px;
  cursor: pointer;
}

.batch-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: rgba(41, 151, 255, 0.1);
  border: 1px solid var(--accent);
  border-radius: 12px;
  flex-wrap: wrap;
}

.batch-count {
  font-weight: 500;
  color: var(--accent);
  margin-right: auto;
}

.section-header h2 {
  font-size: 1.5rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-header h2 i { color: var(--accent); }

.image-count {
  font-size: 1rem;
  color: var(--text-tertiary);
  font-weight: 400;
}

.section-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: var(--bg-elevated);
  border: 1px solid var(--border);
  border-radius: 980px;
  min-width: 200px;
}

.search-box i { color: var(--text-tertiary); }

.search-box input {
  border: none;
  background: transparent;
  color: var(--text-primary);
  font-size: 0.9rem;
  outline: none;
  width: 100%;
}

.search-box input::placeholder { color: var(--text-tertiary); }

.btn-small {
  padding: 8px 16px;
  font-size: 0.85rem;
}

.sort-select {
  padding: 8px 16px;
  border: 1px solid var(--border);
  border-radius: 980px;
  background: var(--bg-elevated);
  color: var(--text-primary);
  font-size: 0.9rem;
  cursor: pointer;
  outline: none;
}

.sort-select:focus {
  border-color: var(--accent);
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid var(--border);
  border-radius: 8px;
  background: var(--bg-elevated);
  color: var(--text-primary);
  font-size: 0.85rem;
  cursor: pointer;
  outline: none;
}

.filter-select:focus {
  border-color: var(--accent);
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
  color: var(--text-secondary);
}

.image-grid.list-view {
  grid-template-columns: 1fr;
}

.image-grid.list-view .image-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  border-radius: 12px;
}

.image-grid.list-view .image-preview {
  width: 80px;
  height: 80px;
  aspect-ratio: 1;
  flex-shrink: 0;
}

.image-grid.list-view .image-meta {
  flex: 1;
  flex-direction: row;
  align-items: center;
  gap: 16px;
  padding: 12px 16px;
}

.image-grid.list-view .image-meta .image-name {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.image-views {
  font-size: 0.8rem;
  color: var(--text-tertiary);
  white-space: nowrap;
}

.image-views i {
  margin-right: 4px;
  color: var(--accent);
  opacity: 0.7;
}

.empty-folders {
  text-align: center;
  padding: 48px;
  color: var(--text-tertiary);
}

.empty-folders i {
  font-size: 3rem;
  margin-bottom: 16px;
  opacity: 0.5;
}

.folder-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
}

.folder-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  padding: 20px;
}

.folder-card:hover {
  border-color: var(--color-secondary);
  transform: translateY(-4px);
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
}

.folder-card.active {
  border-color: var(--color-secondary);
  background: rgba(59, 125, 221, 0.05);
}

.folder-icon {
  background: linear-gradient(135deg, rgba(59, 125, 221, 0.15) 0%, rgba(74, 158, 255, 0.15) 100%);
}

.folder-icon i {
  color: var(--color-secondary);
}

.folder-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.folder-name {
  font-weight: 600;
  color: var(--text-primary);
}

.folder-count {
  font-size: 0.85rem;
  color: var(--text-tertiary);
}

.folder-delete {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: transparent;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  opacity: 0;
  transition: all 0.3s;
}

.folder-card:hover .folder-delete { opacity: 1; }

.folder-delete:hover {
  background: rgba(255, 69, 58, 0.1);
  color: var(--error);
}

/* Images Section */
.images-section {}

.folder-actions {
  display: flex;
  gap: 12px;
}

.loading, .empty-state {
  text-align: center;
  padding: 80px 40px;
  color: var(--text-tertiary);
}

.loading i {
  font-size: 2rem;
  margin-bottom: 16px;
  color: var(--accent);
  display: block;
}

.empty-icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--bg-card);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 24px;
}

.empty-icon i {
  font-size: 2.5rem;
  opacity: 0.5;
}

.empty-state p {
  font-size: 1.1rem;
  margin-bottom: 24px;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 20px;
}

.image-card {
  position: relative;
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 20px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
}

.image-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border-color: var(--accent);
}

.image-card.selected {
  border-color: var(--accent);
  box-shadow: 0 0 0 3px rgba(41, 151, 255, 0.3);
}

.image-preview {
  aspect-ratio: 4/3;
  overflow: hidden;
  background: var(--bg-secondary);
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.4s;
}

.image-card:hover .image-preview img {
  transform: scale(1.05);
}

.image-meta {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.image-name {
  font-size: 0.95rem;
  font-weight: 500;
  color: var(--text-primary);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.image-size {
  font-size: 0.8rem;
  color: var(--text-tertiary);
}

.image-actions {
  position: absolute;
  top: 12px;
  right: 12px;
  display: flex;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-card:hover .image-actions { opacity: 1; }

.image-action {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border: none;
  color: var(--text-primary);
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.image-action:hover {
  background: var(--accent);
  color: #fff;
}

.image-action.delete:hover {
  background: var(--error);
}

/* Image Checkbox */
.image-checkbox {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 10;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-card:hover .image-checkbox {
  opacity: 1;
}

.image-checkbox input {
  width: 20px;
  height: 20px;
  cursor: pointer;
  accent-color: var(--accent);
}

.image-card.selected .image-checkbox {
  opacity: 1;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 3000;
  animation: fadeIn 0.2s ease;
}

.modal {
  background: #ffffff;
  border: 1px solid var(--border);
  border-radius: 24px;
  width: 100%;
  max-width: 440px;
  margin: 24px;
  overflow: hidden;
  animation: slideUp 0.3s cubic-bezier(0.25, 0.1, 0.25, 1);
}

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid var(--border);
}

.modal-header h3 {
  font-size: 1.25rem;
  font-weight: 600;
}

.modal-close {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--bg-elevated);
  border: none;
  color: var(--text-secondary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.modal-close:hover {
  background: var(--text-primary);
  color: var(--bg-primary);
}

.modal-body { padding: 24px; }

.modal-footer {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding: 24px;
  border-top: 1px solid var(--border);
}

.folder-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.folder-option {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background: var(--bg-elevated);
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.folder-option:hover {
  background: var(--bg-secondary);
}

.folder-option i {
  color: var(--accent);
  font-size: 1.2rem;
}

.folder-option span {
  font-weight: 500;
}

/* Trash Section */
.trash-section {
  margin-top: 48px;
  padding-top: 32px;
  border-top: 1px solid var(--border);
}

.trash-actions {
  display: flex;
  gap: 12px;
}

.trash-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 16px;
  margin-top: 24px;
}

.trash-card {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 16px;
  overflow: hidden;
  transition: all 0.3s;
}

.trash-card:hover {
  border-color: var(--error);
}

.trash-preview {
  aspect-ratio: 4/3;
  overflow: hidden;
  background: var(--bg-secondary);
}

.trash-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: grayscale(50%);
}

.trash-info {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.trash-name {
  font-size: 0.9rem;
  font-weight: 500;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.trash-date {
  font-size: 0.8rem;
  color: var(--text-tertiary);
}

.trash-actions-btns {
  display: flex;
  gap: 8px;
  padding: 0 12px 12px;
}

.btn-action {
  flex: 1;
  padding: 8px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-action.restore {
  background: rgba(48, 209, 88, 0.1);
  color: var(--success);
}

.btn-action.restore:hover {
  background: var(--success);
  color: #fff;
}

.btn-action.delete {
  background: rgba(255, 69, 58, 0.1);
  color: var(--error);
}

.btn-action.delete:hover {
  background: var(--error);
  color: #fff;
}

.empty-trash {
  text-align: center;
  padding: 48px;
  color: var(--text-tertiary);
}

.empty-trash i {
  font-size: 3rem;
  margin-bottom: 16px;
  opacity: 0.5;
}

/* Tags Section */
.tags-section {
  margin-bottom: 48px;
}

.empty-tags {
  text-align: center;
  padding: 32px;
  color: var(--text-tertiary);
}

.empty-tags i {
  font-size: 2rem;
  margin-bottom: 8px;
  opacity: 0.5;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
}

.tag-item {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 20px;
}

.tag-item:hover {
  border-color: var(--color-secondary);
}

.tag-item.active {
  background: rgba(59, 125, 221, 0.1);
  border-color: var(--color-secondary);
}

.tag-color {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  flex-shrink: 0;
}

.tag-name {
  font-size: 0.9rem;
  font-weight: 500;
}

.tag-delete {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: transparent;
  border: none;
  color: var(--text-tertiary);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.75rem;
  opacity: 0;
  transition: all 0.3s;
}

.tag-item:hover .tag-delete {
  opacity: 1;
}

.tag-delete:hover {
  background: rgba(255, 69, 58, 0.1);
  color: var(--error);
}

.clear-filter {
  margin-left: auto;
}

/* Image Tags */
.image-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-top: 4px;
}

.image-tag {
  display: inline-block;
  padding: 2px 8px;
  font-size: 0.7rem;
  border-radius: 10px;
  border: 1px solid;
}

.image-tag.more {
  background: var(--bg-elevated);
  color: var(--text-tertiary);
  border-color: var(--border);
}

/* Tag Modal */
.color-picker {
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-input {
  width: 48px;
  height: 48px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  padding: 0;
}

.color-value {
  font-family: monospace;
  color: var(--text-secondary);
}

.tag-preview {
  display: inline-block;
  padding: 8px 16px;
  font-size: 0.9rem;
  font-weight: 500;
  border-radius: 16px;
  border: 1px solid;
}

.empty-tags-modal {
  text-align: center;
  padding: 24px;
  color: var(--text-tertiary);
}

.tag-manage-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.tag-manage-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: var(--bg-elevated);
  border-radius: 12px;
}

.tag-manage-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.tag-manage-info .tag-name {
  font-weight: 500;
}

.btn.remove {
  background: rgba(255, 69, 58, 0.1);
  color: var(--error);
}

.btn.remove:hover {
  background: var(--error);
  color: #fff;
}

/* Edit Modal */
.modal textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid var(--border);
  border-radius: 12px;
  font-size: 0.9rem;
  color: var(--text-primary);
  background: var(--bg-elevated);
  resize: vertical;
  font-family: inherit;
  outline: none;
  transition: border-color 0.3s;
}

.modal textarea:focus {
  border-color: var(--accent);
}

.checkbox-label {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.checkbox-label input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  accent-color: var(--accent);
}
</style>