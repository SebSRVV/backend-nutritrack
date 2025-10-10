// modules/goals/web/GoalProgressController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.UpsertGoalProgressUseCase;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/goals/{goalId}")
public class GoalProgressController {

    private final UpsertGoalProgressUseCase useCase;

    public GoalProgressController(UpsertGoalProgressUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/progress")
    public ResponseEntity<GoalProgressResponse> upsert(
            @PathVariable UUID userId,
            @PathVariable UUID goalId,
            @RequestBody GoalProgressRequest req
    ) {
        var logDate = LocalDate.parse(req.logDate());
        var gp = useCase.execute(userId, goalId, logDate, req.value(), req.note());

        var body = new GoalProgressResponse(
                gp.getId().toString(),
                gp.getUserId().toString(),
                gp.getGoalId().toString(),
                gp.getLogDate().toString(),
                gp.getValue(),
                gp.getNote(),
                gp.getCreatedAt().toString(),
                gp.getUpdatedAt().toString()
        );
        return ResponseEntity.ok(body);
    }
}
