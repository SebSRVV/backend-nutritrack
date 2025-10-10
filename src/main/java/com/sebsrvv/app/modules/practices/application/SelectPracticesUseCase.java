// practices/application/SelectPracticesUseCase.java
package com.sebsrvv.app.modules.practices.application;

import com.sebsrvv.app.modules.practices.domain.model.PracticeSelection;
import com.sebsrvv.app.modules.practices.domain.port.PracticeCatalogQueryPort;
import com.sebsrvv.app.modules.practices.domain.port.PracticeSelectionCommandPort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SelectPracticesUseCase {
    private final PracticeSelectionCommandPort selectionPort;
    private final PracticeCatalogQueryPort catalogPort;

    public SelectPracticesUseCase(PracticeSelectionCommandPort selectionPort,
                                  PracticeCatalogQueryPort catalogPort) {
        this.selectionPort = selectionPort;
        this.catalogPort = catalogPort;
    }

    public List<PracticeSelection> execute(UUID userId,
                                           List<PracticeSelectionCommandPort.SelectionCommand> selections) {
        selections.forEach(s -> {
            if (s.defaultId() == null) throw new IllegalArgumentException("defaultId requerido");
            if (s.frequencyTarget() != null &&
                    (s.frequencyTarget() < 0 || s.frequencyTarget() > 7))
                throw new IllegalArgumentException("frequencyTarget debe estar entre 0 y 7");
        });

        List<PracticeSelection> persisted = selectionPort.upsertSelections(userId, selections);

        // Enriquecemos sólo si algo vino null (fallback a catálogo)
        Set<Integer> missing = persisted.stream()
                .filter(p -> p.getPracticeName() == null || p.getIcon() == null
                        || p.getSortOrder() == null || p.getDescription() == null)
                .map(PracticeSelection::getDefaultId).collect(Collectors.toSet());

        if (!missing.isEmpty()) {
            var meta = catalogPort.getByDefaultIds(missing);
            persisted.forEach(p -> {
                var m = meta.get(p.getDefaultId());
                if (m != null) {
                    if (p.getPracticeName() == null) p.setPracticeName(m.name());
                    if (p.getDescription() == null)  p.setDescription(m.description());
                    if (p.getIcon() == null)         p.setIcon(m.icon());
                    if (p.getSortOrder() == null)    p.setSortOrder(m.sortOrder());
                }
            });
        }

        persisted.sort(Comparator.comparing(PracticeSelection::getSortOrder,
                        Comparator.nullsLast(Integer::compareTo))
                .thenComparing(PracticeSelection::getDefaultId));

        return persisted;
    }
}
