// modules/goals/domain/port/GoalSelectionCommandPort.java
package com.sebsrvv.app.modules.goals.domain.port;

import com.sebsrvv.app.modules.goals.domain.model.GoalSelection;
import java.util.List;
import java.util.UUID;

public interface GoalSelectionCommandPort {
    List<GoalSelection> upsertSelections(UUID userId, List<SelectionCommand> selections);

    record SelectionCommand(Integer defaultId, Boolean active) {}
}
