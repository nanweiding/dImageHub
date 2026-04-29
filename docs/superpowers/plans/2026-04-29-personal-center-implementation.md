# 个人中心功能实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现图床平台个人中心完整功能，包括账号信息、图片管理、存储配额、上传记录、安全隐私、偏好设置。

**Architecture:** 前后端分离架构。前端Vue3组件化开发，新增个人中心路由和组件；后端Spring Boot新增Controller/Service/Repository，扩展现有User实体，新建数据库表。

**Tech Stack:** Vue 3, Spring Boot, JPA/Hibernate, MySQL, Axios

---

## 文件结构

### 后端 (Backend/src/main/java/com/imagehub/)

| 文件 | 改动类型 | 职责 |
|------|---------|------|
| `entity/UserSettings.java` | 新建 | 用户设置实体 |
| `entity/UploadLog.java` | 新建 | 上传日志实体 |
| `entity/SecurityLog.java` | 新建 | 安全日志实体 |
| `entity/LoginDevice.java` | 新建 | 登录设备实体 |
| `entity/ImagePermission.java` | 新建 | 图片权限实体 |
| `repository/UserSettingsRepository.java` | 新建 | 用户设置Repository |
| `repository/UploadLogRepository.java` | 新建 | 上传日志Repository |
| `repository/SecurityLogRepository.java` | 新建 | 安全日志Repository |
| `repository/LoginDeviceRepository.java` | 新建 | 登录设备Repository |
| `repository/ImagePermissionRepository.java` | 新建 | 图片权限Repository |
| `service/UserProfileService.java` | 新建 | 个人中心服务接口 |
| `service/impl/UserProfileServiceImpl.java` | 新建 | 个人中心服务实现 |
| `controller/UserProfileController.java` | 新建 | 个人中心API |
| `entity/User.java` | 修改 | 新增avatar、signature字段 |

### 前端 (Frontend/src/)

| 文件 | 改动类型 | 职责 |
|------|---------|------|
| `views/UserProfile.vue` | 新建 | 个人中心主框架（侧边栏+内容） |
| `views/profile/AccountOverview.vue` | 新建 | 账号概览 |
| `views/profile/AccountInfo.vue` | 新建 | 账号信息编辑 |
| `views/profile/StorageQuota.vue` | 新建 | 存储配额 |
| `views/profile/UploadLogs.vue` | 新建 | 上传记录 |
| `views/profile/SecuritySettings.vue` | 新建 | 安全隐私设置 |
| `views/profile/Preferences.vue` | 新建 | 偏好设置 |
| `router/index.js` | 修改 | 新增个人中心路由 |
| `api/index.js` | 修改 | 新增profileAPI |

---

## Task 1: 数据库表创建

**Files:**
- Create SQL migration script

- [ ] **Step 1: 创建数据库表SQL**

```sql
-- 用户设置表
CREATE TABLE user_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    link_format VARCHAR(20) DEFAULT 'url',
    theme VARCHAR(20) DEFAULT 'light',
    watermark_enabled BOOLEAN DEFAULT false,
    compress_enabled BOOLEAN DEFAULT false,
    watermark_text VARCHAR(100),
    default_permission BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 上传日志表
CREATE TABLE upload_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(50),
    status VARCHAR(20) DEFAULT 'success',
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 安全日志表
CREATE TABLE security_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    ip_address VARCHAR(50),
    device_info VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 登录设备表
CREATE TABLE login_devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_name VARCHAR(100),
    ip_address VARCHAR(50),
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_current BOOLEAN DEFAULT false,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 图片权限表
CREATE TABLE image_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_id BIGINT NOT NULL UNIQUE,
    is_public BOOLEAN DEFAULT true,
    referer_whitelist TEXT,
    FOREIGN KEY (image_id) REFERENCES images(id)
);
```

- [ ] **Step 2: 执行SQL创建表**

Run in MySQL:
```bash
mysql -u root -p image_hosting < create_tables.sql
```

- [ ] **Step 3: 提交**

```bash
git add Backend/src/main/resources/
git commit -m "feat: add personal center database tables"
```

---

## Task 2: 后端 - 用户设置功能

**Files:**
- Create: `entity/UserSettings.java`
- Create: `repository/UserSettingsRepository.java`
- Create: `service/UserProfileService.java`
- Create: `service/impl/UserProfileServiceImpl.java`
- Create: `controller/UserProfileController.java`

- [ ] **Step 1: 创建UserSettings实体**

```java
package com.imagehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "link_format", length = 20)
    private String linkFormat = "url";

    @Column(length = 20)
    private String theme = "light";

    @Column(name = "watermark_enabled")
    private Boolean watermarkEnabled = false;

    @Column(name = "compress_enabled")
    private Boolean compressEnabled = false;

    @Column(name = "watermark_text", length = 100)
    private String watermarkText;

    @Column(name = "default_permission")
    private Boolean defaultPermission = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 2: 创建UserSettingsRepository**

```java
package com.imagehub.repository;

