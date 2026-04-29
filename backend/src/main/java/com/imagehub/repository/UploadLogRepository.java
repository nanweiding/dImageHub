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