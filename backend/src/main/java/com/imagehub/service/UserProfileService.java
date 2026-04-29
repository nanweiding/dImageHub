package com.imagehub.service;

import com.imagehub.entity.UserSettings;
import com.imagehub.entity.User;
import com.imagehub.entity.UploadLog;
import com.imagehub.entity.SecurityLog;
import com.imagehub.entity.LoginDevice;
import java.util.Map;
import java.util.List;

public interface UserProfileService {
    UserSettings getSettings(Long userId);
    UserSettings updateSettings(Long userId, Map<String, Object> settings);
    User getUserInfo(Long userId);
    void updateUserInfo(Long userId, String nickname, String signature, String avatar);
    Map<String, Object> getStorageStats(Long userId);
    List<UploadLog> getUploadLogs(Long userId, String range);
    List<SecurityLog> getSecurityLogs(Long userId);
    List<LoginDevice> getLoginDevices(Long userId);
    void logoutDevice(Long deviceId);
    void updateImagePermission(Long imageId, Boolean isPublic);
}