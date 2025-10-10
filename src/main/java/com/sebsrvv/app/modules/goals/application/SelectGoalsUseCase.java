// modules/goals/application/SelectGoalsUseCase.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.model.GoalSelection;
import com.sebsrvv.app.modules.goals.domain.port.GoalCatalogQueryPort;
import com.sebsrvv.app.modules.goals.domain.port.GoalSelectionCommandPort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SelectGoalsUseCase {

    private final GoalSelectionCommandPort selectionPort;
    private final GoalCatalogQueryPort catalogPort;

    public SelectGoalsUseCase(GoalSelectionCommandPort selectionPort,
                              GoalCatalogQueryPort catalogPort) {
        this.selectionPort = selectionPort;
        this.catalogPort = catalogPort;
    }

    public List<GoalSelection> execute(UUID userId, List<GoalSelectionCommandPort.SelectionCommand> selections) {
        selections.forEach(s -> {
            if (s.defaultId() == null) throw new IllegalArgumentException("defaultId requerido");
            if (s.active() == null) throw new IllegalArgumentException("active requerido");
        });

        // Traer metadatos (nombre/weeklyTarget) del catálogo
        var meta = catalogPort.getByDefaultIds(
                selections.stream().map(GoalSelectionCommandPort.SelectionCommand::defaultId).collect(Collectors.toSet())
        );
        if (meta.isEmpty())
            throw new IllegalArgumentException("Ninguno de los defaultId existe en default_goals");

        // Upsert con snapshot de catálogo
        List<GoalSelection> persisted = selectionPort.upsertSelections(userId, selections);

        // Rellenar por si algo vino nulo (fallback seguro)
        persisted.forEach(g -> {
            var m = meta.get(g.getDefaultId());
            if (m != null) {
                if (g.getGoalName() == null) g.setGoalName(m.goalName());
                if (g.getWeeklyTarget() == null) g.setWeeklyTarget(m.weeklyTarget());
            }
        });

        return persisted;
    }
}
