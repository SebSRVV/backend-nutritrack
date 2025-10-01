package com.sebsrvv.app.modules.users.application;

import com.sebsrvv.app.modules.users.domain.ReportType;
import com.sebsrvv.app.modules.users.domain.UserReport;
import com.sebsrvv.app.modules.users.port.out.NutritionReportGeneratorPort;
import com.sebsrvv.app.modules.users.port.out.ReportStoragePort;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class NutritionReportService {

    private final NutritionReportGeneratorPort generator;
    private final ReportStoragePort storage;

    public NutritionReportService(NutritionReportGeneratorPort generator, ReportStoragePort storage) {
        this.generator = Objects.requireNonNull(generator);
        this.storage = Objects.requireNonNull(storage);
    }

    public UserReport generate(String userId,
                               LocalDate from,
                               LocalDate to,
                               boolean foodByCategory,
                               boolean intakeVsGoal,
                               boolean trends,
                               boolean notes) {

        String key = generator.generatePdf(userId, from, to, foodByCategory, intakeVsGoal, trends, notes);
        String publicUrl = storage.publish(key);

        String name = "Nutrition_Report_" +
                DateTimeFormatter.ISO_DATE.format(from) + "_to_" + DateTimeFormatter.ISO_DATE.format(to);

        return new UserReport(
                UUID.randomUUID(),
                name,
                ReportType.NUTRITION_PDF,
                publicUrl,
                Instant.now()
        );
    }
}
