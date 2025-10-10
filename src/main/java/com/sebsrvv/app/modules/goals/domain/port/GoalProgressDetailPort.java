// modules/goals/domain/port/GoalProgressDetailPort.java
package com.sebsrvv.app.modules.goals.domain.port;

import java.time.LocalDate;
import java.util.*;

public interface GoalProgressDetailPort {

    record Goal(UUID id, Integer defaultId, String goalName, Integer weeklyTarget, Boolean isActive) {}
    record DayLog(LocalDate date, Integer value, String note) {}

    Goal findGoal(UUID userId, UUID goalId);
    List<DayLog> findDaily(UUID userId, UUID goalId, LocalDate from, LocalDate to);
}
