<template>
  <div class="logs-page">
    <!-- Page Header -->
    <div class="page-header">
      <h1 class="page-title">上传记录</h1>
      <div class="filter-tabs">
        <button
          v-for="tab in filterTabs"
          :key="tab.value"
          class="filter-tab"
          :class="{ active: selectedRange === tab.value }"
          @click="selectRange(tab.value)"
        >
          {{ tab.label }}
        </button>
      </div>
    </div>

    <!-- Stats Summary Row -->
    <div class="stats-row">
      <div class="stat-card">
        <div class="stat-value">{{ stats.total }}</div>
        <div class="stat-label">总上传数</div>
      </div>
      <div class="stat-card">
        <div class="stat-value success">{{ stats.successRate }}%</div>
        <div class="stat-label">成功率</div>
      </div>
      <div class="stat-card">
        <div class="stat-value">{{ formatSize(stats.totalSize) }}</div>
        <div class="stat-label">总上传大小</div>
      </div>
    </div>

    <!-- Logs Table Card -->
    <div class="logs-card">
      <table v-if="logs.length > 0" class="logs-table">
        <thead>
          <tr>
            <th>时间</th>
            <th>文件名</th>
            <th>大小</th>
            <th>格式</th>
            <th>状态</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="log in logs" :key="log.id">
            <td>{{ formatDate(log.uploadedAt) }}</td>
            <td>
              <div class="filename">
                <div class="filename-icon">
                  <i class="fas fa-image"></i>
                </div>
                <span>{{ log.filename }}</span>
              </div>
            </td>
            <td>{{ formatSize(log.fileSize) }}</td>
            <td>{{ getFormat(log.contentType) }}</td>
            <td>
              <span class="status-badge" :class="log.status === 'success' ? 'success' : 'failed'">
                <i :class="log.status === 'success' ? 'fas fa-check' : 'fas fa-times'"></i>
                {{ log.status === 'success' ? '成功' : '失败' }}
              </span>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- Empty State -->
      <div v-else class="empty-state">
        <i class="fas fa-inbox"></i>
        <h3>暂无上传记录</h3>
        <p>在选定的时间范围内没有上传记录</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const userId = localStorage.getItem('userId') || ''
const selectedRange = ref('all')
const logs = ref([])

const filterTabs = [
  { label: '全部', value: 'all' },
  { label: '今日', value: 'day' },
  { label: '本周', value: 'week' },
  { label: '本月', value: 'month' }
]

const stats = computed(() => {
  const total = logs.value.length
  const successful = logs.value.filter(log => log.status === 'success').length
  const successRate = total > 0 ? Math.round((successful / total) * 100) : 0
  const totalSize = logs.value.reduce((sum, log) => sum + (log.fileSize || 0), 0)
  return { total, successRate, totalSize }
})

const formatSize = (bytes) => {
  if (!bytes || bytes === 0) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}`
}

const getFormat = (contentType) => {
  if (!contentType) return '-'
  const formatMap = {
    'image/jpeg': 'JPEG',
    'image/jpg': 'JPG',
    'image/png': 'PNG',
    'image/gif': 'GIF',
    'image/webp': 'WebP',
    'image/svg+xml': 'SVG',
    'image/bmp': 'BMP'
  }
  return formatMap[contentType] || contentType.split('/')[1]?.toUpperCase() || '-'
}

const fetchLogs = async () => {
  if (!userId) return
  try {
    const response = await axios.get(`/api/user/${userId}/upload-logs?range=${selectedRange.value}`)
    logs.value = response.data || []
  } catch (error) {
    console.error('Failed to fetch upload logs:', error)
    logs.value = []
  }
}

const selectRange = (range) => {
  selectedRange.value = range
  fetchLogs()
}

onMounted(() => {
  fetchLogs()
})
</script>

<style scoped>
.logs-page {
  max-width: 1100px;
  margin: 0 auto;
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
  margin: 0;
}

.filter-tabs {
  display: flex;
  gap: 8px;
}

.filter-tab {
  padding: 8px 16px;
  border-radius: 8px;
  background: #F5F7FA;
  border: none;
  color: #64748B;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tab:hover {
  border-color: #3B7DDD;
  color: #3B7DDD;
}

.filter-tab.active {
  background: #3B7DDD;
  color: #fff;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #E2E8F0;
}

.stat-card .stat-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1E293B;
  margin-bottom: 4px;
}

.stat-card .stat-value.success {
  color: #22C55E;
}

.stat-card .stat-label {
  font-size: 0.85rem;
  color: #64748B;
}

.logs-card {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #E2E8F0;
  overflow: hidden;
}

.logs-table {
  width: 100%;
  border-collapse: collapse;
}

.logs-table th {
  text-align: left;
  padding: 16px 20px;
  background: #F5F7FA;
  color: #64748B;
  font-size: 0.85rem;
  font-weight: 500;
  border-bottom: 1px solid #E2E8F0;
}

.logs-table td {
  padding: 16px 20px;
  border-bottom: 1px solid #F5F7FA;
  color: #1E293B;
  font-size: 0.9rem;
}

.logs-table tr:last-child td {
  border-bottom: none;
}

.logs-table tr:hover {
  background: #F5F7FA;
}

.filename {
  display: flex;
  align-items: center;
  gap: 8px;
}

.filename-icon {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: rgba(59, 125, 221, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.filename-icon i {
  color: #3B7DDD;
  font-size: 14px;
}

.status-badge {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 500;
}

.status-badge.success {
  background: rgba(34, 197, 94, 0.1);
  color: #22C55E;
}

.status-badge.failed {
  background: rgba(239, 68, 68, 0.1);
  color: #EF4444;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #64748B;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  color: #94A3B8;
}

.empty-state h3 {
  font-size: 1.1rem;
  font-weight: 500;
  margin-bottom: 8px;
  color: #1E293B;
}

.empty-state p {
  font-size: 0.9rem;
  margin: 0;
}
</style>
