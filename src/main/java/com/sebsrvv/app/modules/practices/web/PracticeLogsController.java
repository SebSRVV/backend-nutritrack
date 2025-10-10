// web/PracticeLogsController.java
package com.sebsrvv.app.modules.practices.web;

import com.sebsrvv.app.modules.practices.application.LogPracticeUseCase;
import com.sebsrvv.app.modules.practices.web.dto.PracticeLogRequest;
import com.sebsrvv.app.modules.practices.web.dto.PracticeLogResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/practices/{practiceId}")
public class PracticeLogsController {

    private final LogPracticeUseCase useCase;

    public PracticeLogsController(LogPracticeUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/logs")
    public ResponseEntity<PracticeLogResponse> createLog(
            @PathVariable UUID userId,
            @PathVariable UUID practiceId,
            @RequestBody PracticeLogRequest request
    ) {
        OffsetDateTime loggedAt = (request.loggedAt() == null || request.loggedAt().isBlank())
                ? null
                : OffsetDateTime.parse(request.loggedAt());

        var log = useCase.execute(userId, practiceId, loggedAt, request.note());

        var body = new PracticeLogResponse(
                log.getId().toString(),
                log.getUserId().toString(),
                log.getPracticeId().toString(),
                log.getLoggedAt().toString(),
                log.getLoggedDate().toString(),
                log.getNote(),
                log.getCreatedAt().toString()
        );
        return ResponseEntity.ok(body);
    }
}
