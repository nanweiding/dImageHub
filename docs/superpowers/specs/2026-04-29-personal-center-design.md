# 图床个人中心功能设计规格

## 1. 概述

本文档定义图床平台个人中心的完整功能设计，为开发、测试、产品验收提供依据。

## 2. 技术架构

### 2.1 前端架构（Vue 3）
- 主框架组件：`UserProfile.vue`（侧边栏 + 内容区）
- 子模块组件：
  - `AccountOverview.vue` - 账号概览
  - `AccountInfo.vue` - 账号信息编辑
  - `ImageManagement.vue` - 图片资源管理（复用现有逻辑）
  - `StorageQuota.vue` - 存储配额
  - `UploadLogs.vue` - 上传记录
  - `SecuritySettings.vue` - 安全隐私设置
  - `Preferences.vue` - 偏好设置

### 2.2 路由设计
```
/profile              - 个人中心首页（账号概览）
/profile/account      - 账号信息编辑
/profile/images      - 图片资源管理
/profile/quota        - 存储配额查看
/profile/logs         - 上传记录
/profile/security     - 安全与隐私设置
/profile/preferences  - 偏好设置
```

### 2.3 后端架构（Spring Boot）
- 新增Controller：`UserProfileController`、`SettingsController`、`LogController`
- 新增Service：`UserProfileService`、`SettingsService`、`LogService`
- 扩展现有UserService、ImageService

### 2.4 数据库设计

#### user_settings（用户设置表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 外键 |
| link_format | VARCHAR(20) | 默认链接格式：markdown/url/html |
| theme | VARCHAR(20) | 主题：light/dark |
| watermark_enabled | BOOLEAN | 水印开关 |
| compress_enabled | BOOLEAN | 压缩开关 |
| watermark_text | VARCHAR(100) | 水印文字 |
| created_at | TIMESTAMP | 创建时间 |
| updated_at | TIMESTAMP | 更新时间 |

#### upload_logs（上传记录表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 外键 |
| filename | VARCHAR(255) | 文件名 |
| file_size | BIGINT | 文件大小 |
| content_type | VARCHAR(50) | 格式 |
| status | VARCHAR(20) | 状态：success/failed |
| uploaded_at | TIMESTAMP | 上传时间 |

#### security_logs（安全日志表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 外键 |
| action | VARCHAR(50) | 操作类型 |
| ip_address | VARCHAR(50) | IP地址 |
| device_info | VARCHAR(255) | 设备信息 |
| created_at | TIMESTAMP | 时间 |

#### login_devices（登录设备表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| user_id | BIGINT | 外键 |
| device_name | VARCHAR(100) | 设备名称 |
| ip_address | VARCHAR(50) | IP地址 |
| last_active | TIMESTAMP | 最后活跃时间 |
| is_current | BOOLEAN | 是否当前设备 |

#### image_permissions（图片权限表）
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| image_id | BIGINT | 外键 |
| is_public | BOOLEAN | 是否公开 |
| referer_whitelist | TEXT | Referer白名单 |

## 3. 功能模块详细设计

### 3.1 账号信息模块

#### 3.1.1 账号概览
- 左侧：头像卡片（点击跳转编辑）
- 右侧：统计信息卡片（图片总数、存储用量、上传次数）
- 底部：快捷操作入口

#### 3.1.2 账号信息编辑
- 头像上传：支持JPG/PNG/GIF，最大2MB
- 昵称修改：3-20字符
- 个性签名：最多200字符
- 会员等级展示（普通/付费）
- 注册时间展示

#### 3.1.3 账号状态
- 正常状态：绿色标识
- 冻结状态：红色标识 + 提示
- 过期状态：橙色标识 + 续费入口

### 3.2 图片资源管理

#### 3.2.1 功能范围
- 全部图片展示：网格/列表视图切换
- 分类管理：自定义相册/文件夹
- 单图操作：预览、复制直链、Markdown/HTML链接、重命名、删除、下载
- 批量操作：批量选中、移动、删除、导出链接

