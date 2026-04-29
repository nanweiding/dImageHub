<template>
  <div class="security-page">
    <!-- Password Change Section -->
    <div class="section-card">
      <h2 class="section-title">修改密码</h2>
      <p class="section-subtitle">定期更换密码可以保护账户安全</p>

      <div v-if="message.text" :class="['message', message.type]">
        {{ message.text }}
      </div>

      <div class="form-group">
        <label>当前密码</label>
        <input
          type="password"
          v-model="passwordForm.currentPassword"
          placeholder="请输入当前密码"
        />
      </div>

      <div class="form-row">
        <div class="form-group">
          <label>新密码</label>
          <input
            type="password"
            v-model="passwordForm.newPassword"
            placeholder="请输入新密码"
            @input="updatePasswordStrength"
          />
        </div>
        <div class="form-group">
          <label>确认密码</label>
          <input
            type="password"
            v-model="passwordForm.confirmPassword"
            placeholder="请再次输入新密码"
          />
        </div>
      </div>

      <div class="form-group">
        <label>密码强度</label>
        <div :class="['strength-bar', passwordStrength.level]">
          <div class="bar"></div>
          <div class="bar"></div>
          <div class="bar"></div>
        </div>
        <span :class="['strength-text', passwordStrength.level]">
          {{ passwordStrength.text }}
        </span>
      </div>

      <button class="btn-change" @click="changePassword" :disabled="changingPassword">
        {{ changingPassword ? '修改中...' : '修改密码' }}
      </button>
    </div>

    <!-- Access Permissions Section -->
    <div class="section-card">
      <h2 class="section-title">访问权限</h2>
      <p class="section-subtitle">设置图片的默认访问权限</p>

      <div class="toggle-row">
        <div class="toggle-label">
          <span>默认公开访问</span>
          <span>开启后，新上传的图片默认设为公开访问</span>
        </div>
        <label class="toggle-switch">
          <input type="checkbox" v-model="permissionForm.defaultPublic" />
          <span class="slider"></span>
        </label>
      </div>

      <button class="btn-change" @click="updatePermission" :disabled="savingPermission">
        {{ savingPermission ? '保存中...' : '保存设置' }}
      </button>
    </div>

    <!-- Hotlink Protection Section -->
    <div class="section-card">
      <h2 class="section-title">防盗链设置</h2>
      <p class="section-subtitle">配置图片防盗链策略，保护资源不被非法引用</p>

      <div class="form-group">
        <label>Referer 白名单</label>
        <textarea
          v-model="hotlinkForm.refererWhitelist"
          placeholder="输入允许引用资源的域名，每行一个，例如：&#10;example.com&#10;www.example.com"
        ></textarea>
        <span class="helper-text">留空表示不限制，仅在防盗链开启时生效</span>
      </div>

      <div class="toggle-row">
        <div class="toggle-label">
          <span>Token 访问令牌</span>
          <span>启用后，访问图片需要携带有效的 Token</span>
        </div>
        <label class="toggle-switch">
          <input type="checkbox" v-model="hotlinkForm.tokenEnabled" />
          <span class="slider"></span>
        </label>
      </div>

      <button class="btn-change" @click="updateHotlink" :disabled="savingHotlink">
        {{ savingHotlink ? '保存中...' : '保存设置' }}
      </button>
    </div>

    <!-- Login Devices Section -->
    <div class="section-card">
      <h2 class="section-title">登录设备</h2>
      <p class="section-subtitle">查看和管理登录过的设备</p>

      <div class="device-list">
        <div v-for="device in devices" :key="device.id" class="device-item">
          <div class="device-info">
            <div class="device-icon">
              <i :class="getDeviceIcon(device.deviceName)"></i>
            </div>
            <div class="device-details">
              <span class="device-name">{{ device.deviceName }}</span>
              <span class="device-meta">{{ device.ipAddress }} · {{ formatLastActive(device.lastActive) }}</span>
            </div>
          </div>
          <div class="device-actions">
            <span v-if="device.isCurrent" class="device-badge">当前设备</span>
            <button
              v-else
              class="btn-logout-device"
              @click="logoutDevice(device.id)"
            >
              退出登录
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

const userId = localStorage.getItem('userId') || ''

