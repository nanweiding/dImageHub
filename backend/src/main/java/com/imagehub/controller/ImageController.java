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
import java.util.ArrayList;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "folderId", required = false) Long folderId) {
        try {
            if (file.isEmpty()) {
                return errorResponse("请选择要上传的图片");
            }

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return errorResponse("只能上传图片文件");
            }

            Image image = imageService.uploadImage(file, userId, folderId);
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
    public ResponseEntity<Map<String, Object>> getUserImages(
            @PathVariable Long userId,
            @RequestParam(value = "sortBy", required = false, defaultValue = "uploadTime") String sortBy,
            @RequestParam(value = "sortOrder", required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        List<Image> images = imageService.getUserImages(userId, sortBy, sortOrder, page, pageSize);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("images", images.stream().map(img -> Map.of(
            "id", img.getId(),
            "originalFilename", img.getOriginalFilename(),
            "fileSize", img.getFileSize(),
            "contentType", img.getContentType(),
            "uploadTime", img.getUploadTime() != null ? img.getUploadTime().toString() : "",
            "folderId", img.getFolderId() != null ? img.getFolderId() : "",
            "viewCount", img.getViewCount() != null ? img.getViewCount() : 0,
            "remark", img.getRemark() != null ? img.getRemark() : "",
            "tags", img.getTags() != null ? img.getTags().stream().map(tag -> Map.of(
                "id", tag.getId(),
                "name", tag.getName(),
                "color", tag.getColor()
            )).toList() : List.of(),
            "url", "/api/images/" + img.getId()
        )).toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getImage(@PathVariable Long id) {
        try {
            Image image = imageService.getImageById(id);
            imageService.incrementViewCount(id);
            java.nio.file.Path filePath = java.nio.file.Paths.get(image.getFilePath());
            if (!java.nio.file.Files.exists(filePath)) {
                return errorResponse("图片文件不存在");
            }
            byte[] imageData = java.nio.file.Files.readAllBytes(filePath);
            return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(image.getContentType()))
                .header("Content-Disposition", "attachment; filename=\"" + image.getOriginalFilename() + "\"")
                .body(imageData);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        } catch (java.io.IOException e) {
            return errorResponse("读取图片失败");
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

    @PutMapping("/{id}/move")
    public ResponseEntity<Map<String, Object>> moveImage(
            @PathVariable Long id,
            @RequestParam Long folderId,
            @RequestParam Long userId) {
        try {
            imageService.moveToFolder(id, folderId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "移动成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateImage(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam(required = false) String filename,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) Long folderId) {
        try {
            imageService.updateImage(id, userId, filename, remark, folderId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "更新成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @PutMapping("/batch/delete")
    public ResponseEntity<Map<String, Object>> batchDeleteImages(
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> imageIds = ((List<?>) request.get("imageIds")).stream()
                .map(i -> ((Number) i).longValue())
                .toList();
            Long userId = Long.valueOf(request.get("userId").toString());
            imageService.batchDelete(imageIds, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @PutMapping("/batch/move")
    public ResponseEntity<Map<String, Object>> batchMoveImages(
            @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> imageIds = ((List<?>) request.get("imageIds")).stream()
                .map(i -> ((Number) i).longValue())
                .toList();
            Long folderId = request.get("folderId") != null ? Long.valueOf(request.get("folderId").toString()) : null;
            Long userId = Long.valueOf(request.get("userId").toString());
            imageService.batchMove(imageIds, folderId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "批量移动成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // Trash / Recovery endpoints
    @GetMapping("/trash/{userId}")
    public ResponseEntity<Map<String, Object>> getTrashImages(@PathVariable Long userId) {
        List<Image> images = imageService.getTrashImages(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("images", images.stream().map(img -> Map.of(
            "id", img.getId(),
            "originalFilename", img.getOriginalFilename(),
            "fileSize", img.getFileSize(),
            "contentType", img.getContentType(),
            "uploadTime", img.getUploadTime().toString(),
            "deletedAt", img.getDeletedAt() != null ? img.getDeletedAt().toString() : "",
            "folderId", img.getFolderId() != null ? img.getFolderId() : ""
        )).toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<Map<String, Object>> restoreImage(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            imageService.restoreImage(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "恢复成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Map<String, Object>> permanentDeleteImage(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            imageService.permanentDeleteImage(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "彻底删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @DeleteMapping("/trash/{userId}")
    public ResponseEntity<Map<String, Object>> emptyTrash(@PathVariable Long userId) {
        try {
            imageService.emptyTrash(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "清空回收站成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // Admin endpoints
    @GetMapping("/admin/all")
    public ResponseEntity<Map<String, Object>> getAllImages() {
        List<Image> images = imageService.getAllImages();
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("images", images.stream().map(img -> Map.of(
            "id", img.getId(),
            "userId", img.getUserId(),
            "originalFilename", img.getOriginalFilename(),
            "fileSize", img.getFileSize(),
            "contentType", img.getContentType(),
            "uploadTime", img.getUploadTime().toString(),
            "folderId", img.getFolderId() != null ? img.getFolderId() : ""
        )).toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stats", imageService.getStats());
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }
}
