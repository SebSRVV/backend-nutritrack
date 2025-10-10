// modules/practices/web/PracticeUpdateController.java
package com.sebsrvv.app.modules.practices.web;

import com.sebsrvv.app.modules.practices.application.UpdatePracticeUseCase;
import com.sebsrvv.app.modules.practices.domain.port.PracticeUpdateCommandPort;
import com.sebsrvv.app.modules.practices.web.dto.PracticeUpdateRequest;
import com.sebsrvv.app.modules.practices.web.dto.PracticeUpdateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/practices/{practiceId}")
public class PracticeUpdateController {

    private final UpdatePracticeUseCase useCase;

    public PracticeUpdateController(UpdatePracticeUseCase useCase) {
        this.useCase = useCase;
    }

    @PatchMapping
    public ResponseEntity<PracticeUpdateResponse> patch(
            @PathVariable UUID userId,
            @PathVariable UUID practiceId,
            @RequestBody PracticeUpdateRequest req
    ) {
        var result = useCase.execute(userId, practiceId,
                new PracticeUpdateCommandPort.UpdateFields(
                        req.practiceName(),
                        req.description(),
                        req.icon(),
                        req.frequencyTarget(),
                        req.isActive()
                ));

        var body = new PracticeUpdateResponse(
                result.id().toString(),
                result.practiceName(),
                result.description(),
                result.icon(),
                result.frequencyTarget(),
                result.isActive(),
                result.updatedAtIso()
        );
        return ResponseEntity.ok(body);
    }
}
