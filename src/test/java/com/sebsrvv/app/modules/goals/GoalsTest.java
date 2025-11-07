package com.sebsrvv.app.modules.goals;

import com.sebsrvv.app.modules.goals.web.GoalController;
import com.sebsrvv.app.modules.goals.application.GoalService;
import com.sebsrvv.app.modules.goals.web.dto.GoalRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoalController - Pruebas Unitarias")
class GoalsTest {

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController controller;

    @Test
    @DisplayName("POST /api/goals crea la meta y retorna 201 con GoalResponse")
    void createGoal_Returns201_WithGoalResponse() {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000010");

        GoalRequest req = new GoalRequest();
        req.setGoal_name("Beber agua");
        req.setDescription("2L por día");
        req.setWeekly_target(7);
        req.setIs_active(true);
        req.setCategory_id(1);
        req.setValue_type("QUANTITATIVE");
        req.setUnit("ml");
        req.setStart_date(LocalDate.of(2025, 1, 1));
        req.setEnd_date(LocalDate.of(2025, 12, 31));
        req.setTarget_value(new BigDecimal("2000"));

        GoalResponse mockResp = new GoalResponse();
        mockResp.setId(UUID.fromString("00000000-0000-0000-0000-000000000011"));
        mockResp.setGoal_name(req.getGoal_name());
        mockResp.setDescription(req.getDescription());
        mockResp.setWeekly_target(req.getWeekly_target());
        mockResp.setIs_active(req.getIs_active());
        mockResp.setCategory_id(req.getCategory_id());
        mockResp.setValue_type(req.getValue_type());
        mockResp.setUnit(req.getUnit());
        mockResp.setStart_date(req.getStart_date());
        mockResp.setEnd_date(req.getEnd_date());
        mockResp.setTarget_value(req.getTarget_value());

        when(goalService.createGoal(any(GoalRequest.class), eq(userId))).thenReturn(mockResp);

        // Act
        ResponseEntity<GoalResponse> response = controller.create(userId, req);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(mockResp.getId());
        assertThat(response.getBody().getGoal_name()).isEqualTo("Beber agua");

        verify(goalService).createGoal(any(GoalRequest.class), eq(userId));
        verifyNoMoreInteractions(goalService);
    }

    @Test
    @DisplayName("GET /api/goals?userId=... retorna lista vacía cuando no hay metas")
    void listGoals_ReturnsEmptyList_WhenNoGoals() {
        // Arrange
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000012");
        when(goalService.listGoals(eq(userId))).thenReturn(List.of());

        // Act
        ResponseEntity<List<GoalResponse>> response = controller.list(userId);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();

        verify(goalService).listGoals(userId);
        verifyNoMoreInteractions(goalService);
    }

    @Test
    @DisplayName("PUT /api/goals/{goalId}?userId=... actualiza y retorna GoalResponse")
    void putGoal_ReturnsOk_WithUpdatedGoal() {
        // Arrange
        UUID goalId = UUID.fromString("00000000-0000-0000-0000-000000000020");
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000021");

        GoalRequest req = new GoalRequest();
        req.setGoal_name("Beber agua (editado)");
        req.setWeekly_target(6);

        GoalResponse updated = new GoalResponse();
        updated.setId(goalId);
        updated.setGoal_name(req.getGoal_name());
        updated.setWeekly_target(req.getWeekly_target());

        when(goalService.putOrPatchGoal(eq(goalId), any(GoalRequest.class), eq(userId)))
                .thenReturn(updated);

        // Act
        ResponseEntity<GoalResponse> response = controller.put(goalId, userId, req);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(goalId);
        assertThat(response.getBody().getGoal_name()).isEqualTo("Beber agua (editado)");
        assertThat(response.getBody().getWeekly_target()).isEqualTo(6);

        verify(goalService).putOrPatchGoal(eq(goalId), any(GoalRequest.class), eq(userId));
        verifyNoMoreInteractions(goalService);
    }

    @Test
    @DisplayName("DELETE /api/goals/{goalId}?mode=soft&userId=... realiza soft delete y retorna 200")
    void deleteGoal_Soft_ReturnsOk() {
        // Arrange
        UUID goalId = UUID.fromString("00000000-0000-0000-0000-000000000030");
        UUID userId = UUID.fromString("00000000-0000-0000-0000-000000000031");

        // Act
        ResponseEntity<?> response = controller.delete(goalId, userId, "soft");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(String.valueOf(response.getBody())).contains("\"status\":200");

        verify(goalService).softDelete(goalId, userId);
        verifyNoMoreInteractions(goalService);
    }
}
