package com.imagehub.service.impl;

import com.imagehub.entity.Image;
import com.imagehub.repository.ImageRepository;
import com.imagehub.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Override
    @Transactional
    public Image uploadImage(MultipartFile file, Long userId) throws IOException {
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

        return imageRepository.save(image);
    }

    @Override
    public List<Image> getUserImages(Long userId) {
        return imageRepository.findByUserId(userId);
    }

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("图片不存在"));
    }

    @Override
    @Transactional
    public void deleteImage(Long id, Long userId) {
        Image image = getImageById(id);
        if (!image.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此图片");
        }

        try {
            Path filePath = Paths.get(image.getFilePath());
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            // 文件删除失败不影响数据库记录删除
        }

        imageRepository.delete(image);
    }
}
