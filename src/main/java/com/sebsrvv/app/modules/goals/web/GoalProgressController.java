// modules/goals/web/GoalProgressController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.UpsertGoalProgressUseCase;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressResponse;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/goals/{goalId}")
public class GoalProgressController {

    private final UpsertGoalProgressUseCase useCase;

    public GoalProgressController(UpsertGoalProgressUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping(
            path = "/progress",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<GoalProgressResponse> upsert(
            @PathVariable UUID userId,                     // opcionalmente puedes validar contra el JWT si lo deseas
            @PathVariable UUID goalId,
            @Valid @RequestBody GoalProgressRequest req,
            @RequestHeader(name = "Authorization", required = false) String authorization
    ) {
        if (authorization == null || authorization.isBlank()) {
            // El RPC necesita el JWT para resolver auth.uid()
            return ResponseEntity.status(401).build();
        }

        var result = useCase.executeViaRpcForwardJwt(
                authorization,
                goalId,
                req.logDate(),
                req.value(),
                req.note()
        );

        // devolvemos el objeto recibido del RPC
        return ResponseEntity.ok(result);
    }
}
