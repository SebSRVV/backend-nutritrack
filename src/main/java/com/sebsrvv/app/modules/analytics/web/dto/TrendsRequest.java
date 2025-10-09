// src/main/java/com/sebsrvv/app/modules/analytics/web/dto/TrendsRequest.java
package com.sebsrvv.app.modules.analytics.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TrendsRequest {
    @NotBlank
    @Pattern(regexp = "weekly|monthly", message = "period debe ser 'weekly' o 'monthly'")
    private String period;

    @NotBlank
    @Pattern(regexp = "calories|protein_g|carbs_g|fat_g|goalAdherence",
            message = "metric inválido")
    private String metric;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "from debe ser YYYY-MM-DD")
    private String from;

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "to debe ser YYYY-MM-DD")
    private String to;

    @Min(1) @Max(30)
    private Integer movingAvg; // tamaño de la ventana (en buckets)
}
