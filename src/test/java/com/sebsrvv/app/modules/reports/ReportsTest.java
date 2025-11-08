package com.sebsrvv.app.modules.reports;

import com.sebsrvv.app.modules.reports.application.ProgressAnalysisService;
import com.sebsrvv.app.modules.reports.application.ReportService;
import com.sebsrvv.app.modules.reports.infra.PdfReportGenerator;
import com.sebsrvv.app.modules.reports.web.ReportController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportController - Pruebas Unitarias")
class ReportsTest {

    @Mock
    private ProgressAnalysisService progressAnalysisService;

    @Mock
    private PdfReportGenerator pdfReportGenerator;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController controller;

    @Test
    @DisplayName("GET /api/reports/export/{userId} retorna 200 con PDF en el cuerpo")
    void exportReport_ReturnsPdfSuccessfully() {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        byte[] pdfBytes = "FAKE_PDF".getBytes();

        when(progressAnalysisService.getFullSummary(eq(userId)))
                .thenReturn(Map.of("summary", "ok"));

        when(pdfReportGenerator.generateNutritionReport(eq("Usuario"), any(Map.class)))
                .thenReturn(pdfBytes);

        // Act
        ResponseEntity<byte[]> response = controller.downloadReport(userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getHeaders().getContentType()).isNotNull();
        assertThat(response.getHeaders().getContentType().toString()).contains("application/pdf");
        assertThat(response.getHeaders().getContentDisposition().getFilename()).isEqualTo("informe_nutricional.pdf");
        assertThat(response.getBody()).isEqualTo(pdfBytes);

        verify(progressAnalysisService).getFullSummary(userId);
        verify(pdfReportGenerator).generateNutritionReport(eq("Usuario"), any(Map.class));
        verifyNoMoreInteractions(progressAnalysisService, pdfReportGenerator);
        verifyNoInteractions(reportService);
    }

    @Test
    @DisplayName("GET /api/reports/history/{userId} retorna lista vacía si no hay reportes")
    void getHistory_ReturnsEmptyList_WhenNoReports() {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        when(reportService.getReportsByUser(eq(userId))).thenReturn(List.of());

        // Act
        ResponseEntity<?> response = controller.getUserReportHistory(userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isInstanceOf(List.class);

        @SuppressWarnings("unchecked")
        List<?> body = (List<?>) response.getBody();
        assertThat(body).isEmpty();

        verify(reportService).getReportsByUser(userId);
        verifyNoMoreInteractions(reportService);
        verifyNoInteractions(progressAnalysisService, pdfReportGenerator);
    }

}