import com.imagehub.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
    Optional<UserSettings> findByUserId(Long userId);
}
```

- [ ] **Step 3: 创建UserProfileService接口**

```java
package com.imagehub.service;

import com.imagehub.entity.*;
import java.util.Map;

public interface UserProfileService {
    UserSettings getSettings(Long userId);
    UserSettings updateSettings(Long userId, Map<String, Object> settings);
    User getUserInfo(Long userId);
    void updateUserInfo(Long userId, String nickname, String signature, String avatar);
    Map<String, Object> getStorageStats(Long userId);
}
```

- [ ] **Step 4: 创建UserProfileServiceImpl**

```java
package com.imagehub.service.impl;

import com.imagehub.entity.*;
import com.imagehub.repository.*;
import com.imagehub.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserSettingsRepository settingsRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final UploadLogRepository uploadLogRepository;

    @Override
    public UserSettings getSettings(Long userId) {
        return settingsRepository.findByUserId(userId)
            .orElseGet(() -> createDefaultSettings(userId));
    }

    @Override
    public UserSettings updateSettings(Long userId, Map<String, Object> settings) {
        UserSettings userSettings = getSettings(userId);
        if (settings.containsKey("linkFormat")) userSettings.setLinkFormat((String) settings.get("linkFormat"));
        if (settings.containsKey("theme")) userSettings.setTheme((String) settings.get("theme"));
        if (settings.containsKey("watermarkEnabled")) userSettings.setWatermarkEnabled((Boolean) settings.get("watermarkEnabled"));
        if (settings.containsKey("watermarkText")) userSettings.setWatermarkText((String) settings.get("watermarkText"));
        if (settings.containsKey("compressEnabled")) userSettings.setCompressEnabled((Boolean) settings.get("compressEnabled"));
        if (settings.containsKey("defaultPermission")) userSettings.setDefaultPermission((Boolean) settings.get("defaultPermission"));
        return settingsRepository.save(userSettings);
    }

    @Override
    public User getUserInfo(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public void updateUserInfo(Long userId, String nickname, String signature, String avatar) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (nickname != null) user.setUsername(nickname);
        if (signature != null) user.setSignature(signature);
        if (avatar != null) user.setAvatar(avatar);
        userRepository.save(user);
    }

    @Override
    public Map<String, Object> getStorageStats(Long userId) {
        Long totalImages = imageRepository.countByUserId(userId);
        return Map.of(
            "totalImages", totalImages,
            "usedStorage", 0L,
            "totalStorage", 5L * 1024 * 1024 * 1024L,
            "todayUploads", 0
        );
    }

    private UserSettings createDefaultSettings(Long userId) {
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        return settingsRepository.save(settings);
    }
}
```

- [ ] **Step 5: 创建UserProfileController**

```java
package com.imagehub.controller;

import com.imagehub.entity.*;
import com.imagehub.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSettings(@RequestParam Long userId) {
        UserSettings settings = userProfileService.getSettings(userId);
        return ResponseEntity.ok(Map.of("success", true, "settings", settings));
    }

    @PutMapping("/settings")
    public ResponseEntity<Map<String, Object>> updateSettings(
            @RequestParam Long userId,
            @RequestBody Map<String, Object> settings) {
        UserSettings updated = userProfileService.updateSettings(userId, settings);
        return ResponseEntity.ok(Map.of("success", true, "settings", updated));
    }

    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestParam Long userId) {
        User user = userProfileService.getUserInfo(userId);
        return ResponseEntity.ok(Map.of("success", true, "user", Map.of(
            "id", user.getId(),
            "username", user.getUsername(),
            "avatar", user.getAvatar() != null ? user.getAvatar() : "",
            "signature", user.getSignature() != null ? user.getSignature() : "",
            "role", user.getRole(),
            "createdAt", user.getCreatedAt().toString()
        )));
    }

    @PutMapping("/user")
    public ResponseEntity<Map<String, Object>> updateUserInfo(
            @RequestParam Long userId,
            @RequestBody Map<String, String> info) {
        userProfileService.updateUserInfo(
            userId,
            info.get("nickname"),
            info.get("signature"),
            info.get("avatar")
        );
        return ResponseEntity.ok(Map.of("success", true, "message", "更新成功"));
    }

    @GetMapping("/storage")
    public ResponseEntity<Map<String, Object>> getStorageStats(@RequestParam Long userId) {
        return ResponseEntity.ok(Map.of("success", true, "stats", userProfileService.getStorageStats(userId)));
    }
}
```

- [ ] **Step 6: 提交**

```bash
git add Backend/src/main/java/com/imagehub/
git commit -m "feat: add user settings backend API"
```

---

## Task 3: 后端 - 上传记录和安全日志

**Files:**
- Create: `entity/UploadLog.java`
- Create: `entity/SecurityLog.java`
- Create: `entity/LoginDevice.java`
- Create: `entity/ImagePermission.java`
- Create: `repository/UploadLogRepository.java`
- Create: `repository/SecurityLogRepository.java`
- Create: `repository/LoginDeviceRepository.java`
- Create: `repository/ImagePermissionRepository.java`

- [ ] **Step 1: 创建UploadLog实体**

```java
package com.imagehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "upload_logs")
public class UploadLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String filename;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type", length = 50)
    private String contentType;

    @Column(length = 20)
    private String status = "success";

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 2: 创建SecurityLog实体**

