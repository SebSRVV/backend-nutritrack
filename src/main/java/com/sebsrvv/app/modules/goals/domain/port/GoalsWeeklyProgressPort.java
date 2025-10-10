// modules/goals/domain/port/GoalsWeeklyProgressPort.java
package com.sebsrvv.app.modules.goals.domain.port;

import java.time.LocalDate;
import java.util.*;

public interface GoalsWeeklyProgressPort {

    record UserGoal(UUID id, Integer defaultId, String goalName, Integer weeklyTarget, Boolean isActive) {}

    /** Goals del usuario (puedes filtrar solo activos en el adapter si prefieres). */
    List<UserGoal> findUserGoals(UUID userId);

    /** Mapa: goalId -> (date -> sum(value)) en [from, to]. */
    Map<UUID, Map<LocalDate, Integer>> findDailyValues(UUID userId, Collection<UUID> goalIds,
                                                       LocalDate from, LocalDate to);
}
