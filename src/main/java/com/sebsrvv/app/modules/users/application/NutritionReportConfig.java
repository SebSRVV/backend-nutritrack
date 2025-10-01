package com.sebsrvv.app.modules.users.application;

import com.sebsrvv.app.modules.users.port.out.NutritionReportGeneratorPort;
import com.sebsrvv.app.modules.users.port.out.ReportStoragePort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NutritionReportConfig {

    @Bean
    public NutritionReportService nutritionReportService(
            NutritionReportGeneratorPort generatorPort,
            ReportStoragePort storagePort
    ) {
        return new NutritionReportService(generatorPort, storagePort);
    }
}
