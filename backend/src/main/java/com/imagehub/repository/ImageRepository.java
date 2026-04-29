package com.imagehub.repository;

import com.imagehub.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUserIdAndDeleted(Long userId, Boolean deleted);
    List<Image> findByDeleted(Boolean deleted);
    Optional<Image> findByIdAndDeleted(Long id, Boolean deleted);
    long countByUserIdAndDeleted(Long userId, Boolean deleted);
    long countByDeleted(Boolean deleted);

    @Query("SELECT COALESCE(SUM(i.fileSize), 0) FROM Image i WHERE i.deleted = false")
    Long getTotalStorageUsed();

    @Query("SELECT COALESCE(SUM(i.fileSize), 0) FROM Image i WHERE i.userId = ?1 AND i.deleted = false")
    Long getStorageUsedByUser(Long userId);
}