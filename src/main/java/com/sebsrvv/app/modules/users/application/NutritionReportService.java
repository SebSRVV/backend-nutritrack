package com.sebsrvv.app.modules.users.application;

import com.sebsrvv.app.modules.users.domain.ReportType;
import com.sebsrvv.app.modules.users.domain.UserReport;
import com.sebsrvv.app.modules.users.port.out.NutritionReportGeneratorPort;
import com.sebsrvv.app.modules.users.port.out.ReportStoragePort;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class NutritionReportService {

    private final NutritionReportGeneratorPort generator;
    private final ReportStoragePort storage;

    public NutritionReportService(NutritionReportGeneratorPort generator, ReportStoragePort storage) {
        this.generator = generator;
        this.storage = storage;
    }

    public UserReport generate(String userId, LocalDate from, LocalDate to,
                               boolean foodByCategory, boolean intakeVsGoal,
                               boolean trends, boolean notes) {

        if (from.isAfter(to)) {
            throw new IllegalArgumentException("'from' must be <= 'to'");
        }

        byte[] pdf = generator.generatePdf(userId, from, to, foodByCategory, intakeVsGoal, trends, notes);

        String key = "reports/" + userId + "/nutrition/" +
                DateTimeFormatter.ISO_DATE.format(from) + "_to_" + DateTimeFormatter.ISO_DATE.format(to) + ".pdf";

        String url = storage.upload(key, pdf, "application/pdf");

        return new UserReport(
                UUID.randomUUID(),
                "Nutrition_Report_" + userId,
                ReportType.NUTRITION_PDF,
                url,
                Instant.now()
        );
    }
}