```java
package com.imagehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "security_logs")
public class SecurityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 3: 创建LoginDevice实体**

```java
package com.imagehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "login_devices")
public class LoginDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @Column(name = "is_current")
    private Boolean isCurrent = false;
}
```

- [ ] **Step 4: 创建ImagePermission实体**

```java
package com.imagehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Entity
@Table(name = "image_permissions")
public class ImagePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_id", nullable = false, unique = true)
    private Long imageId;

    @Column(name = "is_public")
    private Boolean isPublic = true;

    @Column(name = "referer_whitelist", columnDefinition = "TEXT")
    private String refererWhitelist;
}
```

- [ ] **Step 5: 创建Repositories**

```java
// UploadLogRepository
package com.imagehub.repository;

import com.imagehub.entity.UploadLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface UploadLogRepository extends JpaRepository<UploadLog, Long> {
    List<UploadLog> findByUserIdOrderByUploadedAtDesc(Long userId);
    List<UploadLog> findByUserIdAndUploadedAtAfterOrderByUploadedAtDesc(Long userId, LocalDateTime after);
    long countByUserId(Long userId);
}

// SecurityLogRepository
package com.imagehub.repository;

import com.imagehub.entity.SecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {
    List<SecurityLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}

// LoginDeviceRepository
package com.imagehub.repository;

import com.imagehub.entity.LoginDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LoginDeviceRepository extends JpaRepository<LoginDevice, Long> {
    List<LoginDevice> findByUserIdOrderByLastActiveDesc(Long userId);
    Optional<LoginDevice> findByUserIdAndIsCurrent(Long userId, Boolean isCurrent);
}

// ImagePermissionRepository
package com.imagehub.repository;

import com.imagehub.entity.ImagePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImagePermissionRepository extends JpaRepository<ImagePermission, Long> {
    Optional<ImagePermission> findByImageId(Long imageId);
}
```

- [ ] **Step 6: 提交**

```bash
git add Backend/src/main/java/com/imagehub/
git commit -m "feat: add upload logs and security logs backend"
```

---

## Task 4: 前端 - 个人中心主框架

**Files:**
- Create: `Frontend/src/views/UserProfile.vue`
- Modify: `Frontend/src/router/index.js`

- [ ] **Step 1: 创建UserProfile.vue主框架**

```vue
<template>
  <div class="profile-layout">
    <aside class="profile-sidebar">
      <div class="sidebar-header">
        <div class="user-avatar" @click="goToAccount">
          <img v-if="userInfo.avatar" :src="userInfo.avatar" alt="avatar" />
          <i v-else class="fas fa-user-circle"></i>
        </div>
        <div class="user-name">{{ userInfo.username }}</div>
      </div>
      <nav class="sidebar-nav">
        <router-link to="/profile" class="nav-item" exact>
          <i class="fas fa-chart-line"></i> 账号概览
        </router-link>
        <router-link to="/profile/account" class="nav-item">
          <i class="fas fa-user"></i> 账号信息
        </router-link>
        <router-link to="/profile/images" class="nav-item">
          <i class="fas fa-images"></i> 图片管理
        </router-link>
        <router-link to="/profile/quota" class="nav-item">
          <i class="fas fa-database"></i> 存储配额
        </router-link>
        <router-link to="/profile/logs" class="nav-item">
          <i class="fas fa-list-alt"></i> 上传记录
        </router-link>
        <router-link to="/profile/security" class="nav-item">
          <i class="fas fa-shield-alt"></i> 安全隐私
        </router-link>
        <router-link to="/profile/preferences" class="nav-item">
          <i class="fas fa-cog"></i> 偏好设置
        </router-link>
      </nav>
      <div class="sidebar-footer">
        <button @click="logout" class="btn-logout">
          <i class="fas fa-sign-out-alt"></i> 退出登录
        </button>
      </div>
    </aside>
    <main class="profile-content">
      <div class="content-header">
        <h1>{{ pageTitle }}</h1>
      </div>
      <div class="content-body">
        <router-view></router-view>
      </div>
    </main>
  </div>
