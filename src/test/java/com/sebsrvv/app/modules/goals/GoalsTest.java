package com.sebsrvv.app.modules.goals;

import com.sebsrvv.app.modules.goals.web.GoalController;
import com.sebsrvv.app.modules.goals.application.GoalService;
import com.sebsrvv.app.modules.goals.dto.GoalRequest;
import com.sebsrvv.app.modules.goals.dto.GoalResponse;
import com.sebsrvv.app.modules.goals.dto.GoalProgressRequest;
import com.sebsrvv.app.modules.goals.dto.GoalProgressResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;


import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Ajusta los paquetes de dto si en tu proyecto están en otro namespace
 * (por ejemplo com.sebsrvv.app.modules.reports...); aquí usé .modules.goals.dto
 * para mayor claridad.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GoalController - Pruebas Unitarias")
class GoalsTest {

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController controller;

    @Test
    @DisplayName("POST /api/goals crea la meta y la retorna dentro de 'data'")
    void createGoal_ReturnsOk_WithWrappedBody() {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000010");

        GoalRequest request = new GoalRequest();
        request.setUserId(userId);
        request.setCalories(2200);
        request.setProtein(150);
        request.setCarbs(250);
        request.setFat(70);

        GoalResponse mockResp = new GoalResponse();
        mockResp.setId(UUID.fromString("00000000-0000-0000-0000-000000000011"));
        mockResp.setUserId(userId);
        mockResp.setCalories(2200);
        mockResp.setProtein(150);
        mockResp.setCarbs(250);
        mockResp.setFat(70);

        when(goalService.create(any(GoalRequest.class))).thenReturn(mockResp);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.createGoal(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body.get("status")).isEqualTo("success");
        assertThat(body.get("timestamp")).isNotNull();

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertThat(data)
                .containsEntry("userId", userId.toString())
                .containsEntry("calories", 2200)
                .containsEntry("protein", 150)
                .containsEntry("carbs", 250)
                .containsEntry("fat", 70);

        verify(goalService).create(any(GoalRequest.class));
        verifyNoMoreInteractions(goalService);
    }

    @Test
    @DisplayName("POST /api/goals/progress retorna progreso dentro de 'data'")
    void getProgress_ReturnsOk_WithWrappedBody() {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000012");

        GoalProgressRequest request = new GoalProgressRequest();
        request.setUserId(userId);
        // Si tu request tiene más campos (fecha rango, etc.), asígnalos aquí

        GoalProgressResponse progress = new GoalProgressResponse();
        // Ajusta los setters según tu DTO real:
        // p.ej. progress.setCaloriesPercent(0.82), setProteinGramsRemaining(20), etc.
        progress.setCaloriesConsumed(1800);
        progress.setCaloriesGoal(2200);
        progress.setProteinConsumed(120);
        progress.setProteinGoal(150);
        progress.setCarbsConsumed(210);
        progress.setCarbsGoal(250);
        progress.setFatConsumed(60);
        progress.setFatGoal(70);

        when(goalService.getProgress(any(GoalProgressRequest.class))).thenReturn(progress);

        // Act
        ResponseEntity<Map<String, Object>> response = controller.getProgress(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();

        Map<String, Object> body = response.getBody();
        assertThat(body.get("status")).isEqualTo("success");
        assertThat(body.get("timestamp")).isNotNull();

        @SuppressWarnings("unchecked")
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertThat(data)
                .containsEntry("caloriesConsumed", 1800)
                .containsEntry("caloriesGoal", 2200)
                .containsEntry("proteinConsumed", 120)
                .containsEntry("proteinGoal", 150)
                .containsEntry("carbsConsumed", 210)
                .containsEntry("carbsGoal", 250)
                .containsEntry("fatConsumed", 60)
                .containsEntry("fatGoal", 70);

        verify(goalService).getProgress(any(GoalProgressRequest.class));
        verifyNoMoreInteractions(goalService);
    }
}
