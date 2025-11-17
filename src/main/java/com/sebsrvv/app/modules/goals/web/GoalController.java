// src/main/java/com/sebsrvv/app/modules/goals/web/GoalController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.GoalService;
import com.sebsrvv.app.modules.goals.web.dto.GoalRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    public GoalController(GoalService goalService) { this.goalService = goalService; }

    // GET /api/goals?userId=...
    @GetMapping
    public ResponseEntity<List<GoalResponse>> list(@RequestParam UUID userId) {
        return ResponseEntity.ok(goalService.listGoals(userId));
    }

    // POST /api/goals?userId=...
    @PostMapping
    public ResponseEntity<GoalResponse> create(@RequestParam UUID userId,
                                               @Valid @RequestBody GoalRequest body) { // <-- Validación Activada
        GoalResponse out = goalService.createGoal(body, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(out);
    }

    // PUT/PATCH /api/goals/{goalId}?userId=...
    @PutMapping("/{goalId}")
    public ResponseEntity<GoalResponse> put(@PathVariable UUID goalId,
                                            @RequestParam UUID userId,
                                            @Valid @RequestBody GoalRequest body) { // <-- Validación Activada
        return ResponseEntity.ok(goalService.putOrPatchGoal(goalId, body, userId));
    }

    @PatchMapping("/{goalId}")
    public ResponseEntity<GoalResponse> patch(@PathVariable UUID goalId,
                                              @RequestParam UUID userId,
                                              @Valid @RequestBody GoalRequest body) { // <-- Validación Activada
        return ResponseEntity.ok(goalService.putOrPatchGoal(goalId, body, userId));
    }

    // DELETE soft: /api/goals/{goalId}?mode=soft&userId=...
    @DeleteMapping("/{goalId}")
    public ResponseEntity<?> delete(@PathVariable UUID goalId,
                                    @RequestParam UUID userId,
                                    @RequestParam(required = false, defaultValue = "soft") String mode) {
        if ("soft".equalsIgnoreCase(mode)) {
            goalService.softDelete(goalId, userId);
            return ResponseEntity.ok().body("{\"status\":200}");
        } else {
            goalService.hardDelete(goalId, userId);
            return ResponseEntity.noContent().build();
        }
    }
}