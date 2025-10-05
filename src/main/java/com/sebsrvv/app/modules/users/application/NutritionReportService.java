package com.sebsrvv.app.modules.users.application;

import com.sebsrvv.app.modules.users.domain.ReportType;
import com.sebsrvv.app.modules.users.domain.UserReport;
import com.sebsrvv.app.modules.users.port.out.NutritionReportGeneratorPort;
import com.sebsrvv.app.modules.users.port.out.ReportStoragePort;
import com.sebsrvv.app.modules.users.port.out.UserReportRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class NutritionReportService {

    private final NutritionReportGeneratorPort generator;
    private final ReportStoragePort storage;
    private final UserReportRepository reports;

    public NutritionReportService(NutritionReportGeneratorPort generator,
                                  ReportStoragePort storage,
                                  UserReportRepository reports) {
        this.generator = generator;
        this.storage = storage;
        this.reports = reports;
    }

    /** Método que ya usas para el POST JSON */
    public UserReport generate(String userId, LocalDate from, LocalDate to,
                               boolean foodByCategory, boolean intakeVsGoal,
                               boolean trends, boolean notes) {
        GeneratedReport gr = generateAndUpload(userId, from, to, foodByCategory, intakeVsGoal, trends, notes);
        return gr.getReport();
    }

    /** Nuevo: genera bytes, sube a Supabase y retorna (reporte + bytes) para poder hacer streaming */
    public GeneratedReport generateAndUpload(String userId, LocalDate from, LocalDate to,
                                             boolean foodByCategory, boolean intakeVsGoal,
                                             boolean trends, boolean notes) {

        if (from.isAfter(to)) throw new IllegalArgumentException("'from' must be <= 'to'");

        // 1) Generar bytes del PDF
        byte[] pdf = generator.generatePdf(userId, from, to, foodByCategory, intakeVsGoal, trends, notes);

        // 2) Subir (IMPORTANTE: pathKey sin nombre del bucket)
        String file = DateTimeFormatter.ISO_DATE.format(from) + "_to_" + DateTimeFormatter.ISO_DATE.format(to) + ".pdf";
        String key  = userId + "/nutrition/" + file;
        String url  = storage.upload(key, pdf, "application/pdf");

        // 3) Construir y guardar el UserReport
        UserReport report = new UserReport(
                UUID.randomUUID(),
                "Nutrition_Report_" + userId,
                ReportType.NUTRITION_PDF,
                url,
                Instant.now()
        );
        reports.save(report);

        return new GeneratedReport(report, pdf);
    }
}
