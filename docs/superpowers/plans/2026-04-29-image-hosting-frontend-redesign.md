# 图片托管平台前端重设计实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 对现有Vue.js图片托管平台进行整体重新设计，采用AdminLTE蓝色系风格，打造专业现代的图片管理界面。

**Architecture:** 保持现有Vue 3 + Vite技术栈，仅更新UI样式和布局。新CSS变量系统替换现有变量，组件结构和业务逻辑保持不变。

**Tech Stack:** Vue 3, Vite, Font Awesome 6, 现有API接口

---

## 文件结构

| 文件 | 改动类型 | 职责 |
|------|---------|------|
| `Frontend/src/App.vue` | 修改 | 主布局、顶部导航栏、CSS变量定义、图片查看器样式 |
| `Frontend/src/views/Home.vue` | 修改 | 首页/仪表盘全部模块（上传、文件夹、标签、图片网格、弹窗、回收站） |
| `Frontend/src/views/Login.vue` | 修改 | 登录页（蓝色主题） |
| `Frontend/src/views/Register.vue` | 修改 | 注册页（蓝色主题） |
| `Frontend/src/views/Admin.vue` | 修改 | 管理后台（蓝色主题统计卡片） |

---

## Task 1: 更新App.vue - 主布局和导航栏

**Files:**
- Modify: `Frontend/src/App.vue:197-300` (CSS变量和导航栏样式)

- [ ] **Step 1: 更新CSS变量为蓝色系**

替换现有的`:root` CSS变量块：

```css
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
```

- [ ] **Step 2: 更新顶部导航栏样式**

替换`.top-nav`样式为深蓝色：

```css
.top-nav {
  background: var(--color-primary);
  backdrop-filter: none;
  -webkit-backdrop-filter: none;
  border-bottom: none;
}

.nav-link {
  color: rgba(255, 255, 255, 0.8);
}

.nav-link:hover {
  color: #fff;
}

.nav-link.router-link-active {
  color: #fff;
}

.user-name {
  color: rgba(255, 255, 255, 0.9);
}

.btn-logout {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.btn-logout:hover {
  background: rgba(255, 255, 255, 0.2);
  border-color: rgba(255, 255, 255, 0.3);
}
```

- [ ] **Step 3: 更新品牌Logo文字渐变**

```css
.brand-text {
  background: linear-gradient(135deg, #fff 0%, rgba(255,255,255,0.8) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
```

- [ ] **Step 4: 更新图片查看器遮罩和按钮样式**

```css
.image-viewer {
  background: rgba(30, 58, 95, 0.95);
}

.viewer-close {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.viewer-close:hover {
  background: rgba(255, 255, 255, 0.2);
}

.viewer-info {
  background: rgba(30, 58, 95, 0.9);
  border-color: rgba(255, 255, 255, 0.1);
}

.viewer-controls {
  background: rgba(30, 58, 95, 0.9);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.control-btn {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.2);
  color: #fff;
}

.control-btn:hover {
  background: var(--color-secondary);
  border-color: var(--color-secondary);
}
```

- [ ] **Step 5: 提交**

```bash
git add Frontend/src/App.vue
git commit -m "feat: update App.vue with AdminLTE blue theme for navbar and viewer"
```

---

## Task 2: 更新Home.vue - 首页/仪表盘（上传区域）

**Files:**
- Modify: `Frontend/src/views/Home.vue:1409-1700` (上传区域样式)

- [ ] **Step 1: 更新上传区域样式**

替换`.upload-zone`样式：

```css
.upload-zone {
  background: var(--bg-card);
  border: 2px dashed var(--color-secondary);
  border-radius: 16px;
  padding: 60px 40px;
}

.upload-zone:hover {
  border-color: var(--color-hover);
}

.upload-zone.drag-over {
  border-color: var(--color-hover);
  background: rgba(59, 125, 221, 0.05);
  transform: scale(1.01);
}

.upload-icon {
  background: linear-gradient(135deg, rgba(59, 125, 221, 0.15) 0%, rgba(74, 158, 255, 0.15) 100%);
}

.upload-icon i {
  color: var(--color-secondary);
}
```

- [ ] **Step 2: 更新主按钮和次按钮样式**

