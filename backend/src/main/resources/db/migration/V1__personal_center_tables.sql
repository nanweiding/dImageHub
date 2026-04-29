-- 个人中心数据库表
-- 执行方式: mysql -u root -p image_hosting < Backend/src/main/resources/db/migration/V1__personal_center_tables.sql

-- 用户设置表
CREATE TABLE IF NOT EXISTS user_settings (
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
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 上传日志表
CREATE TABLE IF NOT EXISTS upload_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    filename VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(50),
    status VARCHAR(20) DEFAULT 'success',
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 安全日志表
CREATE TABLE IF NOT EXISTS security_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    action VARCHAR(50) NOT NULL,
    ip_address VARCHAR(50),
    device_info VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 登录设备表
CREATE TABLE IF NOT EXISTS login_devices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    device_name VARCHAR(100),
    ip_address VARCHAR(50),
    last_active TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_current BOOLEAN DEFAULT false,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 图片权限表
CREATE TABLE IF NOT EXISTS image_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    image_id BIGINT NOT NULL UNIQUE,
    is_public BOOLEAN DEFAULT true,
    referer_whitelist TEXT,
    FOREIGN KEY (image_id) REFERENCES images(id) ON DELETE CASCADE
);

-- 为users表添加新字段
ALTER TABLE users ADD COLUMN IF NOT EXISTS avatar VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS signature VARCHAR(200);
