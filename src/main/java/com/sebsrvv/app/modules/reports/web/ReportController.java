package com.sebsrvv.app.modules.reports.web;

import com.sebsrvv.app.modules.reports.application.ProgressAnalysisService;
import com.sebsrvv.app.modules.reports.application.ReportService;
import com.sebsrvv.app.modules.reports.infra.PdfReportGenerator;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ProgressAnalysisService analysisService;
    private final PdfReportGenerator pdfGenerator;
    private final ReportService reportService;

    public ReportController(ProgressAnalysisService analysisService,
                            PdfReportGenerator pdfGenerator,
                            ReportService reportService) {
        this.analysisService = analysisService;
        this.pdfGenerator = pdfGenerator;
        this.reportService = reportService;
    }

    @GetMapping("/consumption-by-category/{userId}")
    public ResponseEntity<Map<String, Object>> getConsumptionByCategory(@PathVariable UUID userId) {
        return ResponseEntity.ok(analysisService.getConsumptionByCategory(userId));
    }

    @GetMapping("/goals-comparison/{userId}")
    public ResponseEntity<Map<String, Object>> getGoalsComparison(@PathVariable UUID userId) {
        return ResponseEntity.ok(analysisService.getGoalsComparison(userId));
    }

    @GetMapping("/trends/{userId}")
    public ResponseEntity<Map<String, Object>> getTrends(@PathVariable UUID userId,
                                                         @RequestParam(defaultValue = "weekly") String period) {
        return ResponseEntity.ok(analysisService.getTrends(userId, period));
    }

    @GetMapping("/download/{userId}")
    public ResponseEntity<byte[]> downloadReport(@PathVariable UUID userId) {
        Map<String, Object> summary = analysisService.getFullSummary(userId);
        byte[] pdf = pdfGenerator.generateNutritionReport("Usuario", summary);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename("informe_nutricional.pdf").build());
        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getUserReportHistory(@PathVariable UUID userId) {
        return ResponseEntity.ok(reportService.getReportsByUser(userId));
    }
}