// Password form
const passwordForm = ref({
  currentPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const changingPassword = ref(false)
const message = ref({ type: '', text: '' })

// Password strength
const passwordStrength = computed(() => {
  const password = passwordForm.value.newPassword
  if (!password) return { level: '', text: '' }

  let score = 0
  if (password.length >= 8) score++
  if (password.length >= 12) score++
  if (/[a-zA-Z]/.test(password) && /[0-9]/.test(password)) score++
  if (/[^a-zA-Z0-9]/.test(password)) score++

  if (score <= 1) return { level: 'weak', text: '弱：建议包含字母、数字和特殊字符' }
  if (score <= 2) return { level: 'medium', text: '中：密码强度一般，建议增加长度和复杂度' }
  return { level: 'strong', text: '强：密码强度良好' }
})

const updatePasswordStrength = () => {
  // Trigger reactivity by accessing the computed
}

// Permission form
const permissionForm = ref({
  defaultPublic: true
})

const savingPermission = ref(false)

// Hotlink form
const hotlinkForm = ref({
  refererWhitelist: '',
  tokenEnabled: false
})

const savingHotlink = ref(false)

// Devices list
const devices = ref([
  {
    id: '1',
    deviceName: 'Chrome on Windows',
    ipAddress: '192.168.1.100',
    lastActive: new Date().toISOString(),
    isCurrent: true
  },
  {
    id: '2',
    deviceName: 'Safari on iPhone',
    ipAddress: '192.168.1.101',
    lastActive: new Date(Date.now() - 86400000).toISOString(),
    isCurrent: false
  }
])

const getDeviceIcon = (deviceName) => {
  if (deviceName.toLowerCase().includes('iphone') || deviceName.toLowerCase().includes('android')) {
    return 'fas fa-mobile-alt'
  }
  if (deviceName.toLowerCase().includes('ipad') || deviceName.toLowerCase().includes('tablet')) {
    return 'fas fa-tablet-alt'
  }
  return 'fas fa-laptop'
}

const formatLastActive = (dateStr) => {
  if (!dateStr) return '未知'
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now - date
  const minutes = Math.floor(diff / 60000)
  const hours = Math.floor(diff / 3600000)
  const days = Math.floor(diff / 86400000)

  if (minutes < 1) return '刚刚'
  if (minutes < 60) return `${minutes} 分钟前`
  if (hours < 24) return `${hours} 小时前`
  if (days < 7) return `${days} 天前`
  return date.toLocaleDateString('zh-CN')
}

const validatePassword = () => {
  const { currentPassword, newPassword, confirmPassword } = passwordForm.value

  if (!currentPassword) {
    message.value = { type: 'error', text: '请输入当前密码' }
    return false
  }

  if (!newPassword) {
    message.value = { type: 'error', text: '请输入新密码' }
    return false
  }

  if (newPassword.length < 8) {
    message.value = { type: 'error', text: '新密码至少需要 8 个字符' }
    return false
  }

  if (!/[a-zA-Z]/.test(newPassword)) {
    message.value = { type: 'error', text: '新密码必须包含字母' }
    return false
  }

  if (!/[0-9]/.test(newPassword)) {
    message.value = { type: 'error', text: '新密码必须包含数字' }
    return false
  }

  if (newPassword !== confirmPassword) {
    message.value = { type: 'error', text: '两次输入的密码不一致' }
    return false
  }

  return true
}

const changePassword = async () => {
  if (!validatePassword()) return

  changingPassword.value = true
  message.value = { type: '', text: '' }

  try {
    // Simulate API call - in real implementation:
    // await axios.put(`/api/user/${userId}/password`, {
    //   currentPassword: passwordForm.value.currentPassword,
    //   newPassword: passwordForm.value.newPassword
    // })

    await new Promise(resolve => setTimeout(resolve, 800))

    message.value = { type: 'success', text: '密码修改成功！' }
    passwordForm.value = {
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    }

    setTimeout(() => {
      message.value = { type: '', text: '' }
    }, 3000)
  } catch (error) {
    message.value = { type: 'error', text: '密码修改失败，请检查当前密码是否正确' }
  } finally {
    changingPassword.value = false
  }
}

const updatePermission = async () => {
  savingPermission.value = true

  try {
    // Simulate API call - in real implementation:
    // await axios.put(`/api/user/${userId}/settings`, {
    //   defaultPublic: permissionForm.value.defaultPublic
    // })

    await new Promise(resolve => setTimeout(resolve, 500))

    message.value = { type: 'success', text: '权限设置已保存！' }

    setTimeout(() => {
      message.value = { type: '', text: '' }
    }, 3000)
  } catch (error) {
    message.value = { type: 'error', text: '保存失败，请重试' }
  } finally {
    savingPermission.value = false
  }
}

const updateHotlink = async () => {
  savingHotlink.value = true

  try {
    // Simulate API call - in real implementation:
    // await axios.put(`/api/user/${userId}/settings`, {
    //   refererWhitelist: hotlinkForm.value.refererWhitelist,
    //   tokenEnabled: hotlinkForm.value.tokenEnabled
    // })

    await new Promise(resolve => setTimeout(resolve, 500))

    message.value = { type: 'success', text: '防盗链设置已保存！' }

    setTimeout(() => {
      message.value = { type: '', text: '' }
    }, 3000)
  } catch (error) {
    message.value = { type: 'error', text: '保存失败，请重试' }
  } finally {
    savingHotlink.value = false
  }
}

const logoutDevice = async (deviceId) => {
  if (!confirm('确定要退出该设备吗？')) return

  try {
    // Simulate API call - in real implementation:
    // await axios.delete(`/api/user/devices/${deviceId}`)

    await new Promise(resolve => setTimeout(resolve, 300))

    devices.value = devices.value.filter(d => d.id !== deviceId)
    message.value = { type: 'success', text: '设备已退出登录' }

    setTimeout(() => {
      message.value = { type: '', text: '' }
    }, 3000)
  } catch (error) {
    message.value = { type: 'error', text: '操作失败，请重试' }
  }
}

const fetchDevices = async () => {
  if (!userId) return

  try {
    // Simulate API call - in real implementation:
    // const response = await axios.get(`/api/user/${userId}/devices`)
    // devices.value = response.data

    // Mock data for development
    await new Promise(resolve => setTimeout(resolve, 500))
  } catch (error) {
    console.warn('Failed to fetch devices')
  }
}

onMounted(() => {
  fetchDevices()
})
</script>

<style scoped>
.security-page {
  max-width: 900px;
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
  margin-bottom: 20px;
}

.section-subtitle {
  font-size: 0.9rem;
  color: #64748B;
  margin-top: -12px;
  margin-bottom: 16px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.form-group {
  margin-bottom: 16px;
}

.form-group label {
  display: block;
  margin-bottom: 6px;
  color: #64748B;
  font-size: 0.85rem;
  font-weight: 500;
}

.form-group input[type="password"],
.form-group input[type="text"],
.form-group textarea {
  width: 100%;
  padding: 12px 16px;
  border: 1px solid #E2E8F0;
  border-radius: 12px;
  font-size: 0.95rem;
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
  min-height: 80px;
}

.helper-text {
  display: block;
  font-size: 0.8rem;
  color: #94A3B8;
  margin-top: 4px;
}

.strength-bar {
  display: flex;
  gap: 4px;
  margin-top: 8px;
}

.strength-bar .bar {
  flex: 1;
  height: 4px;
  border-radius: 2px;
  background: #E2E8F0;
}

.strength-bar.weak .bar:nth-child(1) {
  background: #EF4444;
}

.strength-bar.medium .bar:nth-child(-n+2) {
  background: #F59E0B;
}

.strength-bar.strong .bar {
  background: #22C55E;
}

.strength-text {
  font-size: 0.8rem;
  margin-top: 4px;
  display: block;
}

.strength-text.weak {
  color: #EF4444;
}

.strength-text.medium {
  color: #F59E0B;
}

.strength-text.strong {
  color: #22C55E;
}

.btn-change {
  padding: 12px 24px;
  background: #3B7DDD;
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-change:hover:not(:disabled) {
  background: #2563EB;
}

.btn-change:disabled {
  background: #94A3B8;
  cursor: not-allowed;
}

.toggle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #F5F7FA;
}

.toggle-row:last-child {
  border-bottom: none;
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

.device-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.device-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: #F5F7FA;
  border-radius: 12px;
}

.device-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.device-icon {
  width: 40px;
  height: 40px;
  border-radius: 10px;
  background: rgba(59, 125, 221, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
}

.device-icon i {
  color: #3B7DDD;
  font-size: 16px;
}

.device-details {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.device-name {
  font-size: 0.95rem;
  font-weight: 500;
  color: #1E293B;
}

.device-meta {
  font-size: 0.8rem;
  color: #64748B;
}

.device-badge {
  padding: 4px 8px;
  background: rgba(34, 197, 94, 0.1);
  color: #22C55E;
  border-radius: 4px;
  font-size: 0.75rem;
  font-weight: 500;
}

.btn-logout-device {
  padding: 8px 14px;
  background: #fff;
  border: 1px solid #E2E8F0;
  color: #64748B;
  border-radius: 8px;
  font-size: 0.85rem;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-logout-device:hover {
  border-color: #EF4444;
  color: #EF4444;
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
