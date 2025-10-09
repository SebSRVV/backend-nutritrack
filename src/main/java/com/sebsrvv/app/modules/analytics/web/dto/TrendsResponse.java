// src/main/java/com/sebsrvv/app/modules/analytics/web/dto/TrendsResponse.java
package com.sebsrvv.app.modules.analytics.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class TrendsResponse {

    @Data @NoArgsConstructor @AllArgsConstructor
    public static class Point {
        private String bucket; // YYYY-MM-DD (inicio de semana/mes)
        private Double value;
    }

    private String period;   // weekly | monthly
    private String metric;   // calories | protein_g | carbs_g | fat_g | goalAdherence
    private List<Point> series;
    private List<Point> movingAverage; // puede venir vacío si movingAvg == null
}
