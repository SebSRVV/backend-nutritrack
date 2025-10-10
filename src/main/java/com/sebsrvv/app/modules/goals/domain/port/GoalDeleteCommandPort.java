// modules/goals/domain/port/GoalDeleteCommandPort.java
package com.sebsrvv.app.modules.goals.domain.port;

import java.util.UUID;

public interface GoalDeleteCommandPort {
    DeletedGoal delete(UUID userId, UUID goalId, boolean soft);

    record DeletedGoal(UUID id, boolean isActive) {}
}
