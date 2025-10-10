// modules/practices/domain/port/PracticeDeleteCommandPort.java
package com.sebsrvv.app.modules.practices.domain.port;

import java.util.UUID;

public interface PracticeDeleteCommandPort {
    DeletedPractice delete(UUID userId, UUID practiceId, boolean soft);

    record DeletedPractice(UUID id, boolean isActive) {}
}
