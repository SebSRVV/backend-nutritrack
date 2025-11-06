package com.sebsrvv.app.modules.metrics.web;

import com.sebsrvv.app.modules.metrics.application.MetricsService;
import com.sebsrvv.app.modules.metrics.web.dto.MetricsQuery;
import com.sebsrvv.app.modules.metrics.web.dto.MetricsResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/metrics")
public class MetricsController {

    private final MetricsService service;

    public MetricsController(MetricsService service) {
        this.service = service;
    }

    // GET /api/metrics?dob=2067-12-12&height_cm=170&weight_kg=70
    @GetMapping
    public MetricsResponse calculate(@Valid @ModelAttribute MetricsQuery q) {
        return service.calculate(q);
    }
}
