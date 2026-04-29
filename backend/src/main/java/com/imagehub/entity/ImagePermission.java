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