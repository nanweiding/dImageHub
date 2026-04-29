package com.imagehub.service.impl;

import com.imagehub.entity.Image;
import com.imagehub.repository.ImageRepository;
import com.imagehub.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private static final Logger log = LoggerFactory.getLogger(ImageServiceImpl.class);

    private final ImageRepository imageRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public Image uploadImage(MultipartFile file, Long userId, Long folderId) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String storedFilename = UUID.randomUUID().toString() + extension;

        Path filePath = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath);

        Image image = new Image();
        image.setUserId(userId);
        image.setOriginalFilename(originalFilename);
        image.setStoredFilename(storedFilename);
        image.setFilePath(filePath.toString());
        image.setFileSize(file.getSize());
        image.setContentType(file.getContentType());
        image.setFolderId(folderId);

        return imageRepository.save(image);
    }

    @Override
    public List<Image> getUserImages(Long userId, String sortBy, String sortOrder, Integer page, Integer pageSize) {
        List<Image> images = imageRepository.findByUserIdAndDeleted(userId, false);

        // Sort (pagination params are accepted for future use, currently handled frontend-side)
        boolean ascending = "asc".equalsIgnoreCase(sortOrder);
        images.sort((a, b) -> {
            int cmp = 0;
            switch (sortBy != null ? sortBy : "uploadTime") {
                case "fileSize":
                    cmp = Long.compare(a.getFileSize() != null ? a.getFileSize() : 0L, b.getFileSize() != null ? b.getFileSize() : 0L);
                    break;
                case "viewCount":
                    cmp = Long.compare(a.getViewCount() != null ? a.getViewCount() : 0L, b.getViewCount() != null ? b.getViewCount() : 0L);
                    break;
                case "uploadTime":
                default:
                    LocalDateTime ta = a.getUploadTime() != null ? a.getUploadTime() : LocalDateTime.MIN;
                    LocalDateTime tb = b.getUploadTime() != null ? b.getUploadTime() : LocalDateTime.MIN;
                    cmp = ta.compareTo(tb);
                    break;
            }
            return ascending ? cmp : -cmp;
        });

        return images;
    }

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findByIdAndDeleted(id, false)
                .orElseThrow(() -> new RuntimeException("图片不存在"));
    }

    @Override
    @Transactional
    public void incrementViewCount(Long id) {
        Image image = imageRepository.findById(id).orElse(null);
        if (image != null) {
            image.setViewCount(image.getViewCount() != null ? image.getViewCount() + 1 : 1);
            imageRepository.save(image);
        }
    }

    @Override
    @Transactional
    public void deleteImage(Long id, Long userId) {
        Image image = getImageById(id);
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此图片");
        }
        // Soft delete
        image.setDeleted(true);
        image.setDeletedAt(LocalDateTime.now());
        imageRepository.save(image);
    }

    @Override
    @Transactional
    public void moveToFolder(Long imageId, Long folderId, Long userId) {
        Image image = getImageById(imageId);
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此图片");
        }
        image.setFolderId(folderId);
        imageRepository.save(image);
    }

    @Override
    @Transactional
    public void batchDelete(List<Long> imageIds, Long userId) {
        for (Long imageId : imageIds) {
            deleteImage(imageId, userId);
        }
    }

    @Override
    @Transactional
    public void batchMove(List<Long> imageIds, Long folderId, Long userId) {
        for (Long imageId : imageIds) {
            Image image = getImageById(imageId);
            if (!image.getUserId().equals(userId)) {
                throw new RuntimeException("无权限操作图片 " + imageId);
            }
            image.setFolderId(folderId);
            imageRepository.save(image);
        }
    }

    @Override
    @Transactional
    public void updateImage(Long id, Long userId, String filename, String remark, Long folderId) {
        Image image = getImageById(id);
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此图片");
        }
        if (filename != null && !filename.trim().isEmpty()) {
            image.setOriginalFilename(filename.trim());
        }
        if (remark != null) {
            image.setRemark(remark.trim());
        }
        if (folderId != null) {
            image.setFolderId(folderId);
        }
        imageRepository.save(image);
    }

    @Override
    public List<Image> getTrashImages(Long userId) {
        return imageRepository.findByUserIdAndDeleted(userId, true);
    }

    @Override
    @Transactional
    public void restoreImage(Long id, Long userId) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图片不存在"));
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此图片");
        }
        image.setDeleted(false);
        image.setDeletedAt(null);
        imageRepository.save(image);
    }

    @Override
    @Transactional
    public void permanentDeleteImage(Long id, Long userId) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图片不存在"));
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限操作此图片");
        }
        try {
            Path filePath = Paths.get(image.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("Failed to delete file: {}", image.getFilePath(), e);
        }
        imageRepository.delete(image);
    }

    @Override
    @Transactional
    public void emptyTrash(Long userId) {
        List<Image> trashImages = getTrashImages(userId);
        for (Image image : trashImages) {
            try {
                Path filePath = Paths.get(image.getFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                log.error("Failed to delete file: {}", image.getFilePath(), e);
            }
            imageRepository.delete(image);
        }
    }

    @Override
    public List<Image> getAllImages() {
        return imageRepository.findByDeleted(false);
    }

    @Override
    public List<Image> getDeletedImages() {
        return imageRepository.findByDeleted(true);
    }

    @Override
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalImages", imageRepository.countByDeleted(false));
        stats.put("totalTrash", imageRepository.countByDeleted(true));
        stats.put("totalStorage", imageRepository.getTotalStorageUsed());
        return stats;
    }
}
