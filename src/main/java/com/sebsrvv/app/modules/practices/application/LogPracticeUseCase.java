// application/LogPracticeUseCase.java
package com.sebsrvv.app.modules.practices.application;

import com.sebsrvv.app.modules.practices.domain.model.PracticeLog;
import com.sebsrvv.app.modules.practices.domain.port.PracticeLogCommandPort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class LogPracticeUseCase {

    private final PracticeLogCommandPort logs;

    public LogPracticeUseCase(PracticeLogCommandPort logs) {
        this.logs = logs;
    }

    public PracticeLog execute(UUID userId, UUID practiceId, OffsetDateTime loggedAt, String note) {
        if (loggedAt == null) loggedAt = OffsetDateTime.now(); // default
        return logs.create(userId, practiceId, loggedAt, note);
    }
}
