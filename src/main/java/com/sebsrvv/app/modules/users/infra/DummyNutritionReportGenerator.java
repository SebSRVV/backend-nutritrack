package com.sebsrvv.app.modules.users.infra;

import com.sebsrvv.app.modules.users.port.out.NutritionReportGeneratorPort;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DummyNutritionReportGenerator implements NutritionReportGeneratorPort {
    @Override
    public String generatePdf(String userId, LocalDate from, LocalDate to,
                              boolean foodByCategory, boolean intakeVsGoal,
                              boolean trends, boolean notes) {
        // TODO: Reemplazar con iText/HTML->PDF/Flying-Saucer, etc.
        // Devolvemos una “clave” de almacenamiento simulada
        return "reports/" + userId + "/nutrition/" + from + "_" + to + ".pdf";
    }
}