#### 3.2.2 实现说明
- 复用现有Home.vue图片管理逻辑
- 新增路由 `/profile/images`

### 3.3 存储配额管理

#### 3.3.1 容量统计展示
- 环形进度条：已用/总容量
- 数字展示：已用GB、剩余GB、总容量GB
- 今日上传用量

#### 3.3.2 超限处理
- 80%时：黄色警告提示
- 95%时：弹窗警告，限制上传
- 100%时：阻止上传，提示升级

#### 3.3.3 套餐升级
- 免费套餐：5GB
- 付费套餐：展示价格和容量

### 3.4 上传记录管理

#### 3.4.1 上传日志
- 表格列：时间、图片名称、大小、格式、状态
- 时间倒序排列

#### 3.4.2 时间筛选
- 日：当天记录
- 周：近7天
- 月：近30天

#### 3.4.3 操作溯源
- 记录删除、修改、移动操作
- 日志表security_logs关联

### 3.5 安全与隐私设置

#### 3.5.1 密码管理
- 修改密码：原密码 + 新密码 + 确认密码
- 密码要求：至少8字符，包含字母和数字

#### 3.5.2 访问权限
- 全局默认：公开/私密
- 单图设置：公开/私密
- 私密图片仅本人可见

#### 3.5.3 防盗链设置
- Referer白名单配置
- 令牌访问开关

#### 3.5.4 登录设备管理
- 设备列表：设备名、IP、最后活跃时间
- 当前设备标识
- 下线按钮

### 3.6 偏好设置

#### 3.6.1 链接格式
- 下拉选择：Markdown / URL / HTML
- 实时预览

#### 3.6.2 主题切换
- 浅色模式 / 深色模式
- 跟随系统选项

#### 3.6.3 上传默认参数
- 水印开关 + 水印文字
- 压缩开关
- 格式转换选项

## 4. 视觉设计

### 4.1 布局结构
- 左侧固定侧边栏（240px宽）
- 右侧内容区（自适应）
- 顶部面包屑导航

### 4.2 颜色系统（AdminLTE蓝）
| 用途 | 颜色 |
|------|------|
| 主色调 | #1E3A5F |
| 强调色 | #3B7DDD |
| 成功色 | #22C55E |
| 警告色 | #F59E0B |
| 错误色 | #EF4444 |
| 背景色 | #F5F7FA |
| 卡片色 | #FFFFFF |

### 4.3 组件样式
- 卡片圆角：16px
- 按钮圆角：8px
- 输入框圆角：12px
- 阴影：0 1px 3px rgba(0,0,0,0.1)

## 5. API接口设计

### 5.1 用户设置
- `GET /api/profile/settings` - 获取用户设置
- `PUT /api/profile/settings` - 更新用户设置

### 5.2 上传日志
- `GET /api/profile/upload-logs` - 获取上传记录
- `GET /api/profile/upload-logs?range=week` - 按时间筛选

### 5.3 安全日志
- `GET /api/profile/security-logs` - 获取安全日志
- `GET /api/profile/devices` - 获取登录设备
- `DELETE /api/profile/devices/{id}` - 下线设备

### 5.4 图片权限
- `PUT /api/images/{id}/permission` - 更新图片权限
- `PUT /api/profile/default-permission` - 更新默认权限

## 6. 验收标准

1. 所有模块功能正常打开、编辑、保存无报错
2. 个人资源数据隔离，权限控制生效
3. 配额统计、日志记录、回收站数据准确无误
4. 各项配置修改实时生效，数据持久化保存
5. 响应式布局适配主流分辨率
6. 页面加载流畅，千级图片无卡顿

## 7. 实现顺序

1. 数据库表创建
2. 后端API实现
3. 前端路由和框架搭建
4. 各功能模块依次实现
5. 集成测试和优化
