// modules/goals/domain/port/GoalCatalogQueryPort.java
package com.sebsrvv.app.modules.goals.domain.port;

import java.util.Map;

public interface GoalCatalogQueryPort {
    /** defaultId -> (name, weeklyTarget) */
    Map<Integer, CatalogItem> getByDefaultIds(Iterable<Integer> defaultIds);

    record CatalogItem(String goalName, Integer weeklyTarget) {}
}
