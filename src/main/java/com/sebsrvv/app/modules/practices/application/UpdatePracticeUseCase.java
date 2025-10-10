// modules/practices/application/UpdatePracticeUseCase.java
package com.sebsrvv.app.modules.practices.application;

import com.sebsrvv.app.modules.practices.domain.port.PracticeUpdateCommandPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UpdatePracticeUseCase {

    private final PracticeUpdateCommandPort port;

    public UpdatePracticeUseCase(PracticeUpdateCommandPort port) {
        this.port = port;
    }

    public PracticeUpdateCommandPort.UpdatedPractice execute(
            UUID userId, UUID practiceId, PracticeUpdateCommandPort.UpdateFields fields) {

        boolean empty = fields.practiceName() == null
                && fields.description() == null
                && fields.icon() == null
                && fields.frequencyTarget() == null
                && fields.isActive() == null;
        if (empty) throw new IllegalArgumentException("No hay campos para actualizar");

        if (fields.frequencyTarget() != null) {
            int ft = fields.frequencyTarget();
            if (ft < 0 || ft > 7) throw new IllegalArgumentException("frequencyTarget debe estar entre 0 y 7");
        }

        return port.update(userId, practiceId, fields);
    }
}
