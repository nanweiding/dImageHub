package com.imagehub.controller;

import com.imagehub.entity.User;
import com.imagehub.entity.UserSettings;
import com.imagehub.entity.UploadLog;
import com.imagehub.entity.SecurityLog;
import com.imagehub.entity.LoginDevice;
import com.imagehub.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
public class UserProfileController {

    private final UserProfileService userProfileService;

    @GetMapping("/{userId}/settings")
    public ResponseEntity<Map<String, Object>> getSettings(@PathVariable Long userId) {
        try {
            UserSettings settings = userProfileService.getSettings(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("settings", Map.of(
                "linkFormat", settings.getLinkFormat() != null ? settings.getLinkFormat() : "url",
                "theme", settings.getTheme() != null ? settings.getTheme() : "light",
                "watermarkEnabled", settings.getWatermarkEnabled() != null ? settings.getWatermarkEnabled() : false,
                "compressEnabled", settings.getCompressEnabled() != null ? settings.getCompressEnabled() : false,
                "watermarkText", settings.getWatermarkText() != null ? settings.getWatermarkText() : "",
                "defaultPermission", settings.getDefaultPermission() != null ? settings.getDefaultPermission() : true
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @PutMapping("/{userId}/settings")
    public ResponseEntity<Map<String, Object>> updateSettings(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> settings) {
        try {
            UserSettings updated = userProfileService.updateSettings(userId, settings);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "设置更新成功");
            response.put("settings", Map.of(
                "linkFormat", updated.getLinkFormat(),
                "theme", updated.getTheme(),
                "watermarkEnabled", updated.getWatermarkEnabled(),
                "compressEnabled", updated.getCompressEnabled(),
                "watermarkText", updated.getWatermarkText(),
                "defaultPermission", updated.getDefaultPermission()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GetMapping("/{userId}/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        try {
            User user = userProfileService.getUserInfo(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", Map.of(
                "id", user.getId(),
                "username", user.getUsername(),
                "role", user.getRole()
            ));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GetMapping("/{userId}/storage")
    public ResponseEntity<Map<String, Object>> getStorageStats(@PathVariable Long userId) {
        try {
            Map<String, Object> stats = userProfileService.getStorageStats(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GetMapping("/{userId}/upload-logs")
    public ResponseEntity<Map<String, Object>> getUploadLogs(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "all") String range) {
        try {
            List<UploadLog> logs = userProfileService.getUploadLogs(userId, range);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("logs", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GetMapping("/{userId}/security-logs")
    public ResponseEntity<Map<String, Object>> getSecurityLogs(@PathVariable Long userId) {
        try {
            List<SecurityLog> logs = userProfileService.getSecurityLogs(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("logs", logs);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @GetMapping("/{userId}/devices")
    public ResponseEntity<Map<String, Object>> getLoginDevices(@PathVariable Long userId) {
        try {
            List<LoginDevice> devices = userProfileService.getLoginDevices(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("devices", devices);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<Map<String, Object>> logoutDevice(@PathVariable Long deviceId) {
        try {
            userProfileService.logoutDevice(deviceId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "设备已登出");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return errorResponse(e.getMessage());
        }
    }

    @PutMapping("/images/{imageId}/permission")
    public ResponseEntity<Map<String, Object>> updateImagePermission(
            @PathVariable Long imageId,
            @RequestBody Map<String, Object> permission) {
        try {
            Boolean isPublic = (Boolean) permission.get("isPublic");
            userProfileService.updateImagePermission(imageId, isPublic);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "权限已更新");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
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