// src/main/java/com/sebsrvv/app/modules/metrics/application/MetricsService.java
package com.sebsrvv.app.modules.metrics.application;

import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetricsService {

    // Limites
    public static final int MIN_HEIGHT_CM = 80;
    public static final int MAX_HEIGHT_CM = 230;
    public static final int MIN_WEIGHT_KG = 25;
    public static final int MAX_WEIGHT_KG = 250;

    public static final int MAX_AGE_YEARS = 90; // tope superior de edad

    public Map<String, Object> compute(String dob, Integer heightCm, Integer weightKg) {
        // --- validar height / weight ---
        if (heightCm == null || heightCm < MIN_HEIGHT_CM || heightCm > MAX_HEIGHT_CM) {
            throw new IllegalArgumentException(
                    "height_cm fuera de rango [" + MIN_HEIGHT_CM + ".." + MAX_HEIGHT_CM + "]");
        }
        if (weightKg == null || weightKg < MIN_WEIGHT_KG || weightKg > MAX_WEIGHT_KG) {
            throw new IllegalArgumentException(
                    "weight_kg fuera de rango [" + MIN_WEIGHT_KG + ".." + MAX_WEIGHT_KG + "]");
        }

        // --- validar/parsear dob ---
        if (dob == null || dob.isBlank()) {
            throw new IllegalArgumentException("Ingresa una fecha de nacimiento (formato yyyy-MM-dd).");
        }

        final LocalDate birth;
        try {
            birth = LocalDate.parse(dob); // ISO yyyy-MM-dd
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("El formato de la fecha de nacimiento es invalido. Usa formato yyyy-MM-dd.");
        }

        LocalDate today = LocalDate.now(ZoneOffset.UTC);

        if (birth.isAfter(today)) {
            throw new IllegalArgumentException("Cambia la fecha de nacimiento, no puede ser una fecha futura.");
        }

        int age = Period.between(birth, today).getYears();
        if (age < 0 || age > MAX_AGE_YEARS) {
            throw new IllegalArgumentException("La edad ingresada no es validad, solo esta permitido de 0 a " + MAX_AGE_YEARS + ").");
        }

        // --- BMI ---
        double bmi = round1(bmi(heightCm, weightKg));

        // --- días hasta el próximo cumpleaños ---
        int days = daysUntilNextBirthday(today, birth);

        Map<String, Object> out = new HashMap<>();
        out.put("bmi", bmi);
        out.put("age", age);
        out.put("daysToBirthday", days);
        return out;
    }

    /* ----------------- helpers ----------------- */

    private static double bmi(int heightCm, int weightKg) {
        double m = heightCm / 100.0;
        return weightKg / (m * m);
    }

    private static double round1(double v) {
        return Math.round(v * 10.0) / 10.0;
    }

    /** Días al próximo cumpleaños desde 'today'. */
    private static int daysUntilNextBirthday(LocalDate today, LocalDate dob) {
        LocalDate next = dob.withYear(today.getYear());
        if (!next.isAfter(today)) {
            next = next.plusYears(1);
        }
        return (int) Duration.between(today.atStartOfDay(), next.atStartOfDay()).toDays();
    }
}