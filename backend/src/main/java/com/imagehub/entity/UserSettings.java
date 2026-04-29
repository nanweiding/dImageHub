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