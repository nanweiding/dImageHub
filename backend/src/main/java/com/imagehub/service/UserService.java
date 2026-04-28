package com.imagehub.service;

import com.imagehub.entity.User;

public interface UserService {
    User register(String username, String password);
    User login(String username, String password);
    User findById(Long id);
    boolean existsByUsername(String username);
}
