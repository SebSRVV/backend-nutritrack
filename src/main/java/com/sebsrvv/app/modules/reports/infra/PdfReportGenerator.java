package com.sebsrvv.app.modules.reports.infra;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;
import java.io.ByteArrayOutputStream;
import java.util.Map;

@Component
public class PdfReportGenerator {

    public byte[] generateNutritionReport(String username, Map<String, Object> summary) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12);

            document.add(new Paragraph("Informe Nutricional", titleFont));
            document.add(new Paragraph("Usuario: " + username, normalFont));
            document.add(new Paragraph("Fecha: " + java.time.LocalDate.now(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Resumen de Consumo", sectionFont));
            Map<String, Object> consumption = (Map<String, Object>) summary.get("consumptionByCategory");
            for (Map.Entry<String, Object> entry : consumption.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue() + " kcal", normalFont));
            }
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Comparación con Metas", sectionFont));
            Map<String, Object> goals = (Map<String, Object>) summary.get("goalsComparison");
            for (Map.Entry<String, Object> entry : goals.entrySet()) {
                document.add(new Paragraph(entry.getKey() + ": " + entry.getValue(), normalFont));
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }
}
