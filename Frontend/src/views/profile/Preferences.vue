<template>
  <div class="preferences-page">
    <!-- Message Display -->
    <div v-if="message.text" :class="['message', message.type]">
      {{ message.text }}
    </div>

    <!-- Link Format Section -->
    <div class="section-card">
      <h3 class="section-title">默认链接格式</h3>
      <p class="section-desc">选择图片链接的默认输出格式</p>

      <div class="setting-row">
        <div class="setting-info">
          <div class="setting-label">链接格式</div>
          <div class="setting-desc">选择复制图片链接时使用的格式</div>
        </div>
        <div class="setting-control">
          <select v-model="settings.linkFormat" class="form-select">
            <option value="markdown">Markdown</option>
            <option value="url">URL</option>
            <option value="html">HTML</option>
          </select>
        </div>
      </div>

      <div class="preview-box">
        <strong>预览:</strong>
        <code>{{ previewLink }}</code>
      </div>
    </div>

    <!-- Theme Section -->
    <div class="section-card">
      <h3 class="section-title">主题设置</h3>
      <p class="section-desc">自定义您的界面主题偏好</p>

      <div class="theme-options">
        <div
          v-for="option in themeOptions"
          :key="option.value"
          :class="['theme-card', { active: settings.theme === option.value }]"
          @click="settings.theme = option.value"
        >
          <i :class="option.icon"></i>
          <span>{{ option.label }}</span>
        </div>
      </div>
    </div>

    <!-- Upload Defaults Section -->
    <div class="section-card">
      <h3 class="section-title">上传默认设置</h3>
      <p class="section-desc">配置图片上传时的默认处理选项</p>

      <div class="toggle-row">
        <div class="toggle-label">
          <span>启用水印</span>
          <span>上传图片时自动添加水印</span>
        </div>
        <label class="toggle-switch">
          <input type="checkbox" v-model="settings.watermarkEnabled">
          <span class="slider"></span>
        </label>
      </div>

      <div v-if="settings.watermarkEnabled" class="watermark-input">
        <input
          type="text"
          v-model="settings.watermarkText"
          placeholder="输入水印文本"
        >
      </div>

      <div class="toggle-row">
        <div class="toggle-label">
          <span>启用压缩</span>
          <span>上传图片时自动压缩以节省存储空间</span>
        </div>
        <label class="toggle-switch">
          <input type="checkbox" v-model="settings.compressEnabled">
          <span class="slider"></span>
        </label>
      </div>
    </div>

    <!-- Save Button -->
    <button class="btn-save" @click="saveSettings" :disabled="saving">
      {{ saving ? '保存中...' : '保存设置' }}
    </button>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import api from '../../api'

const userId = localStorage.getItem('userId') || ''

const settings = ref({
  linkFormat: 'markdown',
  theme: 'light',
  watermarkEnabled: false,
  watermarkText: '',
  compressEnabled: false
})

const saving = ref(false)
const message = ref({ text: '', type: '' })

const themeOptions = [
  { value: 'light', label: '浅色模式', icon: 'fas fa-sun' },
  { value: 'dark', label: '深色模式', icon: 'fas fa-moon' },
  { value: 'system', label: '跟随系统', icon: 'fas fa-cog' }
]

const previewLink = computed(() => {
  const exampleUrl = 'https://example.com/image.jpg'
  const filename = 'image.jpg'

  switch (settings.value.linkFormat) {
    case 'markdown':
      return `![${filename}](${exampleUrl})`
    case 'url':
      return exampleUrl
    case 'html':
      return `<img src="${exampleUrl}" alt="${filename}">`
    default:
      return exampleUrl
  }
})

const fetchSettings = async () => {
  if (!userId) return

  try {
    const response = await api.get(`/api/user/${userId}/settings`)
    if (response.data) {
      settings.value = { ...settings.value, ...response.data }
    }
  } catch (err) {
    console.warn('Failed to fetch settings, using defaults')
  }
}

