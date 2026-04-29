package com.imagehub.controller;

import com.imagehub.entity.Folder;
import com.imagehub.service.FolderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/folders")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
public class FolderController {

    private final FolderService folderService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createFolder(
            @RequestBody Map<String, Object> request) {
        try {
            String name = (String) request.get("name");
            Object userIdObj = request.get("userId");
            Long userId = userIdObj instanceof Number ? ((Number) userIdObj).longValue() : Long.parseLong(userIdObj.toString());
            Folder folder = folderService.createFolder(name, userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("folder", Map.of(
                "id", folder.getId(),
                "name", folder.getName(),
                "createdAt", folder.getCreatedAt().toString()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserFolders(@PathVariable Long userId) {
        List<Folder> folders = folderService.getUserFolders(userId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("folders", folders.stream().map(f -> Map.of(
            "id", f.getId(),
            "name", f.getName(),
            "createdAt", f.getCreatedAt() != null ? f.getCreatedAt().toString() : ""
        )).toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{folderId}")
    public ResponseEntity<Map<String, Object>> deleteFolder(
            @PathVariable Long folderId,
            @RequestParam Long userId) {
        try {
            folderService.deleteFolder(folderId, userId);
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