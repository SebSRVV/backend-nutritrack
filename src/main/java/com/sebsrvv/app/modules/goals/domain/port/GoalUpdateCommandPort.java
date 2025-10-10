// modules/goals/domain/port/GoalUpdateCommandPort.java
package com.sebsrvv.app.modules.goals.domain.port;

import java.util.UUID;

public interface GoalUpdateCommandPort {
    UpdatedGoal update(UUID userId, UUID goalId, UpdateFields fields);

    record UpdateFields(String goalName,
                        Integer weeklyTarget,
                        Boolean isActive,
                        Integer categoryId,
                        String description) {}

    record UpdatedGoal(UUID id, UUID userId, Integer defaultId, String goalName,
                       Integer weeklyTarget, Boolean isActive, Integer categoryId,
                       String description, String updatedAtIso) {}
}
