// modules/goals/application/UpdateGoalUseCase.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.port.GoalUpdateCommandPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdateGoalUseCase {

    private final GoalUpdateCommandPort port;

    public UpdateGoalUseCase(GoalUpdateCommandPort port) {
        this.port = port;
    }

    public GoalUpdateCommandPort.UpdatedGoal execute(UUID userId, UUID goalId,
                                                     GoalUpdateCommandPort.UpdateFields f) {

        boolean empty = f.goalName() == null
                && f.weeklyTarget() == null
                && f.isActive() == null
                && f.categoryId() == null
                && f.description() == null;
        if (empty) throw new IllegalArgumentException("No hay campos para actualizar");

        if (f.weeklyTarget() != null) {
            int wt = f.weeklyTarget();
            if (wt < 1 || wt > 7) throw new IllegalArgumentException("weeklyTarget debe estar entre 1 y 7");
        }
        return port.update(userId, goalId, f);
    }
}
