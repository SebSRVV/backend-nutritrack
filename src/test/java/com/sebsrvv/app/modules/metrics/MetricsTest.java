// src/test/java/com/sebsrvv/app/modules/metrics/MetricsTest.java
package com.sebsrvv.app.modules.metrics;

import com.sebsrvv.app.modules.metrics.application.MetricsService;
import com.sebsrvv.app.modules.metrics.web.MetricsController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MetricsController - Pruebas Unitarias")
class MetricsTest {

    @Mock
    private MetricsService metricsService;

    @InjectMocks
    private MetricsController controller;

    @Test
    @DisplayName("GET /api/metrics retorna 200 y el cuerpo dentro de 'data'")
    void getMetrics_ReturnsOk_WithWrappedBody() {
        // Arrange
        String dob = "1990-01-15";
        Integer height = 180;
        Integer weight = 75;

        Map<String, Object> mockData = Map.of(
                "bmi", 23.1,
                "age", 35,
                "daysToBirthday", 70
        );

        when(metricsService.compute(dob, height, weight)).thenReturn(mockData);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getMetrics(dob, height, weight);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body.get("status")).isEqualTo("success");
        assertThat(body.get("timestamp")).isNotNull(); // no validamos valor exacto

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertThat(data)
                .containsEntry("bmi", 23.1)
                .containsEntry("age", 35)
                .containsEntry("daysToBirthday", 70);

        verify(metricsService).compute(dob, height, weight);
        verifyNoMoreInteractions(metricsService);
    }
}
