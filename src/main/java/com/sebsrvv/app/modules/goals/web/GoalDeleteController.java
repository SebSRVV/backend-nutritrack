// modules/goals/web/GoalDeleteController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.DeleteGoalUseCase;
import com.sebsrvv.app.modules.goals.web.dto.GoalDeleteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/goals/{goalId}")
public class GoalDeleteController {

    private final DeleteGoalUseCase useCase;

    public GoalDeleteController(DeleteGoalUseCase useCase) {
        this.useCase = useCase;
    }

    @DeleteMapping
    public ResponseEntity<GoalDeleteResponse> delete(
            @PathVariable UUID userId,
            @PathVariable UUID goalId,
            @RequestParam(required = false, defaultValue = "hard") String mode
    ) {
        boolean soft = "soft".equalsIgnoreCase(mode);
        var result = useCase.execute(userId, goalId, soft);
        return ResponseEntity.ok(new GoalDeleteResponse(result.id().toString(), result.isActive()));
    }
}
