// modules/goals/web/GoalUpdateController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.UpdateGoalUseCase;
import com.sebsrvv.app.modules.goals.domain.port.GoalUpdateCommandPort;
import com.sebsrvv.app.modules.goals.web.dto.GoalUpdateRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalUpdateResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/goals/{goalId}")
public class GoalUpdateController {

    private final UpdateGoalUseCase useCase;

    public GoalUpdateController(UpdateGoalUseCase useCase) {
        this.useCase = useCase;
    }

    @PatchMapping
    public ResponseEntity<GoalUpdateResponse> patch(
            @PathVariable UUID userId,
            @PathVariable UUID goalId,
            @RequestBody GoalUpdateRequest req
    ) {
        var result = useCase.execute(userId, goalId,
                new GoalUpdateCommandPort.UpdateFields(
                        req.goalName(),
                        req.weeklyTarget(),
                        req.isActive(),
                        req.categoryId(),
                        req.description()
                ));

        var body = new GoalUpdateResponse(
                result.id().toString(),
                result.userId().toString(),
                result.defaultId(),
                result.goalName(),
                result.weeklyTarget(),
                result.isActive(),
                result.categoryId(),
                result.description(),
                result.updatedAtIso()
        );
        return ResponseEntity.ok(body);
    }
}
