package com.sebsrvv.app.modules.reports.infra;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

@Component
public class PdfReportGenerator {

    public byte[] generateNutritionReport(String username, Map<String, Object> summary) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 60, 50);
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            PdfContentByte canvas = writer.getDirectContentUnder();
            Rectangle rect = new Rectangle(0, 0, PageSize.A4.getWidth(), PageSize.A4.getHeight());
            rect.setBackgroundColor(new Color(13, 17, 23)); // Fondo azul oscuro
            canvas.rectangle(rect);

            // ---------- Tipografías ----------
            com.lowagie.text.Font titleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 24, com.lowagie.text.Font.BOLD, new Color(0, 217, 126));  // Verde
            com.lowagie.text.Font subTitleFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 14, com.lowagie.text.Font.BOLD, new Color(59, 130, 246)); // Azul
            com.lowagie.text.Font textFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 12, com.lowagie.text.Font.NORMAL, new Color(234, 234, 234)); // Gris claro
            com.lowagie.text.Font smallFont = new com.lowagie.text.Font(com.lowagie.text.Font.HELVETICA, 10, com.lowagie.text.Font.ITALIC, new Color(200, 200, 200));

            // ---------- Encabezado ----------
            Paragraph title = new Paragraph("📘 NutriTrack - Informe Nutricional", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            Paragraph subtitle = new Paragraph("Usuario: " + username + "\nFecha: " + java.time.LocalDate.now(), smallFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(25);
            document.add(subtitle);

            // ---------- Sección 1: Consumo por categoría ----------
            Map<String, Object> consumption = (Map<String, Object>) summary.get("consumptionByCategory");
            Paragraph section1 = new Paragraph("Consumo por tipo de alimento", subTitleFont);
            section1.setSpacingBefore(10);
            section1.setSpacingAfter(10);
            document.add(section1);

            PdfPTable table1 = new PdfPTable(2);
            table1.setWidthPercentage(90);
            table1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.setSpacingAfter(20);

            PdfPCell header1 = new PdfPCell(new Phrase("Categoría", textFont));
            PdfPCell header2 = new PdfPCell(new Phrase("Kcal", textFont));
            header1.setBackgroundColor(new Color(0, 217, 126));
            header2.setBackgroundColor(new Color(0, 217, 126));
            header1.setHorizontalAlignment(Element.ALIGN_CENTER);
            header2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table1.addCell(header1);
            table1.addCell(header2);

            for (Map.Entry<String, Object> entry : consumption.entrySet()) {
                PdfPCell cat = new PdfPCell(new Phrase(entry.getKey(), textFont));
                PdfPCell val = new PdfPCell(new Phrase(entry.getValue().toString(), textFont));
                cat.setBackgroundColor(new Color(20, 24, 30));
                val.setBackgroundColor(new Color(20, 24, 30));
                cat.setBorderColor(new Color(30, 30, 30));
                val.setBorderColor(new Color(30, 30, 30));
                table1.addCell(cat);
                table1.addCell(val);
            }

            document.add(table1);

            // ---------- Sección 2: Comparación con metas ----------
            Map<String, Object> goals = (Map<String, Object>) summary.get("goalsComparison");
            Paragraph section2 = new Paragraph("Comparación con tus metas", subTitleFont);
            section2.setSpacingBefore(10);
            section2.setSpacingAfter(10);
            document.add(section2);

            PdfPTable table2 = new PdfPTable(2);
            table2.setWidthPercentage(90);
            table2.setHorizontalAlignment(Element.ALIGN_CENTER);
            table2.setSpacingAfter(20);

            for (Map.Entry<String, Object> entry : goals.entrySet()) {
                PdfPCell key = new PdfPCell(new Phrase(entry.getKey(), textFont));
                PdfPCell val = new PdfPCell(new Phrase(entry.getValue().toString(), textFont));
                key.setBackgroundColor(new Color(20, 24, 30));
                val.setBackgroundColor(new Color(20, 24, 30));
                key.setBorderColor(new Color(30, 30, 30));
                val.setBorderColor(new Color(30, 30, 30));
                table2.addCell(key);
                table2.addCell(val);
            }

            document.add(table2);

            // ---------- Sección 3: Tendencias ----------
            Paragraph section3 = new Paragraph("Tendencia de progreso (últimos días)", subTitleFont);
            section3.setSpacingBefore(10);
            section3.setSpacingAfter(10);
            document.add(section3);

            PdfPTable trendTable = new PdfPTable(2);
            trendTable.setWidthPercentage(90);
            trendTable.setHorizontalAlignment(Element.ALIGN_CENTER);

            trendTable.addCell(createHeaderCell("Fecha", textFont));
            trendTable.addCell(createHeaderCell("Calorías", textFont));

            Map<String, Object> trends = (Map<String, Object>) summary.get("trends");
            List<Map<String, Object>> data = (List<Map<String, Object>>) trends.get("data");
            for (Map<String, Object> d : data) {
                trendTable.addCell(createBodyCell(d.get("date").toString(), textFont));
                trendTable.addCell(createBodyCell(d.get("calories").toString(), textFont));
            }

            document.add(trendTable);

            document.add(Chunk.NEWLINE);
            Paragraph footer = new Paragraph("Generado automáticamente por NutriTrack © 2025", smallFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            document.add(footer);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private PdfPCell createHeaderCell(String text, com.lowagie.text.Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(0, 217, 126));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

    private PdfPCell createBodyCell(String text, com.lowagie.text.Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(20, 24, 30));
        cell.setBorderColor(new Color(30, 30, 30));
        return cell;
    }
}
