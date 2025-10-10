// modules/practices/web/PracticeDeleteController.java
package com.sebsrvv.app.modules.practices.web;

import com.sebsrvv.app.modules.practices.application.DeletePracticeUseCase;
import com.sebsrvv.app.modules.practices.web.dto.PracticeDeleteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/practices/{practiceId}")
public class PracticeDeleteController {

    private final DeletePracticeUseCase useCase;

    public PracticeDeleteController(DeletePracticeUseCase useCase) {
        this.useCase = useCase;
    }

    @DeleteMapping
    public ResponseEntity<PracticeDeleteResponse> deletePractice(
            @PathVariable UUID userId,
            @PathVariable UUID practiceId,
            @RequestParam(required = false, defaultValue = "hard") String mode
    ) {
        boolean soft = "soft".equalsIgnoreCase(mode);
        var result = useCase.execute(userId, practiceId, soft);
        return ResponseEntity.ok(new PracticeDeleteResponse(result.id().toString(), result.isActive()));
    }
}
