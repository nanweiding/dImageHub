# 图片托管平台 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 构建一个完整的图片托管平台，包含Vue.js前端、Spring Boot后端和MySQL数据库，支持用户注册登录、图片上传、图片管理功能。

**Architecture:** 
- 前端: Vue.js 3 + Vite + Vue Router + Axios，单页面应用
- 后端: Spring Boot 3 + Spring Data JPA + MySQL，提供RESTful API
- 部署: 前后端分离，前端静态资源由Nginx服务

**Tech Stack:** Vue.js 3, Vite, Vue Router 4, Axios, Spring Boot 3, Spring Data JPA, MySQL 8, Maven

---

## 项目结构设计

```
dImageHub/
├── frontend/                 # Vue.js 前端项目
│   ├── src/
│   │   ├── api/             # API 调用层
│   │   ├── components/      # Vue 组件
│   │   ├── views/           # 页面视图
│   │   ├── router/          # 路由配置
│   │   ├── stores/          # 状态管理
│   │   └── main.js          # 入口文件
│   └── package.json
├── backend/                  # Spring Boot 后端项目
│   ├── src/main/java/
│   │   └── com/imagehub/
│   │       ├── controller/  # REST Controller
│   │       ├── entity/       # JPA 实体
│   │       ├── repository/   # 数据访问层
│   │       ├── service/      # 业务逻辑层
│   │       └── config/       # 配置类
│   └── pom.xml
└── docs/                     # 文档
```

---

## Task 1: 项目初始化配置

**Files:**
- Create: `backend/pom.xml`
- Create: `backend/src/main/resources/application.properties`
- Create: `backend/src/main/java/com/imagehub/ImageHubApplication.java`

- [ ] **Step 1: 创建后端Maven项目pom.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
        <relativePath/>
    </parent>
    
    <groupId>com.imagehub</groupId>
    <artifactId>imagehub-backend</artifactId>
    <version>1.0.0</version>
    <name>imagehub-backend</name>
    <description>Image Hub Backend API</description>
    
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

- [ ] **Step 2: 创建application.properties**

```properties
# 服务端口
server.port=8080

# MySQL 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/image_hosting?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# 文件上传配置
spring.servlet.multipart.enabled=true
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# 图片存储路径
image.upload.dir=D:/dImageHub/uploads/
```

- [ ] **Step 3: 创建Spring Boot启动类**

```java
package com.imagehub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImageHubApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageHubApplication.class, args);
    }
}
```

- [ ] **Step 4: 验证项目结构**

Run: `ls -la backend/src/main/java/com/imagehub/`
Expected: 启动类文件存在

- [ ] **Step 5: Commit**

```bash
git add backend/pom.xml backend/src/
git commit -m "init: add Spring Boot project structure with Maven"
```

---

## Task 2: 数据库实体设计

**Files:**
- Create: `backend/src/main/java/com/imagehub/entity/User.java`
- Create: `backend/src/main/java/com/imagehub/entity/Image.java`

- [ ] **Step 1: 创建User实体**

```java
package com.imagehub.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "用户名不能为空")
    @Column(nullable = false, unique = true, length = 255)
    private String username;

    @NotBlank(message = "密码不能为空")
    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

- [ ] **Step 2: 创建Image实体**

```java
package com.imagehub.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "images")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    @Column(name = "stored_filename", nullable = false, length = 255)
    private String storedFilename;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "content_type", length = 100)
    private String contentType;

    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    @PrePersist
    protected void onCreate() {
        uploadTime = LocalDateTime.now();
    }
}
```

- [ ] **Step 3: 运行测试验证实体编译**

Run: `cd backend && mvn compile`
Expected: 编译成功，无错误

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/imagehub/entity/
git commit -m "feat: add User and Image JPA entities"
```

---

## Task 3: 数据访问层 (Repository)

**Files:**
- Create: `backend/src/main/java/com/imagehub/repository/UserRepository.java`
- Create: `backend/src/main/java/com/imagehub/repository/ImageRepository.java`

- [ ] **Step 1: 创建UserRepository**

```java
package com.imagehub.repository;

import com.imagehub.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
```

- [ ] **Step 2: 创建ImageRepository**

```java
package com.imagehub.repository;

import com.imagehub.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUserId(Long userId);
    long countByUserId(Long userId);
}
```

- [ ] **Step 3: 验证编译**

