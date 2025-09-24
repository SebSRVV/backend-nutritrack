package com.sebsrvv.app.modules.profile.repo;

import com.sebsrvv.app.modules.profile.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {}
