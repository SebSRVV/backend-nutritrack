package com.sebsrvv.app.modules.users.web;

import com.sebsrvv.app.modules.users.application.NutritionReportService;
import com.sebsrvv.app.modules.users.domain.UserReport;
import com.sebsrvv.app.modules.users.web.dto.NutritionReportRequest;
import com.sebsrvv.app.modules.users.web.dto.NutritionReportResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/users/{userId}/reports")
public class ReportsController {

    private final NutritionReportService nutritionReportService;

    public ReportsController(NutritionReportService nutritionReportService) {
        this.nutritionReportService = nutritionReportService;
    }

    @PostMapping("/nutrition")
    public ResponseEntity<NutritionReportResponse> generateNutritionReport(
            @PathVariable String userId,
            @Valid @RequestBody NutritionReportRequest request
    ) {
        LocalDate from = LocalDate.parse(request.getFrom(), DateTimeFormatter.ISO_DATE);
        LocalDate to   = LocalDate.parse(request.getTo(),   DateTimeFormatter.ISO_DATE);

        var include = request.getInclude();
        UserReport report = nutritionReportService.generate(
                userId, from, to,
                include.isFoodByCategory(),
                include.isIntakeVsGoal(),
                include.isTrends(),
                include.isNotes()
        );

        NutritionReportResponse body = new NutritionReportResponse(
                report.getId().toString(),
                report.getName(),
                report.getType().wire(),
                report.getUrl(),
                report.getGeneratedAt().toString()
        );

        return ResponseEntity.ok(body);
    }
}
