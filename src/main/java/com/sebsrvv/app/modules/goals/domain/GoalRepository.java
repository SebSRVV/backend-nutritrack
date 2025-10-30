// src/main/java/com/sebsrvv/app/modules/goals/domain/GoalRepository.java
package com.sebsrvv.app.modules.goals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {
    List<Goal> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
