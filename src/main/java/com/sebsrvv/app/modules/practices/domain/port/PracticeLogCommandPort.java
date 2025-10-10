// domain/port/PracticeLogCommandPort.java
package com.sebsrvv.app.modules.practices.domain.port;

import com.sebsrvv.app.modules.practices.domain.model.PracticeLog;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface PracticeLogCommandPort {
    PracticeLog create(UUID userId, UUID practiceId, OffsetDateTime loggedAt, String note);
}
