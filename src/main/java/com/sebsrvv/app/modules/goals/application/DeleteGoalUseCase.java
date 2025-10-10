// modules/goals/application/DeleteGoalUseCase.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.port.GoalDeleteCommandPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteGoalUseCase {
    private final GoalDeleteCommandPort port;
    public DeleteGoalUseCase(GoalDeleteCommandPort port) { this.port = port; }

    public GoalDeleteCommandPort.DeletedGoal execute(UUID userId, UUID goalId, boolean soft) {
        return port.delete(userId, goalId, soft);
    }
}
