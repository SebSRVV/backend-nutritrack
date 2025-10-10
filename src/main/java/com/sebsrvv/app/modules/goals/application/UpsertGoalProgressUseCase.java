// modules/goals/application/UpsertGoalProgressUseCase.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.model.GoalProgress;
import com.sebsrvv.app.modules.goals.domain.port.GoalProgressCommandPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class UpsertGoalProgressUseCase {

    private final GoalProgressCommandPort port;

    public UpsertGoalProgressUseCase(GoalProgressCommandPort port) {
        this.port = port;
    }

    public GoalProgress execute(UUID userId, UUID goalId, LocalDate logDate, Integer value, String note) {
        if (logDate == null) throw new IllegalArgumentException("logDate requerido (YYYY-MM-DD)");
        if (value == null || (value != 0 && value != 1))
            throw new IllegalArgumentException("value debe ser 0 o 1");
        return port.upsert(userId, goalId, logDate, value, note);
    }
}
