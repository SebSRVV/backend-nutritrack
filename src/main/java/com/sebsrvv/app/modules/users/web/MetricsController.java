// src/main/java/com/sebsrvv/app/modules/users/web/MetricsController.java
package com.sebsrvv.app.modules.users.web;

import com.sebsrvv.app.modules.users.application.MetricsService;
import com.sebsrvv.app.modules.users.application.UsersAnalyticsService;
import com.sebsrvv.app.modules.users.web.dto.FoodByCategoryRequest;
import com.sebsrvv.app.modules.users.web.dto.IntakeVsGoalRequest;
import com.sebsrvv.app.modules.users.web.dto.IntakeVsGoalResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@Validated
public class MetricsController {

    private final MetricsService metricsService;
    private final UsersAnalyticsService analyticsService;

    public MetricsController(MetricsService metricsService, UsersAnalyticsService analyticsService) {
        this.metricsService = metricsService;
        this.analyticsService = analyticsService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics(
            @RequestParam String dob,
            @RequestParam(name = "height_cm") Integer heightCm,
            @RequestParam(name = "weight_kg") Integer weightKg
    ) {
        Map<String, Object> data = metricsService.compute(dob, heightCm, weightKg);
        return ResponseEntity.ok(success(data));
    }

    @GetMapping("/users/{userId}/analytics/food-by-category")
    public Mono<ResponseEntity<Map<String,Object>>> foodByCategoryGet(
            @PathVariable UUID userId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String groupBy
    ){
        var body = new FoodByCategoryRequest();
        body.setFrom(from); body.setTo(to); body.setGroupBy(groupBy);
        return analyticsService.foodByCategory(userId, body, authHeader)
                .map(data -> Map.of("ok", true, "data", data, "status", 200, "timestamp", Instant.now().toString()))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/users/{userId}/analytics/food-by-category")
    public Mono<ResponseEntity<Map<String, Object>>> foodByCategory(
            @PathVariable UUID userId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestBody @Valid FoodByCategoryRequest body
    ) {
        return analyticsService.foodByCategory(userId, body, authHeader)
                .map(data -> Map.of("ok", true, "data", data, "status", 200, "timestamp", Instant.now().toString()))
                .map(ResponseEntity::ok);
    }

    @GetMapping("/users/{userId}/analytics/intake-vs-goal")
    public Mono<ResponseEntity<Map<String, Object>>> intakeVsGoalGet(
            @PathVariable UUID userId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestParam String from,
            @RequestParam String to
    ) {
        var body = new com.sebsrvv.app.modules.users.web.dto.IntakeVsGoalRequest();
        body.setFrom(from);
        body.setTo(to);

        return analyticsService.intakeVsGoal(userId, body, authHeader)
                .map(data -> Map.of(
                        "ok", true,
                        "data", data,
                        "status", 200,
                        "timestamp", java.time.Instant.now().toString()
                ))
                .map(ResponseEntity::ok);
    }

    @PostMapping("/users/{userId}/analytics/intake-vs-goal")
    public Mono<ResponseEntity<Map<String, Object>>> intakeVsGoal(
            @PathVariable UUID userId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestBody @Valid IntakeVsGoalRequest body
    ) {
        return analyticsService.intakeVsGoal(userId, body, authHeader)
                .map((IntakeVsGoalResponse data) -> Map.of("ok", true, "data", data, "status", 200, "timestamp", Instant.now().toString()))
                .map(ResponseEntity::ok);
    }

    private Map<String, Object> success(Object data) {
        return Map.of(
                "ok", true,
                "data", data,
                "status", 200,
                "timestamp", Instant.now().toString()
        );
    }
}