Run: `cd backend && mvn compile`
Expected: 编译成功

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/imagehub/repository/
git commit -m "feat: add User and Image repositories"
```

---

## Task 4: 服务层 (Service)

**Files:**
- Create: `backend/src/main/java/com/imagehub/service/UserService.java`
- Create: `backend/src/main/java/com/imagehub/service/impl/UserServiceImpl.java`
- Create: `backend/src/main/java/com/imagehub/service/ImageService.java`
- Create: `backend/src/main/java/com/imagehub/service/impl/ImageServiceImpl.java`

- [ ] **Step 1: 创建UserService接口**

```java
package com.imagehub.service;

import com.imagehub.entity.User;

public interface UserService {
    User register(String username, String password);
    User login(String username, String password);
    User findById(Long id);
    boolean existsByUsername(String username);
}
```

- [ ] **Step 2: 创建UserServiceImpl实现**

```java
package com.imagehub.service.impl;

import com.imagehub.entity.User;
import com.imagehub.repository.UserRepository;
import com.imagehub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public User register(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 生产环境应加密
        return userRepository.save(user);
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
}
```

- [ ] **Step 3: 创建ImageService接口**

```java
package com.imagehub.service;

import com.imagehub.entity.Image;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ImageService {
    Image uploadImage(MultipartFile file, Long userId);
    List<Image> getUserImages(Long userId);
    Image getImageById(Long id);
    void deleteImage(Long id, Long userId);
}
```

- [ ] **Step 4: 创建ImageServiceImpl实现**

```java
package com.imagehub.service.impl;

import com.imagehub.entity.Image;
import com.imagehub.repository.ImageRepository;
import com.imagehub.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public Image uploadImage(MultipartFile file, Long userId) throws IOException {
        // 创建上传目录
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath);

        // 保存记录
        Image image = new Image();
        image.setUserId(userId);
        image.setOriginalFilename(originalFilename);
        image.setStoredFilename(storedFilename);
        image.setFilePath(filePath.toString());
        image.setFileSize(file.getSize());
        image.setContentType(file.getContentType());

        return imageRepository.save(image);
    }

    @Override
    public List<Image> getUserImages(Long userId) {
        return imageRepository.findByUserId(userId);
    }

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图片不存在"));
    }

    @Override
    @Transactional
    public void deleteImage(Long id, Long userId) {
        Image image = getImageById(id);
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此图片");
        }

        // 删除文件
        try {
            Path filePath = Paths.get(image.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 文件删除失败不影响数据库记录删除
        }

        imageRepository.delete(image);
    }
}
```

- [ ] **Step 5: 验证编译**

Run: `cd backend && mvn compile`
Expected: 编译成功

- [ ] **Step 6: Commit**

```bash
git add backend/src/main/java/com/imagehub/service/
git commit -m "feat: add User and Image service layer"
```

---

## Task 5: REST Controller

**Files:**
- Create: `backend/src/main/java/com/imagehub/controller/AuthController.java`
- Create: `backend/src/main/java/com/imagehub/controller/ImageController.java`

- [ ] **Step 1: 创建AuthController**

```java
package com.imagehub.controller;

import com.imagehub.entity.User;
import com.imagehub.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        try {
            User user = userService.register(request.getUsername(), request.getPassword());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername()
            ));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            User user = userService.login(request.getUsername(), request.getPassword());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "登录成功");
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername()
            ));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Data
    public static class RegisterRequest {
        private String username;
        private String password;
    }

    @Data
    public static class LoginRequest {
        private String username;
        private String password;
    }
}
```

- [ ] **Step 2: 创建ImageController**

```java
package com.imagehub.controller;

