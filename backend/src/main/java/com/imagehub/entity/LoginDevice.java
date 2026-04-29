package com.imagehub.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
@Entity
@Table(name = "login_devices")
public class LoginDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @Column(name = "is_current")
    private Boolean isCurrent = false;
}