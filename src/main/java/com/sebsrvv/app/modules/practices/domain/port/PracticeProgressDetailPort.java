// modules/practices/domain/port/PracticeProgressDetailPort.java
package com.sebsrvv.app.modules.practices.domain.port;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

public interface PracticeProgressDetailPort {

    record Practice(
            UUID id, String practiceName, Integer frequencyTarget, Boolean isActive
    ) {}

    record Log(UUID id, OffsetDateTime loggedAt, LocalDate loggedDate) {}

    Practice findPractice(UUID userId, UUID practiceId);

    /** Logs de la práctica en el rango [from, to] (inclusive). */
    List<Log> findLogs(UUID userId, UUID practiceId, LocalDate from, LocalDate to);
}
