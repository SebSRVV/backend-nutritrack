package com.sebsrvv.app.modules.goals.api;

import com.sebsrvv.app.modules.goals.dto.*;
import com.sebsrvv.app.modules.goals.service.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {
    private final GoalService service;

    @GetMapping
    public List<GoalView> list(@RequestHeader("X-User-Id") UUID userId){
        return service.list(userId);
    }

    @PostMapping
    public GoalView create(@RequestHeader("X-User-Id") UUID userId,
                           @Valid @RequestBody CreateGoalDto dto){
        return service.create(userId, dto);
    }

    @PatchMapping("/{id}/progress")
    public GoalView progress(@RequestHeader("X-User-Id") UUID userId,
                             @PathVariable("id") UUID goalId,
                             @Valid @RequestBody UpdateProgressDto dto){
        return service.updateProgress(userId, goalId, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("X-User-Id") UUID userId,
                       @PathVariable("id") UUID goalId){
        service.delete(userId, goalId);
    }
}