const saveSettings = async () => {
  if (!userId) return

  saving.value = true
  message.value = { text: '', type: '' }

  try {
    await api.put(`/api/user/${userId}/settings`, settings.value)
    message.value = { text: '设置保存成功', type: 'success' }
    setTimeout(() => {
      message.value = { text: '', type: '' }
    }, 3000)
  } catch (err) {
    message.value = { text: '保存设置失败，请重试', type: 'error' }
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  fetchSettings()
})
</script>

<style scoped>
.preferences-page {
  max-width: 800px;
  margin: 0 auto;
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
  margin-bottom: 8px;
}

.section-desc {
  font-size: 0.9rem;
  color: #64748B;
  margin-bottom: 20px;
}

.setting-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px 0;
  border-bottom: 1px solid #F5F7FA;
}

.setting-row:last-child {
  border-bottom: none;
}

.setting-info {
  flex: 1;
}

.setting-label {
  font-size: 0.95rem;
  font-weight: 500;
  color: #1E293B;
  margin-bottom: 4px;
}

.setting-desc {
  font-size: 0.85rem;
  color: #64748B;
}

.setting-control {
  margin-left: 24px;
}

.form-select {
  padding: 10px 36px 10px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 10px;
  font-size: 0.9rem;
  background: #fff url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='12' height='12' viewBox='0 0 12 12'%3E%3Cpath fill='%2364748B' d='M6 8L1 3h10z'/%3E%3C/svg%3E") no-repeat right 12px center;
  appearance: none;
  cursor: pointer;
}

.form-select:focus {
  outline: none;
  border-color: #3B7DDD;
}

.preview-box {
  margin-top: 12px;
  padding: 16px;
  background: #F5F7FA;
  border-radius: 12px;
  font-size: 0.85rem;
  color: #64748B;
}

.preview-box code {
  background: rgba(59, 125, 221, 0.1);
  color: #3B7DDD;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: monospace;
}

.theme-options {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.theme-card {
  flex: 1;
  padding: 16px;
  border: 2px solid #E2E8F0;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s;
  text-align: center;
}

.theme-card:hover {
  border-color: #3B7DDD;
}

.theme-card.active {
  border-color: #3B7DDD;
  background: rgba(59, 125, 221, 0.05);
}

.theme-card i {
  font-size: 24px;
  margin-bottom: 8px;
  color: #64748B;
}

.theme-card.active i {
  color: #3B7DDD;
}

.theme-card span {
  font-size: 0.85rem;
  color: #64748B;
}

.theme-card.active span {
  color: #3B7DDD;
  font-weight: 500;
}

.toggle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
}

.toggle-label {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.toggle-label span:first-child {
  font-size: 0.95rem;
  color: #1E293B;
  font-weight: 500;
}

.toggle-label span:last-child {
  font-size: 0.8rem;
  color: #64748B;
}

.toggle-switch {
  position: relative;
  width: 48px;
  height: 26px;
}

.toggle-switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.toggle-switch .slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #E2E8F0;
  border-radius: 26px;
  transition: 0.3s;
}

.toggle-switch .slider:before {
  position: absolute;
  content: "";
  height: 20px;
  width: 20px;
  left: 3px;
  bottom: 3px;
  background: #fff;
  border-radius: 50%;
  transition: 0.3s;
}

.toggle-switch input:checked + .slider {
  background: #3B7DDD;
}

.toggle-switch input:checked + .slider:before {
  transform: translateX(22px);
}

.watermark-input {
  margin-top: 12px;
}

.watermark-input input {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  font-size: 0.9rem;
}

.watermark-input input:focus {
  outline: none;
  border-color: #3B7DDD;
  box-shadow: 0 0 0 3px rgba(59, 125, 221, 0.15);
}

.btn-save {
  padding: 14px 32px;
  background: #3B7DDD;
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-save:hover {
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
  margin-bottom: 16px;
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
