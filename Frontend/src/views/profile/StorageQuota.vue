<template>
  <div class="quota-page">
    <!-- Quota Overview Card -->
    <div class="quota-card">
      <div class="quota-visual">
        <!-- Ring Progress Chart -->
        <div class="ring-chart">
          <div class="ring-bg"></div>
          <div
            class="ring-progress"
            :style="{ background: ringGradient }"
          ></div>
          <div class="ring-center">
            <div class="ring-percentage">{{ usedPercentage }}%</div>
            <div class="ring-label">已使用</div>
          </div>
        </div>

        <!-- Detailed Stats -->
        <div class="quota-stats">
          <div class="stat-box">
            <div class="value" :class="{ warning: isWarning80, danger: isWarning95 || isWarning100 }">
              {{ formatStorage(storageData.usedStorage) }}
            </div>
            <div class="label">已用容量</div>
          </div>
          <div class="stat-box">
            <div class="value">
              {{ formatStorage(storageData.totalStorage - storageData.usedStorage) }}
            </div>
            <div class="label">剩余容量</div>
          </div>
          <div class="stat-box">
            <div class="value">{{ formatStorage(storageData.totalStorage) }}</div>
            <div class="label">总容量</div>
          </div>
        </div>
      </div>

      <!-- Today Uploads -->
      <div class="today-uploads">
        <h3>今日上传</h3>
        <span class="today-count">
          <i class="fas fa-cloud-upload-alt"></i>
          {{ storageData.todayUploads }} 张图片
        </span>
      </div>
    </div>

    <!-- Warning Alerts -->
    <div v-if="isWarning80" class="warning-alert" :class="warningClass">
      <i :class="warningIcon"></i>
      <div class="warning-text">
        <h4>{{ warningTitle }}</h4>
        <p>{{ warningMessage }}</p>
      </div>
    </div>

    <!-- Upgrade Section -->
    <div class="upgrade-section">
      <span class="current-plan">{{ planName }}</span>
      <br />
      <a v-if="planName === 'free'" href="/upgrade" class="btn-upgrade">
        <i class="fas fa-arrow-up"></i>
        升级到高级版
      </a>
    </div>

    <!-- 95% Warning Modal -->
    <div v-if="showUpgradeModal" class="modal-overlay" @click.self="showUpgradeModal = false">
      <div class="modal-content">
        <h3>存储空间即将用尽</h3>
        <p>您的存储空间已使用 {{ usedPercentage }}%，建议升级到高级版以获取更多存储空间。</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showUpgradeModal = false">稍后再说</button>
          <a href="/upgrade" class="btn-confirm">立即升级</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

const userId = localStorage.getItem('userId') || ''
const storageData = ref({
  usedStorage: 0,
  totalStorage: 5 * 1024 * 1024 * 1024, // Default 5GB
  todayUploads: 0,
  usedPercentage: 0
})
const showUpgradeModal = ref(false)

const planName = computed(() => {
  const totalGB = storageData.value.totalStorage / (1024 * 1024 * 1024)
  if (totalGB >= 100) return 'premium'
  if (totalGB >= 20) return 'basic'
  return 'free'
})

const isWarning80 = computed(() => storageData.value.usedPercentage >= 80)
const isWarning95 = computed(() => storageData.value.usedPercentage >= 95)
const isWarning100 = computed(() => storageData.value.usedPercentage >= 100)

const warningClass = computed(() => {
  if (isWarning100.value) return 'danger'
  if (isWarning95.value) return 'warning-95'
  if (isWarning80.value) return 'warning-80'
  return ''
})

const warningIcon = computed(() => {
  if (isWarning100.value) return 'fas fa-exclamation-circle'
  if (isWarning95.value) return 'fas fa-exclamation-triangle'
  if (isWarning80.value) return 'fas fa-exclamation-circle'
  return ''
})

const warningTitle = computed(() => {
  if (isWarning100.value) return '存储空间已满'
  if (isWarning95.value) return '存储空间即将用尽'
  if (isWarning80.value) return '存储空间使用率较高'
  return ''
})

const warningMessage = computed(() => {
  if (isWarning100.value) return '您已用完所有存储空间，无法上传新图片。请删除一些图片或升级到高级版。'
  if (isWarning95.value) return '您的存储空间即将用尽，建议立即升级以避免影响使用。'
  if (isWarning80.value) return '您的存储空间使用率已达到80%，建议您关注使用情况。'
  return ''
})

