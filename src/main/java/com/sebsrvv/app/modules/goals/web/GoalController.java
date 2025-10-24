package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.GoalService;
import com.sebsrvv.app.modules.goals.web.dto.GoalDto;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressDto;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/goals", produces = MediaType.APPLICATION_JSON_VALUE)
public class GoalController {

    private final GoalService service;

    public GoalController(GoalService service) {
        this.service = service;
    }

    /* ===== GOALS ===== */

    @GetMapping
    public Mono<ResponseEntity<List<Map<String,Object>>>> list(
            @RequestHeader("Authorization") String bearer) {
        return service.list(bearer)
                .map(ResponseEntity::ok);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String,Object>>> create(
            @RequestBody GoalDto dto,
            @RequestHeader("Authorization") String bearer) {
        return service.create(dto, bearer)
                .map(ResponseEntity::ok);
    }

    @PutMapping(path = "/{goalId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String,Object>>> updatePut(
            @PathVariable String goalId,
            @RequestBody GoalDto dto,
            @RequestHeader("Authorization") String bearer) {
        return service.update(goalId, dto, bearer).map(ResponseEntity::ok);
    }

    @PatchMapping(path = "/{goalId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String,Object>>> updatePatch(
            @PathVariable String goalId,
            @RequestBody GoalDto dto,
            @RequestHeader("Authorization") String bearer) {
        return service.update(goalId, dto, bearer).map(ResponseEntity::ok);
    }

    /** DELETE: soft/hard usando query param ?mode=soft */
    @DeleteMapping(path = "/{goalId}")
    public Mono<ResponseEntity<Map<String, Integer>>> delete(
            @PathVariable String goalId,
            @RequestParam(name = "mode", required = false) String mode,
            @RequestHeader("Authorization") String bearer) {
        boolean soft = "soft".equalsIgnoreCase(mode);
        return service.delete(goalId, soft, bearer)
                .map(ResponseEntity::ok);
    }

    /* ===== PROGRESS ===== */

    @GetMapping("/{goalId}/progress")
    public Mono<ResponseEntity<List<Map<String,Object>>>> listProgress(
            @PathVariable String goalId,
            @RequestParam(name = "from", required = false) String fromDate,
            @RequestHeader("Authorization") String bearer) {
        return service.listProgress(goalId, fromDate, bearer).map(ResponseEntity::ok);
    }

    @PostMapping(path = "/{goalId}/progress", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String,Object>>> createProgress(
            @PathVariable String goalId,
            @RequestBody GoalProgressDto body,
            @RequestHeader("Authorization") String bearer) {
        // Forzamos goalId del path si no viene en body
        GoalProgressDto dto = new GoalProgressDto(
                goalId,
                body.log_date(),
                body.value(),
                body.note()
        );
        return service.createProgress(dto, bearer).map(ResponseEntity::ok);
    }

    /** Upsert por RPC (opcional) */
    @PutMapping(path = "/{goalId}/progress", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Map<String,Object>>> upsertProgress(
            @PathVariable String goalId,
            @RequestBody GoalProgressDto body,
            @RequestHeader("Authorization") String bearer) {
        return service.upsertProgress(goalId, body.log_date(), body.value(), body.note(), bearer)
                .map(ResponseEntity::ok);
    }

    /* ===== STATS ===== */

    @GetMapping("/weekly-stats")
    public Mono<ResponseEntity<List<Map<String,Object>>>> weeklyStats(
            @RequestHeader("Authorization") String bearer) {
        return service.weeklyStats(bearer).map(ResponseEntity::ok);
    }
}