import com.imagehub.entity.Image;
import com.imagehub.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        try {
            if (file.isEmpty()) {
                return errorResponse("请选择要上传的图片");
            }

            // 验证文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return errorResponse("只能上传图片文件");
            }

            Image image = imageService.uploadImage(file, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "上传成功");
            response.put("image", Map.of(
                "id", image.getId(),
                "originalFilename", image.getOriginalFilename(),
                "storedFilename", image.getStoredFilename(),
                "fileSize", image.getFileSize(),
                "contentType", image.getContentType(),
                "url", "/api/images/" + image.getId()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserImages(@PathVariable Long userId) {
        List<Image> images = imageService.getUserImages(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("images", images.stream().map(img -> Map.of(
            "id", img.getId(),
            "originalFilename", img.getOriginalFilename(),
            "fileSize", img.getFileSize(),
            "contentType", img.getContentType(),
            "uploadTime", img.getUploadTime().toString()
        )).toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getImage(@PathVariable Long id) {
        try {
            Image image = imageService.getImageById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("image", Map.of(
                "id", image.getId(),
                "originalFilename", image.getOriginalFilename(),
                "storedFilename", image.getStoredFilename(),
                "filePath", image.getFilePath(),
                "fileSize", image.getFileSize(),
                "contentType", image.getContentType()
            ));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteImage(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            imageService.deleteImage(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }
}
```

- [ ] **Step 3: 验证编译**

Run: `cd backend && mvn compile`
Expected: 编译成功

- [ ] **Step 4: Commit**

```bash
git add backend/src/main/java/com/imagehub/controller/
git commit -m "feat: add Auth and Image REST controllers"
```

---

## Task 6: 后端单元测试

**Files:**
- Create: `backend/src/test/java/com/imagehub/service/UserServiceTest.java`
- Create: `backend/src/test/java/com/imagehub/service/ImageServiceTest.java`

- [ ] **Step 1: 创建UserServiceTest**

```java
package com.imagehub.service;

import com.imagehub.entity.User;
import com.imagehub.repository.UserRepository;
import com.imagehub.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void register_shouldCreateNewUser_whenUsernameNotExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User result = userService.register("testuser", "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals("password123", result.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameExists() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            userService.register("existinguser", "password")
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnUser_whenCredentialsCorrect() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.login("testuser", "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void login_shouldThrowException_whenPasswordIncorrect() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("correctpassword");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () -> 
            userService.login("testuser", "wrongpassword")
        );
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> 
            userService.login("nonexistent", "password")
        );
    }
}
```

- [ ] **Step 2: 创建ImageServiceTest**

```java
package com.imagehub.service;

import com.imagehub.entity.Image;
import com.imagehub.repository.ImageRepository;
import com.imagehub.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private ImageRepository imageRepository;

    private ImageService imageService;

    private String uploadDir = "D:/dImageHub/uploads/";

    @BeforeEach
    void setUp() {
        imageService = new ImageServiceImpl(imageRepository);
        ReflectionTestUtils.setField(imageService, "uploadDir", uploadDir);
    }

    @Test
    void getUserImages_shouldReturnUserImages() {
        Long userId = 1L;
        Image image1 = new Image();
        image1.setId(1L);
        image1.setUserId(userId);
        image1.setOriginalFilename("test1.jpg");

        Image image2 = new Image();
        image2.setId(2L);
        image2.setUserId(userId);
        image2.setOriginalFilename("test2.png");

        when(imageRepository.findByUserId(userId)).thenReturn(Arrays.asList(image1, image2));

        List<Image> result = imageService.getUserImages(userId);

        assertEquals(2, result.size());
        verify(imageRepository).findByUserId(userId);
    }

    @Test
    void getImageById_shouldReturnImage_whenExists() {
        Long imageId = 1L;
        Image image = new Image();
        image.setId(imageId);
        image.setOriginalFilename("test.jpg");

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        Image result = imageService.getImageById(imageId);

        assertNotNull(result);
        assertEquals(imageId, result.getId());
    }

    @Test
    void getImageById_shouldThrowException_whenNotExists() {
        Long imageId = 999L;
        when(imageRepository.findById(imageId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> imageService.getImageById(imageId));
    }

    @Test
    void deleteImage_shouldDelete_whenUserOwnsImage() {
        Long imageId = 1L;
        Long userId = 1L;
        Image image = new Image();
        image.setId(imageId);
        image.setUserId(userId);
        image.setFilePath("D:/dImageHub/uploads/test.jpg");

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        assertDoesNotThrow(() -> imageService.deleteImage(imageId, userId));
        verify(imageRepository).delete(image);
    }

    @Test
    void deleteImage_shouldThrowException_whenUserDoesNotOwnImage() {
        Long imageId = 1L;
        Long imageOwnerId = 1L;
        Long requestUserId = 2L;
        Image image = new Image();
        image.setId(imageId);
        image.setUserId(imageOwnerId);

        when(imageRepository.findById(imageId)).thenReturn(Optional.of(image));

        assertThrows(RuntimeException.class, () -> 
            imageService.deleteImage(imageId, requestUserId)
        );
        verify(imageRepository, never()).delete(any(Image.class));
    }
}
```

- [ ] **Step 3: 运行测试**

Run: `cd backend && mvn test`
Expected: 所有测试通过

- [ ] **Step 4: Commit**

```bash
git add backend/src/test/java/com/imagehub/service/
git commit -m "test: add unit tests for UserService and ImageService"
```

---

## Task 7: 前端项目初始化

**Files:**
- Create: `frontend/package.json`
- Create: `frontend/vite.config.js`
- Create: `frontend/index.html`
- Create: `frontend/src/main.js`

- [ ] **Step 1: 创建前端package.json**

```json
{
  "name": "imagehub-frontend",
  "version": "1.0.0",
  "type": "module",
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview"
  },
  "dependencies": {
    "vue": "^3.4.0",
    "vue-router": "^4.2.5",
    "axios": "^1.6.2"
  },
  "devDependencies": {
    "@vitejs/plugin-vue": "^5.0.0",
    "vite": "^5.0.0"
  }
}
```

- [ ] **Step 2: 创建vite.config.js**

```javascript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
  plugins: [vue()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

- [ ] **Step 3: 创建index.html**

```html
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>图片托管平台</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; }
  </style>
</head>
<body>
  <div id="app"></div>
  <script type="module" src="/src/main.js"></script>
</body>
</html>
```

- [ ] **Step 4: 创建main.js**

```javascript
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
app.use(router)
app.mount('#app')
```

- [ ] **Step 5: 创建App.vue**

```vue
<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-brand">图片托管平台</div>
      <div class="nav-links">
        <router-link to="/">首页</router-link>
        <router-link v-if="!isLoggedIn" to="/login">登录</router-link>
        <router-link v-if="!isLoggedIn" to="/register">注册</router-link>
        <span v-if="isLoggedIn">{{ username }}</span>
        <button v-if="isLoggedIn" @click="logout">退出</button>
      </div>
    </nav>
    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const username = ref(localStorage.getItem('username') || '')
const isLoggedIn = computed(() => !!localStorage.getItem('userId'))

const logout = () => {
  localStorage.removeItem('userId')
  localStorage.removeItem('username')
  username.value = ''
  router.push('/login')
}
</script>

<style>
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 2rem;
  background: #1890ff;
  color: white;
}
.nav-brand { font-size: 1.5rem; font-weight: bold; }
.nav-links { display: flex; gap: 1rem; align-items: center; }
.nav-links a { color: white; text-decoration: none; }
.nav-links button { padding: 0.5rem 1rem; cursor: pointer; }
.main-content { padding: 2rem; max-width: 1200px; margin: 0 auto; }
</style>
```

- [ ] **Step 6: 创建router/index.js**

```javascript
import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/login', component: Login },
  { path: '/register', component: Register }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
```

- [ ] **Step 7: Commit**

```bash
git add frontend/
git commit -m "feat: initialize Vue.js frontend project"
```

---

## Task 8: 前端API层和视图组件

**Files:**
- Create: `frontend/src/api/index.js`
- Create: `frontend/src/views/Home.vue`
- Create: `frontend/src/views/Login.vue`
- Create: `frontend/src/views/Register.vue`

- [ ] **Step 1: 创建API层api/index.js**

```javascript
import axios from 'axios'

const api = axios.create({
  baseURL: '/api'
})

export const authAPI = {
  register(username, password) {
    return api.post('/auth/register', { username, password })
  },
  login(username, password) {
    return api.post('/auth/login', { username, password })
  }
}

export const imageAPI = {
  uploadImage(file, userId) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('userId', userId)
    return api.post('/images/upload', formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })
  },
  getUserImages(userId) {
    return api.get(`/images/user/${userId}`)
  },
  getImage(id) {
    return api.get(`/images/${id}`)
  },
  deleteImage(id, userId) {
    return api.delete(`/images/${id}?userId=${userId}`)
  }
}

export default api
```

- [ ] **Step 2: 创建Login.vue**

```vue
<template>
  <div class="auth-container">
    <h2>登录</h2>
    <form @submit.prevent="handleLogin">
      <div class="form-group">
        <label>用户名</label>
        <input v-model="form.username" type="text" required />
      </div>
      <div class="form-group">
        <label>密码</label>
        <input v-model="form.password" type="password" required />
      </div>
      <button type="submit" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</button>
      <p v-if="error" class="error">{{ error }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await authAPI.login(form.value.username, form.value.password)
    if (response.data.success) {
      localStorage.setItem('userId', response.data.user.id)
      localStorage.setItem('username', response.data.user.username)
      router.push('/')
    } else {
      error.value = response.data.message
    }
  } catch (e) {
    error.value = '登录失败，请检查用户名和密码'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container { max-width: 400px; margin: 0 auto; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; }
.form-group input { width: 100%; padding: 0.5rem; font-size: 1rem; }
button { width: 100%; padding: 0.75rem; background: #1890ff; color: white; border: none; cursor: pointer; }
.error { color: red; margin-top: 1rem; text-align: center; }
</style>
```

- [ ] **Step 3: 创建Register.vue**

```vue
<template>
  <div class="auth-container">
    <h2>注册</h2>
    <form @submit.prevent="handleRegister">
      <div class="form-group">
        <label>用户名</label>
        <input v-model="form.username" type="text" required />
      </div>
      <div class="form-group">
        <label>密码</label>
        <input v-model="form.password" type="password" required />
      </div>
      <button type="submit" :disabled="loading">{{ loading ? '注册中...' : '注册' }}</button>
      <p v-if="error" class="error">{{ error }}</p>
      <p v-if="success" class="success">{{ success }}</p>
    </form>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../api'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)
const error = ref('')
const success = ref('')

const handleRegister = async () => {
  loading.value = true
  error.value = ''
  success.value = ''
  try {
    const response = await authAPI.register(form.value.username, form.value.password)
    if (response.data.success) {
      success.value = '注册成功，即将跳转到登录页面...'
      setTimeout(() => router.push('/login'), 1500)
    } else {
      error.value = response.data.message
    }
  } catch (e) {
    error.value = '注册失败，请稍后重试'
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container { max-width: 400px; margin: 0 auto; }
.form-group { margin-bottom: 1rem; }
.form-group label { display: block; margin-bottom: 0.5rem; }
.form-group input { width: 100%; padding: 0.5rem; font-size: 1rem; }
button { width: 100%; padding: 0.75rem; background: #1890ff; color: white; border: none; cursor: pointer; }
.error { color: red; margin-top: 1rem; text-align: center; }
.success { color: green; margin-top: 1rem; text-align: center; }
</style>
```

- [ ] **Step 4: 创建Home.vue**

```vue
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

    <div v-else class="upload-section">
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
```

- [ ] **Step 5: Commit**

```bash
git add frontend/src/
git commit -m "feat: add API layer and Vue components"
```

---

## Task 9: 集成测试与验证

**Files:**
- Create: `backend/src/test/java/com/imagehub/controller/AuthControllerTest.java`

- [ ] **Step 1: 创建AuthControllerTest**

```java
package com.imagehub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imagehub.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void register_shouldReturnSuccess_whenValidRequest() throws Exception {
        when(userService.register(any(), any())).thenReturn(
            new com.imagehub.entity.User() {{
                setId(1L);
                setUsername("testuser");
            }}
        );

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void register_shouldReturnBadRequest_whenUsernameExists() throws Exception {
        when(userService.register(any(), any())).thenThrow(new RuntimeException("用户名已存在"));

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"existing\",\"password\":\"password123\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }
}
```

- [ ] **Step 2: 运行完整测试**

Run: `cd backend && mvn test`
Expected: 所有测试通过

- [ ] **Step 3: 验证前端构建**

Run: `cd frontend && npm install && npm run build`
Expected: 构建成功

- [ ] **Step 4: Commit**

```bash
git add backend/src/test/
git commit -m "test: add integration tests for AuthController"
```

---

## 实施检查清单

| 需求 | 状态 |
|------|------|
| Spring Boot后端项目结构 | ✅ Task 1 |
| MySQL数据库连接配置 | ✅ Task 1 |
| User实体和Image实体 | ✅ Task 2 |
| 用户注册和登录API | ✅ Task 5 |
| 图片上传API | ✅ Task 5 |
| 图片列表和删除API | ✅ Task 5 |
| 后端单元测试 | ✅ Task 6 |
| Vue.js前端项目 | ✅ Task 7 |
| 用户注册和登录页面 | ✅ Task 8 |
| 图片上传和列表页面 | ✅ Task 8 |
| 前端与后端API集成 | ✅ Task 8 |

---

**Plan complete.**

**Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**