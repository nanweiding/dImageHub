package com.imagehub.service;

import com.imagehub.entity.Image;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ImageService {
    Image uploadImage(MultipartFile file, Long userId, Long folderId) throws IOException;
    List<Image> getUserImages(Long userId, String sortBy, String sortOrder, Integer page, Integer pageSize);
    Image getImageById(Long id);
    void incrementViewCount(Long id);
    void deleteImage(Long id, Long userId);
    void moveToFolder(Long imageId, Long folderId, Long userId);
    void batchDelete(List<Long> imageIds, Long userId);
    void batchMove(List<Long> imageIds, Long folderId, Long userId);

    void updateImage(Long id, Long userId, String filename, String remark, Long folderId);

    // Recycle bin / Trash
    List<Image> getTrashImages(Long userId);
    void restoreImage(Long id, Long userId);
    void permanentDeleteImage(Long id, Long userId);
    void emptyTrash(Long userId);

    // Admin
    List<Image> getAllImages();
    List<Image> getDeletedImages();
    Map<String, Object> getStats();
}