const ringGradient = computed(() => {
  const percentage = storageData.value.usedPercentage
  const color = isWarning100.value ? '#EF4444' : isWarning95.value ? '#24911522' : isWarning80.value ? '#F59E0B' : '#3B7DDD'
  return `conic-gradient(${color} 0deg ${percentage * 3.6}deg, #E2E8F0 0deg)`
})

const formatStorage = (bytes) => {
  const gb = bytes / (1024 * 1024 * 1024)
  return gb.toFixed(2) + ' GB'
}

const fetchStorageStats = async () => {
  if (!userId) return
  try {
    const response = await axios.get(`/api/user/${userId}/storage`)
    const data = response.data
    storageData.value = {
      usedStorage: data.usedStorage || 0,
      totalStorage: data.storageLimit || 5 * 1024 * 1024 * 1024,
      todayUploads: data.todayUploads || 0,
      usedPercentage: Math.round((data.usedStorage / data.storageLimit) * 100) || 0
    }
  } catch (error) {
    console.error('Failed to fetch storage stats:', error)
  }
}

onMounted(() => {
  fetchStorageStats()
})
</script>

<style scoped>
.quota-page {
  max-width: 1000px;
  margin: 0 auto;
}

.quota-card {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  border: 1px solid #E2E8F0;
}

.quota-visual {
  display: flex;
  align-items: center;
  gap: 48px;
  margin-bottom: 32px;
}

.ring-chart {
  position: relative;
  width: 180px;
  height: 180px;
  flex-shrink: 0;
}

.ring-bg {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: #F5F7FA;
  position: absolute;
}

.ring-progress {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  position: absolute;
  transition: all 0.5s;
}

.ring-center {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
}

.ring-percentage {
  font-size: 2.5rem;
  font-weight: 700;
  color: #1E293B;
}

.ring-label {
  font-size: 0.9rem;
  color: #64748B;
}

.quota-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  flex: 1;
}

.stat-box {
  background: #F5F7FA;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.stat-box .value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #1E293B;
  margin-bottom: 4px;
}

.stat-box .value.warning {
  color: #F59E0B;
}

.stat-box .value.danger {
  color: #EF4444;
}

.stat-box .label {
  font-size: 0.85rem;
  color: #64748B;
}

.today-uploads {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #E2E8F0;
}

.today-uploads h3 {
  font-size: 1rem;
  font-weight: 600;
  color: #1E293B;
  margin-bottom: 12px;
}

.today-count {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: rgba(59, 125, 221, 0.1);
  color: #3B7DDD;
  border-radius: 8px;
  font-weight: 600;
}

.warning-alert {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 12px;
  margin-bottom: 16px;
}

.warning-alert.warning-80 {
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.3);
}

.warning-alert.warning-95 {
  background: rgba(249, 115, 22, 0.1);
  border: 1px solid rgba(249, 115, 22, 0.3);
}

.warning-alert.danger {
  background: rgba(239, 68, 68, 0.1);
  border: 1px solid rgba(239, 68, 68, 0.3);
}

.warning-alert i {
  font-size: 1.25rem;
}

.warning-alert.warning-80 i {
  color: #F59E0B;
}

.warning-alert.warning-95 i {
  color: #24911522;
}

.warning-alert.danger i {
  color: #EF4444;
}

.warning-text h4 {
  font-size: 0.95rem;
  font-weight: 600;
  margin-bottom: 4px;
}

.warning-text p {
  font-size: 0.85rem;
  color: #64748B;
  margin: 0;
}

.upgrade-section {
  margin-top: 32px;
  padding-top: 24px;
  border-top: 1px solid #E2E8F0;
  text-align: center;
}

.current-plan {
  display: inline-block;
  padding: 6px 16px;
  background: rgba(59, 125, 221, 0.1);
  color: #3B7DDD;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
  margin-bottom: 16px;
}

.btn-upgrade {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: #3B7DDD;
  color: #fff;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-upgrade:hover {
  background: #2563EB;
  transform: translateY(-1px);
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: #fff;
  border-radius: 16px;
  padding: 32px;
  max-width: 400px;
  width: 90%;
}

.modal-content h3 {
  margin-bottom: 16px;
  color: #1E293B;
}

.modal-content p {
  color: #64748B;
  margin-bottom: 24px;
}

.modal-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
}

.btn-cancel {
  padding: 10px 20px;
  background: #F5F7FA;
  color: #64748B;
  border: none;
  border-radius: 8px;
  cursor: pointer;
}

.btn-confirm {
  padding: 10px 20px;
  background: #3B7DDD;
  color: #fff;
  border: none;
  border-radius: 8px;
  text-decoration: none;
  cursor: pointer;
}
</style>