在`<style>`中添加：

```css
.btn-primary {
  background: var(--color-secondary);
  color: #fff;
}

.btn-primary:hover {
  background: var(--color-hover);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 125, 221, 0.4);
}

.btn-secondary {
  background: var(--bg-card);
  color: var(--text-primary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover {
  border-color: var(--color-secondary);
  color: var(--color-secondary);
}
```

- [ ] **Step 3: 提交**

```bash
git add Frontend/src/views/Home.vue
git commit -m "feat: update Home.vue upload section with blue theme"
```

---

## Task 3: 更新Home.vue - 文件夹和标签区域

**Files:**
- Modify: `Frontend/src/views/Home.vue:1734-1990` (文件夹和标签样式)

- [ ] **Step 1: 更新文件夹卡片样式**

```css
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
```

- [ ] **Step 2: 更新标签样式**

```css
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

.tag-delete:hover {
  background: rgba(239, 68, 68, 0.1);
  color: var(--error);
}
```

- [ ] **Step 3: 提交**

```bash
git add Frontend/src/views/Home.vue
git commit -m "feat: update Home.vue folder and tag sections with blue theme"
```

---

## Task 4: 更新Home.vue - 图片网格和卡片

**Files:**
- Modify: `Frontend/src/views/Home.vue:2016-2200` (图片网格样式)

- [ ] **Step 1: 更新图片卡片样式**

```css
.image-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
  overflow: hidden;
}

.image-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
  border-color: var(--color-secondary);
}

.image-card.selected {
  border-color: var(--color-secondary);
  box-shadow: 0 0 0 3px rgba(59, 125, 221, 0.3);
}

.image-action:hover {
  background: var(--color-secondary);
  color: #fff;
}

.image-action.delete:hover {
  background: var(--error);
}
```

- [ ] **Step 2: 更新图片操作按钮**

```css
.image-actions {
  opacity: 0;
}

.image-card:hover .image-actions {
  opacity: 1;
}

.image-checkbox {
  opacity: 0;
}

.image-card:hover .image-checkbox,
.image-card.selected .image-checkbox {
  opacity: 1;
}
```

- [ ] **Step 3: 提交**

```bash
git add Frontend/src/views/Home.vue
git commit -m "feat: update Home.vue image grid with blue theme"
```

---

## Task 5: 更新Home.vue - 弹窗和回收站

**Files:**
- Modify: `Frontend/src/views/Home.vue:2183-2400` (弹窗、回收站样式)

- [ ] **Step 1: 更新Modal弹窗样式**

```css
.modal-overlay {
  background: rgba(30, 58, 95, 0.7);
  backdrop-filter: blur(8px);
}

.modal {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 20px;
}

.modal-close:hover {
  background: var(--color-secondary);
  color: #fff;
}
```

- [ ] **Step 2: 更新回收站样式**

```css
.trash-section {
  border-top: 1px solid var(--border-color);
}

.trash-card:hover {
  border-color: var(--error);
}

.btn-action.restore:hover {
  background: var(--success);
  color: #fff;
}

.btn-action.delete:hover {
  background: var(--error);
  color: #fff;
}
```

- [ ] **Step 3: 提交**

```bash
git add Frontend/src/views/Home.vue
git commit -m "feat: update Home.vue modals and trash section with blue theme"
```

---

## Task 6: 更新Login.vue和Register.vue - 认证页

**Files:**
- Modify: `Frontend/src/views/Login.vue` (登录页样式)
- Modify: `Frontend/src/views/Register.vue` (注册页样式)

- [ ] **Step 1: 更新Login.vue样式**

替换`<style scoped>`中的认证页样式为蓝色主题：

```css
.auth-page {
  background: linear-gradient(135deg, #1E3A5F 0%, #3B7DDD 100%);
  min-height: 100vh;
}

.auth-container {
  background: var(--bg-card);
  border-radius: 24px;
  padding: 48px;
  box-shadow: 0 25px 80px rgba(0, 0, 0, 0.3);
}

.auth-icon {
  background: linear-gradient(135deg, rgba(59, 125, 221, 0.15) 0%, rgba(74, 158, 255, 0.15) 100%);
  color: var(--color-secondary);
}

.auth-header h1 {
  color: var(--text-primary);
}

.auth-form .btn-primary {
  background: var(--color-secondary);
}

.auth-form .btn-primary:hover {
  background: var(--color-hover);
}
```

