package com.imagehub.service.impl;

import com.imagehub.entity.User;
import com.imagehub.entity.UserSettings;
import com.imagehub.entity.UploadLog;
import com.imagehub.entity.SecurityLog;
import com.imagehub.entity.LoginDevice;
import com.imagehub.entity.ImagePermission;
import com.imagehub.repository.UserRepository;
import com.imagehub.repository.UserSettingsRepository;
import com.imagehub.repository.UploadLogRepository;
import com.imagehub.repository.SecurityLogRepository;
import com.imagehub.repository.LoginDeviceRepository;
import com.imagehub.repository.ImagePermissionRepository;
import com.imagehub.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserSettingsRepository userSettingsRepository;
    private final UserRepository userRepository;
    private final UploadLogRepository uploadLogRepository;
    private final SecurityLogRepository securityLogRepository;
    private final LoginDeviceRepository loginDeviceRepository;
    private final ImagePermissionRepository imagePermissionRepository;

    @Override
    public UserSettings getSettings(Long userId) {
        return userSettingsRepository.findByUserId(userId)
                .orElseGet(() -> createDefaultSettings(userId));
    }

    @Override
    @Transactional
    public UserSettings updateSettings(Long userId, Map<String, Object> settings) {
        UserSettings userSettings = getSettings(userId);

        if (settings.containsKey("linkFormat")) {
            userSettings.setLinkFormat((String) settings.get("linkFormat"));
        }
        if (settings.containsKey("theme")) {
            userSettings.setTheme((String) settings.get("theme"));
        }
        if (settings.containsKey("watermarkEnabled")) {
            userSettings.setWatermarkEnabled((Boolean) settings.get("watermarkEnabled"));
        }
        if (settings.containsKey("compressEnabled")) {
            userSettings.setCompressEnabled((Boolean) settings.get("compressEnabled"));
        }
        if (settings.containsKey("watermarkText")) {
            userSettings.setWatermarkText((String) settings.get("watermarkText"));
        }
        if (settings.containsKey("defaultPermission")) {
            userSettings.setDefaultPermission((Boolean) settings.get("defaultPermission"));
        }

        return userSettingsRepository.save(userSettings);
    }

    @Override
    public User getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    @Override
    @Transactional
    public void updateUserInfo(Long userId, String nickname, String signature, String avatar) {
        User user = getUserInfo(userId);
        // Note: User entity only has username, password, role, deleted, createdAt
        // These fields would need to be added to the User entity if needed
        // For now, this is a placeholder implementation
    }

    @Override
    public Map<String, Object> getStorageStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("usedStorage", 0L);
        stats.put("maxStorage", 100L * 1024 * 1024 * 1024L); // 100GB
        stats.put("imageCount", 0);
        return stats;
    }

    private UserSettings createDefaultSettings(Long userId) {
        UserSettings settings = new UserSettings();
        settings.setUserId(userId);
        return userSettingsRepository.save(settings);
    }

    @Override
    public List<UploadLog> getUploadLogs(Long userId, String range) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime after = switch (range) {
            case "day" -> now.minusDays(1);
            case "week" -> now.minusWeeks(1);
            case "month" -> now.minusMonths(1);
            default -> null;
        };
        if (after != null) {
            return uploadLogRepository.findByUserIdAndUploadedAtAfterOrderByUploadedAtDesc(userId, after);
        }
        return uploadLogRepository.findByUserIdOrderByUploadedAtDesc(userId);
    }

    @Override
    public List<SecurityLog> getSecurityLogs(Long userId) {
        return securityLogRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public List<LoginDevice> getLoginDevices(Long userId) {
        return loginDeviceRepository.findByUserIdOrderByLastActiveDesc(userId);
    }

    @Override
    @Transactional
    public void logoutDevice(Long deviceId) {
        loginDeviceRepository.deleteById(deviceId);
    }

    @Override
    @Transactional
    public void updateImagePermission(Long imageId, Boolean isPublic) {
        ImagePermission permission = imagePermissionRepository.findByImageId(imageId)
                .orElseGet(() -> {
                    ImagePermission p = new ImagePermission();
                    p.setImageId(imageId);
                    return p;
                });
        permission.setIsPublic(isPublic);
        imagePermissionRepository.save(permission);
    }
}