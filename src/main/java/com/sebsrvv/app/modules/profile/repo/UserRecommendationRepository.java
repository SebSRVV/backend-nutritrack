package com.sebsrvv.app.modules.profile.repo;

import com.sebsrvv.app.modules.profile.entity.UserRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRecommendationRepository extends JpaRepository<UserRecommendation, UUID> {}
