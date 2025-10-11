// src/main/java/com/sebsrvv/app/modules/users/infra/pdf/ThymeleafNutritionReportGenerator.java
package com.sebsrvv.app.modules.users.infra.pdf;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.sebsrvv.app.modules.users.port.out.NutritionReportGeneratorPort;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Component
public class ThymeleafNutritionReportGenerator implements NutritionReportGeneratorPort {

    private final TemplateEngine engine; // Spring lo autoconfigura (starter-thymeleaf)

    public ThymeleafNutritionReportGenerator(TemplateEngine engine) {
        this.engine = engine;
    }

    @Override
    public byte[] generatePdf(String userId, LocalDate from, LocalDate to,
                              boolean foodByCategory, boolean intakeVsGoal,
                              boolean trends, boolean notes) {

        Context ctx = new Context();
        ctx.setVariable("userId", userId);
        ctx.setVariable("from", from.toString());
        ctx.setVariable("to", to.toString());
        ctx.setVariable("foodByCategory", foodByCategory);
        ctx.setVariable("intakeVsGoal", intakeVsGoal);
        ctx.setVariable("trends", trends);
        ctx.setVariable("notes", notes);
        // demo
        ctx.setVariable("summaryTotalKcal", 21500);
        ctx.setVariable("avgProtein", 95);

        String html = engine.process("nutrition-report", ctx); // resources/templates/nutrition-report.html

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating nutrition PDF", e);
        }
    }
}
