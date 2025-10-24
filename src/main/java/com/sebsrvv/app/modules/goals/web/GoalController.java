package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.GoalService;
import com.sebsrvv.app.modules.goals.web.dto.GoalDto;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService service;
    public GoalController(GoalService service) { this.service = service; }

    private static String bearer(String authHeader){
        if (authHeader==null || !authHeader.startsWith("Bearer ")) return null;
        return authHeader.substring("Bearer ".length());
    }

    // GOALS
    @GetMapping
    public Mono<ResponseEntity<List<Map<String,Object>>>> list(@RequestHeader("Authorization") String auth){
        return service.list(bearer(auth)).map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<Map<String,Object>>> create(@RequestHeader("Authorization") String auth,
                                                           @RequestBody GoalDto dto){
        return service.create(dto, bearer(auth)).map(ResponseEntity::ok);
    }

    @PatchMapping("/{goalId}")
    public Mono<ResponseEntity<Map<String,Object>>> patch(@RequestHeader("Authorization") String auth,
                                                          @PathVariable String goalId,
                                                          @RequestBody GoalDto dto){
        return service.update(goalId, dto, bearer(auth)).map(ResponseEntity::ok);
    }

    @PutMapping("/{goalId}")
    public Mono<ResponseEntity<Map<String,Object>>> put(@RequestHeader("Authorization") String auth,
                                                        @PathVariable String goalId,
                                                        @RequestBody GoalDto dto){
        return service.update(goalId, dto, bearer(auth)).map(ResponseEntity::ok);
    }

    @DeleteMapping("/{goalId}")
    public Mono<ResponseEntity<Map<String,Object>>> delete(@RequestHeader("Authorization") String auth,
                                                           @PathVariable String goalId,
                                                           @RequestParam(name="mode", defaultValue = "hard") String mode){
        if ("soft".equalsIgnoreCase(mode)) {
            return service.softDelete(goalId, bearer(auth)).map(ResponseEntity::ok);
        }
        return service.hardDelete(goalId, bearer(auth))
                .map(code -> ResponseEntity.status(code).body(Map.of("status", code)));
    }

    // PROGRESS
    @GetMapping("/{goalId}/progress")
    public Mono<ResponseEntity<List<Map<String,Object>>>> listProgress(@RequestHeader("Authorization") String auth,
                                                                       @PathVariable String goalId,
                                                                       @RequestParam(name="from", required = false) String fromDate){
        return service.listProgress(goalId, fromDate, bearer(auth)).map(ResponseEntity::ok);
    }

    @PostMapping("/progress")
    public Mono<ResponseEntity<Map<String,Object>>> createProgress(@RequestHeader("Authorization") String auth,
                                                                   @RequestBody GoalProgressDto dto){
        return service.createProgress(dto, bearer(auth)).map(ResponseEntity::ok);
    }

    @PostMapping("/{goalId}/progress/upsert")
    public Mono<ResponseEntity<Map<String,Object>>> upsertProgress(@RequestHeader("Authorization") String auth,
                                                                   @PathVariable String goalId,
                                                                   @RequestParam String date,
                                                                   @RequestParam Integer value,
                                                                   @RequestParam(required=false) String note){
        return service.upsertProgress(goalId, date, value, note, bearer(auth)).map(ResponseEntity::ok);
    }
}
