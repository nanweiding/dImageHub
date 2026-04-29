package com.imagehub.service;

import com.imagehub.entity.User;
import com.imagehub.repository.UserRepository;
import com.imagehub.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }

    @Test
    void register_shouldCreateNewUser_whenUsernameNotExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User result = userService.register("testuser", "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertTrue(passwordEncoder.matches("password123", result.getPassword()));
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrowException_whenUsernameExists() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            userService.register("existinguser", "password")
        );
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void login_shouldReturnUser_whenCredentialsCorrect() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("password123"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User result = userService.login("testuser", "password123");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }


    @Test
    void login_shouldThrowException_whenPasswordIncorrect() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword(passwordEncoder.encode("correctpassword"));
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () ->
            userService.login("testuser", "wrongpassword")
        );
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            userService.login("nonexistent", "password")
        );
    }
}