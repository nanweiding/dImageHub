package com.imagehub.repository;

import com.imagehub.entity.SecurityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SecurityLogRepository extends JpaRepository<SecurityLog, Long> {
    List<SecurityLog> findByUserIdOrderByCreatedAtDesc(Long userId);
}