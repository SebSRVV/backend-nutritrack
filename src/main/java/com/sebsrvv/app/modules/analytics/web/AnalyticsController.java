// src/main/java/com/sebsrvv/app/modules/analytics/web/AnalyticsController.java
package com.sebsrvv.app.modules.analytics.web;

import com.sebsrvv.app.modules.analytics.web.dto.TrendsRequest;
import com.sebsrvv.app.modules.analytics.web.dto.TrendsResponse;
import com.sebsrvv.app.modules.analytics.application.AnalyticsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/users/{userId}/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/trends")
    public ResponseEntity<TrendsResponse> trends(
            @PathVariable String userId,
            @RequestHeader("Authorization") String authorization,
            @Valid TrendsRequest req // ?period=&metric=&from=&to=&movingAvg=
    ) {
        LocalDate from = req.getFrom() == null ? null : LocalDate.parse(req.getFrom());
        LocalDate to   = req.getTo()   == null ? null : LocalDate.parse(req.getTo());

        TrendsResponse body = analyticsService.getTrends(
                userId,
                authorization,
                req.getPeriod(),
                req.getMetric(),
                from,
                to,
                req.getMovingAvg()
        );
        return ResponseEntity.ok(body);
    }
}
