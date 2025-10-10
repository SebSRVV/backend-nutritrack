// modules/practices/domain/port/PracticeProgressQueryPort.java
package com.sebsrvv.app.modules.practices.domain.port;

import java.time.LocalDate;
import java.util.*;

public interface PracticeProgressQueryPort {

    record UserPractice(
            UUID id,              // healthy_practices.id
            Integer defaultId,
            String practiceName,
            Integer frequencyTarget,
            Boolean isActive
    ) {}

    /** Prácticas del usuario (puedes filtrar inactivos en el adapter si quieres). */
    List<UserPractice> findUserPractices(UUID userId);

    /** Mapa: practiceId -> (date -> count) para el rango indicado. */
    Map<UUID, Map<LocalDate, Integer>> findDailyCounts(UUID userId, Collection<UUID> practiceIds,
                                                       LocalDate from, LocalDate to);
}
