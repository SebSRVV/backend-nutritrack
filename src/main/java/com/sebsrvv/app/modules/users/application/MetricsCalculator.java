// src/main/java/com/sebsrvv/app/modules/users/application/MetricsCalculator.java
package com.sebsrvv.app.modules.users.application;

import java.time.*;

public final class MetricsCalculator {

    private MetricsCalculator() {}

    public static Double bmi(Integer heightCm, Integer weightKg) {
        if (heightCm == null || weightKg == null || heightCm <= 0) return null;
        double m = heightCm / 100.0;
        double raw = weightKg / (m * m);
        return Math.round(raw * 10.0) / 10.0; // 1 decimal
    }

    public static Integer ageFromDob(String dobIso) {
        try {
            LocalDate dob = LocalDate.parse(dobIso);
            return Period.between(dob, LocalDate.now()).getYears();
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer daysUntilBirthday(String dobIso) {
        try {
            LocalDate dob = LocalDate.parse(dobIso);
            LocalDate today = LocalDate.now();
            LocalDate thisYear = LocalDate.of(today.getYear(), dob.getMonth(), dob.getDayOfMonth());
            LocalDate next = thisYear.isBefore(today) ? thisYear.plusYears(1) : thisYear;
            return (int) Duration.between(today.atStartOfDay(), next.atStartOfDay()).toDays();
        } catch (Exception e) {
            return null;
        }
    }
}
