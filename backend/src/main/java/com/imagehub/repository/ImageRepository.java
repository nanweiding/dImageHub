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