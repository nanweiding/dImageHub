package com.imagehub.controller;

import com.imagehub.entity.Image;
import com.imagehub.entity.Tag;
import com.imagehub.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
public class TagController {

    private final TagService tagService;

    // Tag CRUD
    @PostMapping("/tags")
    public ResponseEntity<Map<String, Object>> createTag(
            @RequestParam Long userId,
            @RequestParam String name,
            @RequestParam(required = false) String color) {
        try {
            Tag tag = tagService.createTag(userId, name, color);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "标签创建成功");
            response.put("tag", Map.of(
                "id", tag.getId(),
                "name", tag.getName(),
                "color", tag.getColor() != null ? tag.getColor() : "#0071E3",
                "createdAt", tag.getCreatedAt() != null ? tag.getCreatedAt().toString() : ""
            ));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @GetMapping("/tags/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserTags(@PathVariable Long userId) {
        List<Tag> tags = tagService.getUserTags(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("tags", tags.stream().map(tag -> Map.of(
            "id", tag.getId(),
            "name", tag.getName(),
            "color", tag.getColor() != null ? tag.getColor() : "#0071E3",
            "createdAt", tag.getCreatedAt() != null ? tag.getCreatedAt().toString() : ""
        )).collect(Collectors.toList()));
        return ResponseEntity.ok(response);
    }

    @PutMapping("/tags/{id}")
    public ResponseEntity<Map<String, Object>> updateTag(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String color) {
        try {
            Tag tag = tagService.updateTag(id, name, color, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "标签更新成功");
            response.put("tag", Map.of(
                "id", tag.getId(),
                "name", tag.getName(),
                "color", tag.getColor() != null ? tag.getColor() : "#0071E3"
            ));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @DeleteMapping("/tags/{id}")
    public ResponseEntity<Map<String, Object>> deleteTag(
            @PathVariable Long id,
            @RequestParam Long userId) {
        try {
            tagService.deleteTag(id, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "标签删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // Image-Tag association
    @PostMapping("/images/{imageId}/tags")
    public ResponseEntity<Map<String, Object>> addTagToImage(
            @PathVariable Long imageId,
            @RequestParam Long tagId,
            @RequestParam Long userId) {
        try {
            Image image = tagService.addTagToImage(imageId, tagId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "标签添加成功");
            response.put("imageTags", image.getTags().stream().map(tag -> Map.of(
                "id", tag.getId(),
                "name", tag.getName(),
                "color", tag.getColor() != null ? tag.getColor() : "#0071E3"
            )).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @DeleteMapping("/images/{imageId}/tags/{tagId}")
    public ResponseEntity<Map<String, Object>> removeTagFromImage(
            @PathVariable Long imageId,
            @PathVariable Long tagId,
            @RequestParam Long userId) {
        try {
            Image image = tagService.removeTagFromImage(imageId, tagId, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "标签移除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // Filter images by tags
    @GetMapping("/images/tags/{tagIds}")
    public ResponseEntity<Map<String, Object>> getImagesByTags(
            @PathVariable String tagIds,
            @RequestParam Long userId) {
        try {
            List<Long> tagIdList = Arrays.stream(tagIds.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Image> images = tagService.getImagesByTags(tagIdList, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("images", images.stream().map(img -> Map.of(
                "id", img.getId(),
                "originalFilename", img.getOriginalFilename(),
                "fileSize", img.getFileSize() != null ? img.getFileSize() : 0,
                "contentType", img.getContentType() != null ? img.getContentType() : "",
                "uploadTime", img.getUploadTime() != null ? img.getUploadTime().toString() : "",
                "folderId", img.getFolderId() != null ? img.getFolderId() : "",
                "viewCount", img.getViewCount() != null ? img.getViewCount() : 0,
                "url", "/api/images/" + img.getId(),
                "tags", img.getTags().stream().map(tag -> Map.of(
                    "id", tag.getId(),
                    "name", tag.getName(),
                    "color", tag.getColor() != null ? tag.getColor() : "#0071E3"
                )).collect(Collectors.toList())
            )).collect(Collectors.toList()));
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    // Tag statistics
    @GetMapping("/tags/stats/{userId}")
    public ResponseEntity<Map<String, Object>> getTagStats(@PathVariable Long userId) {
        Map<String, Object> stats = tagService.getTagStats(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("stats", stats);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }
}
