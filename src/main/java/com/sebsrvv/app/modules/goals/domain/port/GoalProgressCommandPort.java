// modules/goals/domain/port/GoalProgressCommandPort.java
package com.sebsrvv.app.modules.goals.domain.port;

import com.sebsrvv.app.modules.goals.domain.model.GoalProgress;

import java.time.LocalDate;
import java.util.UUID;

public interface GoalProgressCommandPort {
    /** Crea o actualiza (idempotente por userId+goalId+logDate). */
    GoalProgress upsert(UUID userId, UUID goalId, LocalDate logDate, int value, String note);
}
