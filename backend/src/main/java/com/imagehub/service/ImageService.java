package com.imagehub.service;

import com.imagehub.entity.Image;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

public interface ImageService {
    Image uploadImage(MultipartFile file, Long userId) throws IOException;
    List<Image> getUserImages(Long userId);
    Image getImageById(Long id);
    void deleteImage(Long id, Long userId);
}
