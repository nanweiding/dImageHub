package com.imagehub.controller;

import com.imagehub.entity.Image;
import com.imagehub.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {
        try {
            if (file.isEmpty()) {
                return errorResponse("请选择要上传的图片");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return errorResponse("只能上传图片文件");
            }

            Image image = imageService.uploadImage(file, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "上传成功");
            response.put("image", Map.of(
                "id", image.getId(),
                "originalFilename", image.getOriginalFilename(),
                "fileSize", image.getFileSize(),
                "contentType", image.getContentType(),
                "url", "/api/images/" + image.getId()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse("上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserImages(@PathVariable Long userId) {
        List<Image> images = imageService.getUserImages(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("images", images.stream().map(img -> Map.of(
            "id", img.getId(),
            "originalFilename", img.getOriginalFilename(),
            "fileSize", img.getFileSize(),
            "contentType", img.getContentType(),
            "uploadTime", img.getUploadTime().toString()
        )).toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getImage(@PathVariable Long id) {
        try {
            Image image = imageService.getImageById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("image", Map.of(
                "id", image.getId(),
                "originalFilename", image.getOriginalFilename(),
                "filePath", image.getFilePath(),
                "fileSize", image.getFileSize(),
                "contentType", image.getContentType()
            ));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteImage(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            imageService.deleteImage(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }
}
