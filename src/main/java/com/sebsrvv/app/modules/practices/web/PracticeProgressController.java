// modules/practices/web/PracticeProgressController.java
package com.sebsrvv.app.modules.practices.web;

import com.sebsrvv.app.modules.practices.application.GetWeeklyProgressUseCase;
import com.sebsrvv.app.modules.practices.web.dto.PracticeProgressResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/practices")
public class PracticeProgressController {

    private final GetWeeklyProgressUseCase useCase;

    public PracticeProgressController(GetWeeklyProgressUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/progress")
    public ResponseEntity<List<PracticeProgressResponse>> getProgress(
            @PathVariable UUID userId,
            @RequestParam String weekStart,
            @RequestParam String weekEnd
    ) {
        LocalDate start = LocalDate.parse(weekStart);
        LocalDate end   = LocalDate.parse(weekEnd);

        var result = useCase.execute(userId, start, end);

        var body = result.stream().map(p ->
                new PracticeProgressResponse(
                        p.getPracticeId().toString(),
                        p.getDefaultId(),
                        p.getPracticeName(),
                        p.getFrequencyTarget(),
                        p.getCompletionsThisWeek(),
                        p.getRemainingThisWeek(),
                        // redondeo a 1 decimal (si te gusta)
                        Math.round(p.getProgressPercent() * 10.0) / 10.0,
                        p.getIsActive(),
                        p.getDays().stream()
                                .map(d -> new PracticeProgressResponse.Day(d.date().toString(), d.completed(), d.count()))
                                .toList(),
                        p.getStreakCurrent(),
                        p.getStreakBest()
                )
        ).toList();

        return ResponseEntity.ok(body);
    }
}
