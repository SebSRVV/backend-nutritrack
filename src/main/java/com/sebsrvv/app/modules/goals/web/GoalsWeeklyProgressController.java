// modules/goals/web/GoalsWeeklyProgressController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.GetGoalsWeeklyProgressUseCase;
import com.sebsrvv.app.modules.goals.web.dto.GoalsWeeklyProgressResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/goals")
public class GoalsWeeklyProgressController {

    private final GetGoalsWeeklyProgressUseCase useCase;

    public GoalsWeeklyProgressController(GetGoalsWeeklyProgressUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/progress")
    public ResponseEntity<List<GoalsWeeklyProgressResponse>> getProgress(
            @PathVariable UUID userId,
            @RequestParam String weekStart,
            @RequestParam String weekEnd
    ) {
        LocalDate start = LocalDate.parse(weekStart);
        LocalDate end   = LocalDate.parse(weekEnd);

        var result = useCase.execute(userId, start, end);

        var body = result.stream().map(g ->
                new GoalsWeeklyProgressResponse(
                        g.getGoalId().toString(),
                        g.getDefaultId(),
                        g.getGoalName(),
                        g.getWeeklyTarget(),
                        g.getCompletedThisWeek(),
                        g.getRemainingThisWeek(),
                        // redondeo opcional a 1 decimal:
                        Math.round(g.getProgressPercent() * 10.0) / 10.0,
                        g.getIsActive(),
                        g.getStreakCurrent(),
                        g.getStreakBest()
                )
        ).toList();

        return ResponseEntity.ok(body);
    }
}
