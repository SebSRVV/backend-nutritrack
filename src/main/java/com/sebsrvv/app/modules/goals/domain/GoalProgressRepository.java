// src/main/java/com/sebsrvv/app/modules/goals/domain/GoalProgressRepository.java
package com.sebsrvv.app.modules.goals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalProgressRepository extends JpaRepository<GoalProgress, UUID> {
    List<GoalProgress> findByUserIdAndGoalIdOrderByLogDateDesc(UUID userId, UUID goalId);
    List<GoalProgress> findByUserIdAndGoalIdAndLogDateGreaterThanEqualOrderByLogDateDesc(UUID userId, UUID goalId, LocalDate fromDate);
    boolean existsByUserIdAndGoalIdAndLogDate(UUID userId, UUID goalId, LocalDate logDate);
    Optional<GoalProgress> findByUserIdAndGoalIdAndLogDate(UUID userId, UUID goalId, LocalDate logDate);
}
