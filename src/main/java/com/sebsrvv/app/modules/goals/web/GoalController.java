// src/main/java/com/sebsrvv/app/modules/goals/web/GoalController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.GoalService;
import com.sebsrvv.app.modules.goals.web.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;
    public GoalController(GoalService goalService) { this.goalService = goalService; }

    //@GetMapping
    //public ResponseEntity<List<GoalResponse>> list() {
        //return ResponseEntity.ok(goalService.listGoals());
    //}

    @PostMapping("/crear/{userid}")
    public ResponseEntity<GoalResponse> create(@RequestBody GoalRequest body, @PathVariable UUID userid) {
        return ResponseEntity.ok(goalService.createGoal(body,userid));
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<GoalResponse> update(@PathVariable UUID id, @RequestBody GoalRequest body) {
        return ResponseEntity.ok(goalService.updateGoal(body,id));
    }
}