- [ ] **Step 2: 更新Register.vue样式**

与Login.vue类似的蓝色主题更新，替换背景和按钮样式。

- [ ] **Step 3: 提交**

```bash
git add Frontend/src/views/Login.vue Frontend/src/views/Register.vue
git commit -m "feat: update auth pages with AdminLTE blue theme"
```

---

## Task 7: 更新Admin.vue - 管理后台

**Files:**
- Modify: `Frontend/src/views/Admin.vue` (管理后台样式)

- [ ] **Step 1: 更新Admin.vue样式**

替换stat-card和section标题为蓝色主题：

```css
.admin-page {
  background: var(--bg-main);
}

.stat-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 16px;
}

.stat-icon {
  background: linear-gradient(135deg, rgba(59, 125, 221, 0.15) 0%, rgba(74, 158, 255, 0.15) 100%);
  color: var(--color-secondary);
}

.stat-icon.users {
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.15) 0%, rgba(74, 158, 255, 0.15) 100%);
  color: var(--success);
}

.stat-icon.storage {
  background: linear-gradient(135deg, rgba(255, 149, 0, 0.15) 0%, rgba(239, 68, 68, 0.15) 100%);
  color: #ff9500;
}

.stat-icon.trash {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.15) 0%, rgba(255, 149, 0, 0.15) 100%);
  color: var(--error);
}

.user-card {
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 12px;
}

.user-role {
  background: rgba(59, 125, 221, 0.1);
  color: var(--color-secondary);
}

.user-role.admin {
  background: rgba(255, 149, 0, 0.1);
  color: #ff9500;
}
```

- [ ] **Step 2: 提交**

```bash
git add Frontend/src/views/Admin.vue
git commit -m "feat: update Admin.vue with blue theme"
```

---

## Task 8: 全局样式和细节优化

**Files:**
- Modify: `Frontend/src/App.vue` (全局样式)

- [ ] **Step 1: 更新Hero区域样式（如有）**

如果Home.vue中有.hero相关样式，更新为蓝色渐变：

```css
.hero {
  background: linear-gradient(180deg, #1E3A5F 0%, #3B7DDD 50%, #F5F7FA 100%);
}

.hero-headline {
  color: #fff;
}

.hero-subheadline {
  color: rgba(255, 255, 255, 0.85);
}

.floating-card {
  background: rgba(255, 255, 255, 0.95);
}
```

- [ ] **Step 2: 统一圆角规格**

确保所有卡片使用16px圆角，按钮使用8px圆角。

- [ ] **Step 3: 提交**

```bash
git add Frontend/src/App.vue Frontend/src/views/Home.vue
git commit -m "feat: finalize global styles and hero section with blue theme"
```

---

## Task 9: 视觉验证

- [ ] **Step 1: 启动开发服务器**

```bash
cd Frontend && npm run dev
```

- [ ] **Step 2: 验证以下页面**

- [ ] 首页未登录状态（Hero区域）
- [ ] 登录页
- [ ] 注册页
- [ ] 登录后首页（上传区、文件夹、标签、图片网格）
- [ ] 图片查看器
- [ ] 各种弹窗（新建文件夹、新建标签、移动图片等）
- [ ] 回收站
- [ ] 管理后台

- [ ] **Step 3: 验证响应式布局**

- [ ] 大屏（>1200px）4列图片网格
- [ ] 中屏（768-1200px）3列
- [ ] 小屏（<768px）2列或1列

---

## 实现顺序

1. Task 1: App.vue - 主布局和导航栏（先改全局变量和导航）
2. Task 2: Home.vue - 上传区域
3. Task 3: Home.vue - 文件夹和标签
4. Task 4: Home.vue - 图片网格
5. Task 5: Home.vue - 弹窗和回收站
6. Task 6: Login.vue & Register.vue
7. Task 7: Admin.vue
8. Task 8: 全局优化
9. Task 9: 视觉验证
