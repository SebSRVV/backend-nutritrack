// src/main/java/com/sebsrvv/app/modules/users/web/MetricsController.java
package com.sebsrvv.app.modules.users.web;

import com.sebsrvv.app.modules.users.application.MetricsService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@Validated
public class MetricsController {

    private final MetricsService service;

    public MetricsController(MetricsService service) {
        this.service = service;
    }

    /**
     * GET /api/metrics?dob=yyyy-MM-dd&height_cm=170&weight_kg=70
     * Público (asegúrate de tenerlo permitido en SecurityConfig).
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMetrics(
            @RequestParam String dob,
            @RequestParam(name = "height_cm") Integer heightCm,
            @RequestParam(name = "weight_kg") Integer weightKg
    ) {
        Map<String, Object> data = service.compute(dob, heightCm, weightKg);
        return ResponseEntity.ok(success(data));
    }

    /* ---------- respuesta success uniforme (como tu screenshot) ---------- */
    private Map<String, Object> success(Object data) {
        return Map.of(
                "ok", true,
                "data", data,
                "status", 200,
                "timestamp", Instant.now().toString()
        );
    }
}
