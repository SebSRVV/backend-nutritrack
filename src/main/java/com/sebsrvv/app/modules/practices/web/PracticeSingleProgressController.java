// modules/practices/web/PracticeSingleProgressController.java
package com.sebsrvv.app.modules.practices.web;

import com.sebsrvv.app.modules.practices.application.GetPracticeWeeklyProgressUseCase;
import com.sebsrvv.app.modules.practices.web.dto.PracticeSingleProgressResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/users/{userId}/practices/{practiceId}")
public class PracticeSingleProgressController {

    private final GetPracticeWeeklyProgressUseCase useCase;

    public PracticeSingleProgressController(GetPracticeWeeklyProgressUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/progress")
    public ResponseEntity<PracticeSingleProgressResponse> getPracticeProgress(
            @PathVariable UUID userId,
            @PathVariable UUID practiceId,
            @RequestParam String weekStart
    ) {
        LocalDate start = LocalDate.parse(weekStart);
        var r = useCase.execute(userId, practiceId, start);

        var body = new PracticeSingleProgressResponse(
                r.getPracticeId().toString(),
                r.getPracticeName(),
                r.getFrequencyTarget(),
                r.getCompletionsThisWeek(),
                r.getDays().stream()
                        .map(d -> new PracticeSingleProgressResponse.Day(
                                d.date().toString(),
                                d.completed(),
                                d.logs().stream()
                                        .map(l -> new PracticeSingleProgressResponse.Log(l.id(), l.loggedAt()))
                                        .toList()
                        )).toList(),
                // redondeo opcional a 1 decimal:
                Math.round(r.getProgressPercent() * 10.0) / 10.0
        );
        return ResponseEntity.ok(body);
    }
}
