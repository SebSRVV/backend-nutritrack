// modules/practices/domain/port/PracticeUpdateCommandPort.java
package com.sebsrvv.app.modules.practices.domain.port;

import java.util.UUID;

public interface PracticeUpdateCommandPort {
    UpdatedPractice update(UUID userId, UUID practiceId, UpdateFields fields);

    record UpdateFields(String practiceName, String description, String icon,
                        Integer frequencyTarget, Boolean isActive) {}

    record UpdatedPractice(UUID id, String practiceName, String description, String icon,
                           Integer frequencyTarget, Boolean isActive, String updatedAtIso) {}
}
