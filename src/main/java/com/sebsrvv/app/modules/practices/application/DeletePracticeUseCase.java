// modules/practices/application/DeletePracticeUseCase.java
package com.sebsrvv.app.modules.practices.application;

import com.sebsrvv.app.modules.practices.domain.port.PracticeDeleteCommandPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeletePracticeUseCase {

    private final PracticeDeleteCommandPort port;

    public DeletePracticeUseCase(PracticeDeleteCommandPort port) {
        this.port = port;
    }

    public PracticeDeleteCommandPort.DeletedPractice execute(UUID userId, UUID practiceId, boolean soft) {
        return port.delete(userId, practiceId, soft);
    }
}
