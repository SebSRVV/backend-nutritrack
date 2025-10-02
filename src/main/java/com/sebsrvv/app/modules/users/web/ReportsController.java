package com.sebsrvv.app.modules.users.web;

import com.sebsrvv.app.modules.users.application.NutritionReportService;
import com.sebsrvv.app.modules.users.domain.UserReport;
import com.sebsrvv.app.modules.users.web.dto.NutritionReportRequest;
import com.sebsrvv.app.modules.users.web.dto.NutritionReportResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/{userId}/reports")
public class ReportsController {

    private final NutritionReportService nutritionReportService;

    public ReportsController(NutritionReportService nutritionReportService) {
        this.nutritionReportService = nutritionReportService;
    }

    // POST que devuelve JSON (opcional, si lo sigues usando)
    @PostMapping("/nutrition")
    public ResponseEntity<NutritionReportResponse> generateNutritionReport(
            @PathVariable String userId,
            @Valid @RequestBody NutritionReportRequest request
    ) {
        var inc = request.getInclude();
        UserReport report = nutritionReportService.generate(
                userId, request.getFrom(), request.getTo(),
                inc.isFoodByCategory(), inc.isIntakeVsGoal(), inc.isTrends(), inc.isNotes()
        );

        var body = new NutritionReportResponse(
                report.getId().toString(),
                report.getName(),
                report.getType().wire(),
                report.getUrl(),
                report.getGeneratedAt().toString()
        );
        return ResponseEntity.ok(body);
    }

    // POST que genera + sube + DESCARGA directo
    @PostMapping(value = "/nutrition/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generateNutritionReportAndDownload(
            @PathVariable String userId,
            @Valid @RequestBody NutritionReportRequest request
    ) {
        var inc = request.getInclude();
        var generated = nutritionReportService.generateAndUpload(
                userId, request.getFrom(), request.getTo(),
                inc.isFoodByCategory(), inc.isIntakeVsGoal(), inc.isTrends(), inc.isNotes()
        );

        var report = generated.getReport();
        var pdf    = generated.getPdf();

        String fileName = "Nutrition_" + userId + "_" + request.getFrom() + "_to_" + request.getTo() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(pdf.length))
                .body(pdf);
    }
}