</template>
```

- [ ] **Step 2: 添加路由配置**

在 router/index.js 中添加profile路由和子路由

- [ ] **Step 3: 提交**

```bash
git add Frontend/src/views/UserProfile.vue Frontend/src/router/index.js
git commit -m "feat: add UserProfile main layout component"
```

---

## Task 5: 前端 - 账号概览组件

**Files:**
- Create: `Frontend/src/views/profile/AccountOverview.vue`

- [ ] **Step 1: 创建AccountOverview.vue**

实现统计卡片（图片总数、已用存储、今日上传）和快捷操作入口

- [ ] **Step 2: 提交**

```bash
git add Frontend/src/views/profile/AccountOverview.vue
git commit -m "feat: add AccountOverview component"
```

---

## Task 6: 前端 - 账号信息组件

**Files:**
- Create: `Frontend/src/views/profile/AccountInfo.vue`

- [ ] **Step 1: 创建AccountInfo.vue**

实现头像上传、昵称修改、个性签名、会员等级展示、注册时间展示

- [ ] **Step 2: 提交**

```bash
git add Frontend/src/views/profile/AccountInfo.vue
git commit -m "feat: add AccountInfo component"
```

---

## Task 7: 前端 - 存储配额组件

**Files:**
- Create: `Frontend/src/views/profile/StorageQuota.vue`

- [ ] **Step 1: 创建StorageQuota.vue**

实现环形进度条、容量统计、今日上传用量、套餐升级入口、超限警告

- [ ] **Step 2: 提交**

```bash
git add Frontend/src/views/profile/StorageQuota.vue
git commit -m "feat: add StorageQuota component"
```

---

## Task 8: 前端 - 上传记录组件

**Files:**
- Create: `Frontend/src/views/profile/UploadLogs.vue`

- [ ] **Step 1: 创建UploadLogs.vue**

实现表格展示、时间筛选、分页

- [ ] **Step 2: 提交**

```bash
git add Frontend/src/views/profile/UploadLogs.vue
git commit -m "feat: add UploadLogs component"
```

---

## Task 9: 前端 - 安全隐私组件

**Files:**
- Create: `Frontend/src/views/profile/SecuritySettings.vue`

- [ ] **Step 1: 创建SecuritySettings.vue**

实现修改密码、访问权限、防盗链配置、登录设备管理

- [ ] **Step 2: 提交**

```bash
git add Frontend/src/views/profile/SecuritySettings.vue
git commit -m "feat: add SecuritySettings component"
```

---

## Task 10: 前端 - 偏好设置组件

**Files:**
- Create: `Frontend/src/views/profile/Preferences.vue`

- [ ] **Step 1: 创建Preferences.vue**

实现链接格式、主题切换、上传默认参数配置

- [ ] **Step 2: 提交**

```bash
git add Frontend/src/views/profile/Preferences.vue
git commit -m "feat: add Preferences component"
```

---

## Task 11: 前端 - API集成

**Files:**
- Modify: `Frontend/src/api/index.js`

- [ ] **Step 1: 添加profileAPI**

```javascript
export const profileAPI = {
  getSettings(userId) {
    return api.get('/profile/settings', { params: { userId } })
  },
  updateSettings(userId, settings) {
    return api.put('/profile/settings', settings, { params: { userId } })
  },
  getUserInfo(userId) {
    return api.get('/profile/user', { params: { userId } })
  },
  updateUserInfo(userId, info) {
    return api.put('/profile/user', info, { params: { userId } })
  },
  getStorageStats(userId) {
    return api.get('/profile/storage', { params: { userId } })
  },
  getUploadLogs(userId, range) {
    return api.get('/profile/upload-logs', { params: { userId, range } })
  },
  getSecurityLogs(userId) {
    return api.get('/profile/security-logs', { params: { userId } })
  },
  getDevices(userId) {
    return api.get('/profile/devices', { params: { userId } })
  },
  logoutDevice(deviceId) {
    return api.delete(`/profile/devices/${deviceId}`)
  }
}
```

- [ ] **Step 2: 提交**

```bash
git add Frontend/src/api/index.js
git commit -m "feat: add profile API endpoints"
```

---

## 实现顺序

1. Task 1: 数据库表创建
2. Task 2: 后端 - 用户设置功能
3. Task 3: 后端 - 上传记录和安全日志
4. Task 4: 前端 - 个人中心主框架
5. Task 5: 前端 - 账号概览
6. Task 6: 前端 - 账号信息
7. Task 7: 前端 - 存储配额
8. Task 8: 前端 - 上传记录
9. Task 9: 前端 - 安全隐私
10. Task 10: 前端 - 偏好设置
11. Task 11: 前端 - API集成
