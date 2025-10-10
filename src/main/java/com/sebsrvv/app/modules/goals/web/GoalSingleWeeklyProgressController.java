// modules/goals/web/GoalSingleWeeklyProgressController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.GetGoalWeeklyProgressUseCase;
import com.sebsrvv.app.modules.goals.web.dto.GoalSingleWeeklyProgressResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/goals/{goalId}")
public class GoalSingleWeeklyProgressController {

    private final GetGoalWeeklyProgressUseCase useCase;

    public GoalSingleWeeklyProgressController(GetGoalWeeklyProgressUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/progress")
    public ResponseEntity<GoalSingleWeeklyProgressResponse> get(
            @PathVariable UUID userId,
            @PathVariable UUID goalId,
            @RequestParam String weekStart,
            @RequestParam String weekEnd
    ) {
        var start = LocalDate.parse(weekStart);
        var end   = LocalDate.parse(weekEnd);

        var r = useCase.execute(userId, goalId, start, end);

        var body = new GoalSingleWeeklyProgressResponse(
                r.getGoalId().toString(),
                r.getDefaultId(),
                r.getGoalName(),
                r.getWeeklyTarget(),
                r.getDays().stream()
                        .map(d -> new GoalSingleWeeklyProgressResponse.Day(d.date().toString(), d.value(), d.note()))
                        .toList(),
                r.getCompletedThisWeek(),
                r.getRemainingThisWeek(),
                // redondeo opcional a 1 decimal:
                Math.round(r.getProgressPercent() * 10.0) / 10.0,
                r.getIsActive(),
                r.getStreakCurrent(),
                r.getStreakBest()
        );
        return ResponseEntity.ok(body);
    }
}
