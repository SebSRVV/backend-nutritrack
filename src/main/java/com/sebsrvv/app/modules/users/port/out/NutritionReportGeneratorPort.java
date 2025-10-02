package com.sebsrvv.app.modules.users.port.out;

import java.time.LocalDate;

public interface NutritionReportGeneratorPort {
    byte[] generatePdf(String userId,
                       LocalDate from,
                       LocalDate to,
                       boolean foodByCategory,
                       boolean intakeVsGoal,
                       boolean trends,
                       boolean notes);
}
