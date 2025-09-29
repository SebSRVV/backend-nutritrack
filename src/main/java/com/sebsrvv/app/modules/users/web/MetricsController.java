// src/main/java/com/sebsrvv/app/modules/users/web/MetricsController.java
package com.sebsrvv.app.modules.users.web;

import com.sebsrvv.app.modules.users.application.MetricsService;
import com.sebsrvv.app.modules.users.application.UsersAnalyticsService;
import com.sebsrvv.app.modules.users.web.dto.FoodByCategoryRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;           // 👈 FALTA ESTE IMPORT
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

    /**
     * GET /api/metrics?dob=yyyy-MM-dd&height_cm=170&weight_kg=70
     * Público. (Aquí se asume service sincrónico como lo tenías)
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics(
            @RequestParam String dob,
            @RequestParam(name = "height_cm") Integer heightCm,
            @RequestParam(name = "weight_kg") Integer weightKg
    ) {
        Map<String, Object> data = metricsService.compute(dob, heightCm, weightKg);
        return ResponseEntity.ok(success(data));
    }

    /**
     * POST /api/users/{userId}/analytics/food-by-category
     * Requiere autenticación. Reenvía el Authorization recibido hacia Supabase.
     */
    @PostMapping("/users/{userId}/analytics/food-by-category")
    public Mono<ResponseEntity<Map<String, Object>>> foodByCategory(
            @PathVariable UUID userId,
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authHeader,
            @RequestBody @Valid FoodByCategoryRequest body
    ) {
        return analyticsService.foodByCategory(userId, body, authHeader)
                .map(data -> Map.of(
                        "ok", true,
                        "data", data,
                        "status", 200,
                        "timestamp", Instant.now().toString()
                ))
                .map(ResponseEntity::ok);
    }

    /* ---------- respuesta success uniforme ---------- */
    private Map<String, Object> success(Object data) {
        return Map.of(
                "ok", true,
                "data", data,
                "status", 200,
                "timestamp", Instant.now().toString()
        );
    }
}
