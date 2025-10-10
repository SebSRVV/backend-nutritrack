// practices/domain/port/PracticeSelectionCommandPort.java
package com.sebsrvv.app.modules.practices.domain.port;

import com.sebsrvv.app.modules.practices.domain.model.PracticeSelection;
import java.util.List;
import java.util.UUID;

public interface PracticeSelectionCommandPort {
    List<PracticeSelection> upsertSelections(UUID userId, List<SelectionCommand> selections);
    record SelectionCommand(Integer defaultId, Boolean isActive, Integer frequencyTarget) {}
}
