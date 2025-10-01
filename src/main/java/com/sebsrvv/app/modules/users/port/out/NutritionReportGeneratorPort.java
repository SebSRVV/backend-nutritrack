package com.sebsrvv.app.modules.users.port.out;

import java.time.LocalDate;

public interface NutritionReportGeneratorPort {
    /**
     * Genera el PDF con los datos de nutrición y devuelve una referencia
     * @return storageKey o URL directa, según la implementación
     */
    String generatePdf(String userId,
                       LocalDate from,
                       LocalDate to,
                       boolean foodByCategory,
                       boolean intakeVsGoal,
                       boolean trends,
                       boolean notes);
}
