package com.sebsrvv.app.modules.reports.application;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProgressAnalysisService {

    public Map<String, Object> getConsumptionByCategory(UUID userId) {
        return Map.of(
                "Proteínas", 1200,
                "Carbohidratos", 1800,
                "Grasas", 800
        );
    }

    public Map<String, Object> getGoalsComparison(UUID userId) {
        return Map.of(
                "caloriesTarget", 2200,
                "caloriesConsumed", 2100,
                "proteinTargetG", 150,
                "proteinConsumedG", 140,
                "goalProgressPercent", 95
        );
    }

    public Map<String, Object> getTrends(UUID userId, String period) {
        List<Map<String, Object>> data = List.of(
                Map.of("date", "2025-10-01", "calories", 2000),
                Map.of("date", "2025-10-02", "calories", 2100),
                Map.of("date", "2025-10-03", "calories", 1950),
                Map.of("date", "2025-10-04", "calories", 2200)
        );
        return Map.of("period", period, "data", data);
    }

    public Map<String, Object> getFullSummary(UUID userId) {
        return Map.of(
                "consumptionByCategory", getConsumptionByCategory(userId),
                "goalsComparison", getGoalsComparison(userId),
                "trends", getTrends(userId, "weekly")
        );
    }
}
