package com.imagehub.repository;

import com.imagehub.entity.LoginDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LoginDeviceRepository extends JpaRepository<LoginDevice, Long> {
    List<LoginDevice> findByUserIdOrderByLastActiveDesc(Long userId);
    Optional<LoginDevice> findByUserIdAndIsCurrent(Long userId, Boolean isCurrent);
}