// src/main/java/com/sebsrvv/app/modules/users/web/MetricsController.java
package com.sebsrvv.app.modules.metrics.web;

import org.springframework.http.HttpHeaders;
import com.sebsrvv.app.modules.metrics.application.MetricsService;
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

    private Map<String, Object> success(Object data) {
        return Map.of(
                "status", "success",
                "timestamp", Instant.now().toString(),
                "data", data
        );
    }

    private final MetricsService metricsService;

    public MetricsController(MetricsService metricsService) {
        this.metricsService = metricsService;
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
}