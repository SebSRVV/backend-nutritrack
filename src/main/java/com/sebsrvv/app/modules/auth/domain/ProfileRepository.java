package com.sebsrvv.app.modules.auth.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ProfileRepository extends JpaRepository<UserProfile, UUID> {
    boolean existsByUsernameIgnoreCase(String username);
}
