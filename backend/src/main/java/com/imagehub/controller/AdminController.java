package com.imagehub.controller;

import com.imagehub.entity.User;
import com.imagehub.repository.UserRepository;
import com.imagehub.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "${app.cors.allowed-origins:*}")
public class AdminController {

    private final UserRepository userRepository;
    private final ImageService imageService;

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        List<User> users = userRepository.findByDeleted(false);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("users", users.stream().map(u -> Map.of(
            "id", u.getId(),
            "username", u.getUsername(),
            "role", u.getRole(),
            "createdAt", u.getCreatedAt() != null ? u.getCreatedAt().toString() : ""
        )).toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            user.setDeleted(true);
            userRepository.save(user);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return errorResponse(e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = imageService.getStats();
        long userCount = userRepository.findByDeleted(false).size();
        stats.put("totalUsers", userCount);
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