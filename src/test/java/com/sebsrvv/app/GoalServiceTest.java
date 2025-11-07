package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.Goal;
import com.sebsrvv.app.modules.goals.domain.GoalRepository;
import com.sebsrvv.app.modules.goals.web.dto.GoalRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoalService - Unit Tests (éxitos + errores 400/401/404/500/502)")
class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @InjectMocks
    private GoalService goalService;

    private UUID USER_ID;
    private UUID GOAL_ID;

    @BeforeEach
    void init() {
        USER_ID = UUID.randomUUID();
        GOAL_ID = UUID.randomUUID();
    }

    // ===== Helpers =====
    private Goal mkGoal(UUID id, UUID userId, String name, boolean active) {
        Goal g = new Goal();
        g.setId(id);
        g.setUserId(userId);
        g.setGoalName(name);
        g.setDescription("desc");
        g.setWeeklyTarget(3);
        g.setIsActive(active);
        g.setCategoryId(1);
        g.setValueType("BOOLEAN");
        g.setUnit("bool");
        g.setStartDate(LocalDate.of(2025, 1, 1));
        g.setEndDate(LocalDate.of(2025, 12, 31));
        g.setTargetValue(new BigDecimal("1"));
        return g;
    }

    private GoalRequest mkRequestOk() {
        GoalRequest r = new GoalRequest();
        r.setGoal_name("Drink water");
        r.setDescription("8 glasses");
        r.setWeekly_target(5);
        r.setIs_active(true);
        r.setCategory_id(1);
        r.setValue_type("QUANTITATIVE");
        r.setUnit("ml");
        r.setStart_date(LocalDate.of(2025, 1, 1));
        r.setEnd_date(LocalDate.of(2025, 12, 31));
        r.setTarget_value(new BigDecimal("2000"));
        return r;
    }

    // =========================================================
    // GET /api/goals
    // =========================================================
    @Nested
    @DisplayName("listGoals (GET /api/goals)")
    class ListGoals {

        @Test
        @DisplayName("200 - Lista metas del usuario")
        void ok_list() {
            Goal g1 = mkGoal(UUID.randomUUID(), USER_ID, "A", true);
            Goal g2 = mkGoal(UUID.randomUUID(), USER_ID, "B", false);
            when(goalRepository.findByUserIdOrderByCreatedAtDesc(USER_ID))
                    .thenReturn(List.of(g1, g2));

            var out = goalService.listGoals(USER_ID);

            assertThat(out).hasSize(2);
            assertThat(out.get(0)).isInstanceOf(GoalResponse.class);
            assertThat(out.get(0).getGoal_name()).isEqualTo("A");
            verify(goalRepository).findByUserIdOrderByCreatedAtDesc(USER_ID);
        }

        @Test
        @DisplayName("401 - userId nulo => SecurityException (Token inválido)")
        void unauthorized_when_userId_null() {
            assertThatThrownBy(() -> goalService.listGoals(null))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Token inválido o sesión expirada");
            verifyNoInteractions(goalRepository);
        }

        @Test
        @DisplayName("502 - falla de acceso a datos => RuntimeException (listar)")
        void bad_gateway_on_repository_error() {
            when(goalRepository.findByUserIdOrderByCreatedAtDesc(USER_ID))
                    .thenThrow(new RuntimeException("DB down"));

            assertThatThrownBy(() -> goalService.listGoals(USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error al listar las metas. (502 Bad Gateway)");
        }
    }

    // =========================================================
    // POST /api/goals
    // =========================================================
    @Nested
    @DisplayName("createGoal (POST /api/goals)")
    class CreateGoal {

        @Test
        @DisplayName("200 - Crea meta correctamente")
        void ok_create() {
            GoalRequest r = mkRequestOk();
            Goal toSave = mkGoal(null, USER_ID, r.getGoal_name(), true);
            Goal saved = mkGoal(GOAL_ID, USER_ID, r.getGoal_name(), true);

            when(goalRepository.save(any(Goal.class))).thenReturn(saved);

            GoalResponse resp = goalService.createGoal(r, USER_ID);

            assertThat(resp).isNotNull();
            assertThat(resp.getId()).isEqualTo(GOAL_ID);
            assertThat(resp.getGoal_name()).isEqualTo("Drink water");
            verify(goalRepository).save(any(Goal.class));
        }

        @Test
        @DisplayName("400 - Body vacío => IllegalArgumentException")
        void bad_request_empty_body() {
            assertThatThrownBy(() -> goalService.createGoal(null, USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Body vacío");
            verifyNoInteractions(goalRepository);
        }

        @Test
        @DisplayName("400 - weekly_target fuera de rango [1..7]")
        void bad_request_weekly_target_range() {
            GoalRequest r = mkRequestOk();
            r.setWeekly_target(0);

            assertThatThrownBy(() -> goalService.createGoal(r, USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weekly_target debe estar entre 1 y 7");
            verifyNoInteractions(goalRepository);
        }

        @Test
        @DisplayName("400 - end_date < start_date")
        void bad_request_date_range() {
            GoalRequest r = mkRequestOk();
            r.setStart_date(LocalDate.of(2025, 5, 1));
            r.setEnd_date(LocalDate.of(2025, 4, 30));

            assertThatThrownBy(() -> goalService.createGoal(r, USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("end_date no puede ser anterior a start_date");
            verifyNoInteractions(goalRepository);
        }

        @Test
        @DisplayName("401 - userId nulo => SecurityException")
        void unauthorized_create() {
            GoalRequest r = mkRequestOk();
            assertThatThrownBy(() -> goalService.createGoal(r, null))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Token inválido o sesión expirada");
            verifyNoInteractions(goalRepository);
        }

        @Test
        @DisplayName("502 - error persistiendo => RuntimeException (registrar)")
        void bad_gateway_on_save_error() {
            GoalRequest r = mkRequestOk();
            when(goalRepository.save(any(Goal.class))).thenThrow(new RuntimeException("DB down"));

            assertThatThrownBy(() -> goalService.createGoal(r, USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error al registrar la meta. (502 Bad Gateway)");
        }
    }

    // =========================================================
    // PUT / PATCH /api/goals/{goalId}
    // =========================================================
    @Nested
    @DisplayName("putOrPatchGoal (PUT/PATCH /api/goals/{goalId})")
    class PutPatchGoal {

        @Test
        @DisplayName("200 - Actualiza meta correctamente")
        void ok_update() {
            GoalRequest r = mkRequestOk();
            Goal current = mkGoal(GOAL_ID, USER_ID, "Old", true);
            when(goalRepository.findByIdAndUserId(GOAL_ID, USER_ID)).thenReturn(Optional.of(current));
            when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

            GoalResponse resp = goalService.putOrPatchGoal(GOAL_ID, r, USER_ID);

            assertThat(resp.getId()).isEqualTo(GOAL_ID);
            assertThat(resp.getGoal_name()).isEqualTo("Drink water");
            verify(goalRepository).findByIdAndUserId(GOAL_ID, USER_ID);
            verify(goalRepository).save(any(Goal.class));
        }

        @Test
        @DisplayName("400 - Body vacío")
        void bad_request_empty_body() {
            assertThatThrownBy(() -> goalService.putOrPatchGoal(GOAL_ID, null, USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Body vacío");
            verify(goalRepository, never()).findByIdAndUserId(any(), any());
        }

        @Test
        @DisplayName("400 - weekly_target fuera de rango")
        void bad_request_weekly_target() {
            GoalRequest r = mkRequestOk();
            r.setWeekly_target(9);

            assertThatThrownBy(() -> goalService.putOrPatchGoal(GOAL_ID, r, USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("weekly_target debe estar entre 1 y 7");
            verify(goalRepository, never()).findByIdAndUserId(any(), any());
        }

        @Test
        @DisplayName("400 - rango de fechas inválido")
        void bad_request_dates() {
            GoalRequest r = mkRequestOk();
            r.setStart_date(LocalDate.of(2025, 6, 1));
            r.setEnd_date(LocalDate.of(2025, 5, 31));

            assertThatThrownBy(() -> goalService.putOrPatchGoal(GOAL_ID, r, USER_ID))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("end_date no puede ser anterior a start_date");
            verify(goalRepository, never()).findByIdAndUserId(any(), any());
        }

        @Test
        @DisplayName("401 - userId nulo => SecurityException")
        void unauthorized_update() {
            GoalRequest r = mkRequestOk();
            assertThatThrownBy(() -> goalService.putOrPatchGoal(GOAL_ID, r, null))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Token inválido o sesión expirada");
            verify(goalRepository, never()).findByIdAndUserId(any(), any());
        }

        @Test
        @DisplayName("404 - Meta no encontrada para id y user")
        void not_found() {
            GoalRequest r = mkRequestOk();
            when(goalRepository.findByIdAndUserId(GOAL_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> goalService.putOrPatchGoal(GOAL_ID, r, USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Meta no encontrada");
        }

        @Test
        @DisplayName("502 - error al actualizar => RuntimeException")
        void bad_gateway_on_update_error() {
            GoalRequest r = mkRequestOk();
            Goal current = mkGoal(GOAL_ID, USER_ID, "Old", true);
            when(goalRepository.findByIdAndUserId(GOAL_ID, USER_ID)).thenReturn(Optional.of(current));
            when(goalRepository.save(any(Goal.class))).thenThrow(new RuntimeException("DB down"));

            assertThatThrownBy(() -> goalService.putOrPatchGoal(GOAL_ID, r, USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error al actualizar la meta. (502 Bad Gateway)");
        }
    }

    // =========================================================
    // DELETE soft /api/goals/{goalId}?mode=soft
    // =========================================================
    @Nested
    @DisplayName("softDelete (DELETE /api/goals/{goalId}?mode=soft)")
    class SoftDelete {

        @Test
        @DisplayName("200 - Desactiva (soft delete) correctamente")
        void ok_soft_delete() {
            Goal current = mkGoal(GOAL_ID, USER_ID, "X", true);
            when(goalRepository.findByIdAndUserId(GOAL_ID, USER_ID)).thenReturn(Optional.of(current));
            when(goalRepository.save(any(Goal.class))).thenAnswer(inv -> inv.getArgument(0));

            goalService.softDelete(GOAL_ID, USER_ID);

            assertThat(current.getIsActive()).isFalse();
            verify(goalRepository).findByIdAndUserId(GOAL_ID, USER_ID);
            verify(goalRepository).save(current);
        }

        @Test
        @DisplayName("401 - userId nulo")
        void unauthorized_soft_delete() {
            assertThatThrownBy(() -> goalService.softDelete(GOAL_ID, null))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Token inválido o sesión expirada");
            verify(goalRepository, never()).findByIdAndUserId(any(), any());
        }

        @Test
        @DisplayName("404 - meta no encontrada")
        void not_found_soft() {
            when(goalRepository.findByIdAndUserId(GOAL_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> goalService.softDelete(GOAL_ID, USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Meta no encontrada");
        }

        @Test
        @DisplayName("500 - error interno al guardar soft delete")
        void internal_error_soft() {
            Goal current = mkGoal(GOAL_ID, USER_ID, "X", true);
            when(goalRepository.findByIdAndUserId(GOAL_ID, USER_ID)).thenReturn(Optional.of(current));
            when(goalRepository.save(any(Goal.class))).thenThrow(new RuntimeException("DB down"));

            assertThatThrownBy(() -> goalService.softDelete(GOAL_ID, USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error al desactivar la meta. (500 Internal Server Error)");
        }
    }

    // =========================================================
    // DELETE hard /api/goals/{goalId}
    // =========================================================
    @Nested
    @DisplayName("hardDelete (DELETE /api/goals/{goalId})")
    class HardDelete {

        @Test
        @DisplayName("204 - Elimina (hard) correctamente (no devuelve body en web layer)")
        void ok_hard_delete() {
            Goal current = mkGoal(GOAL_ID, USER_ID, "X", true);
            when(goalRepository.findByIdAndUserId(GOAL_ID, USER_ID)).thenReturn(Optional.of(current));
            doNothing().when(goalRepository).delete(current);

            goalService.hardDelete(GOAL_ID, USER_ID);

            verify(goalRepository).findByIdAndUserId(GOAL_ID, USER_ID);
            verify(goalRepository).delete(current);
        }

        @Test
        @DisplayName("401 - userId nulo")
        void unauthorized_hard_delete() {
            assertThatThrownBy(() -> goalService.hardDelete(GOAL_ID, null))
                    .isInstanceOf(SecurityException.class)
                    .hasMessageContaining("Token inválido o sesión expirada");
            verify(goalRepository, never()).findByIdAndUserId(any(), any());
        }

        @Test
        @DisplayName("404 - meta no encontrada")
        void not_found_hard() {
            when(goalRepository.findByIdAndUserId(GOAL_ID, USER_ID)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> goalService.hardDelete(GOAL_ID, USER_ID))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessageContaining("Meta no encontrada");
        }

        @Test
        @DisplayName("500 - error interno al eliminar")
        void internal_error_hard() {
            Goal current = mkGoal(GOAL_ID, USER_ID, "X", true);
            when(goalRepository.findByIdAndUserId(GOAL_ID, USER_ID)).thenReturn(Optional.of(current));
            doThrow(new RuntimeException("DB down")).when(goalRepository).delete(current);

            assertThatThrownBy(() -> goalService.hardDelete(GOAL_ID, USER_ID))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error al eliminar la meta permanentemente. (500 Internal Server Error)");
        }
    }
}
