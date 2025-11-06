package com.sebsrvv.app.modules.metrics.application;

import com.sebsrvv.app.modules.metrics.web.dto.MetricsQuery;
import com.sebsrvv.app.modules.metrics.web.dto.MetricsResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoUnit;

@Service
public class MetricsService {

    private static final ZoneId ZONE = ZoneId.of("America/Lima");

    public MetricsResponse calculate(MetricsQuery q) {
        // 1) BMI = peso(kg) / (altura(m)^2)
        double heightM = q.height_cm() / 100.0;
        double rawBmi = q.weight_kg() / (heightM * heightM);
        double bmi = round(rawBmi, 2);

        // 2) Age (puede ser negativo si dob está en el futuro, que es aceptado)
        LocalDate today = LocalDate.now(ZONE);
        int age = Period.between(q.dob(), today).getYears();

        // 3) Days to next birthday
        LocalDate nextBirthday = q.dob().withYear(today.getYear());
        if (!nextBirthday.isAfter(today)) {
            nextBirthday = nextBirthday.plusYears(1);
        }
        int daysToBirthday = (int) ChronoUnit.DAYS.between(today, nextBirthday);

        return new MetricsResponse(bmi, age, daysToBirthday);
    }

    private static double round(double value, int scale) {
        return BigDecimal.valueOf(value)
                .setScale(scale, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
