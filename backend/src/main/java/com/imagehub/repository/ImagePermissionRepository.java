package com.imagehub.repository;

import com.imagehub.entity.ImagePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImagePermissionRepository extends JpaRepository<ImagePermission, Long> {
    Optional<ImagePermission> findByImageId(Long imageId);
}