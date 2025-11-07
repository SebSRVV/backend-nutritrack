package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.Goal;
import com.sebsrvv.app.modules.goals.domain.GoalRepository;
import com.sebsrvv.app.modules.goals.web.GoalMapper;
import com.sebsrvv.app.modules.goals.web.dto.GoalRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.ConnectException;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    // ===== Validaciones generales =====
    private void validateGoalBody(GoalRequest r) {
        if (r == null) {
            throw new IllegalArgumentException("Body vacío: se requiere información de la meta.");
        }
    }

    private void validateWeeklyTarget(Integer wt) {
        if (wt != null && (wt < 1 || wt > 7)) {
            throw new IllegalArgumentException("weekly_target debe estar entre 1 y 7.");
        }
    }

    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start != null && end != null && end.isBefore(start)) {
            throw new IllegalArgumentException("end_date no puede ser anterior a start_date.");
        }
    }

    // ===== Casos de uso =====

    @Transactional(readOnly = true)
    public List<GoalResponse> listGoals(UUID userId) {
        try {
            if (userId == null) {
                throw new SecurityException("Token inválido o sesión expirada. (401 Unauthorized)");
            }

            List<Goal> goals = goalRepository.findByUserIdOrderByCreatedAtDesc(userId);
            if (goals == null) {
                throw new NoSuchElementException("No se pudieron recuperar las metas del usuario.");
            }

            return goals.stream().map(GoalMapper::toResponse).toList();

        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al listar las metas. (502 Bad Gateway)");
        }
    }

    @Transactional
    public GoalResponse createGoal(GoalRequest body, UUID userId) {
        try {
            if (userId == null) {
                throw new SecurityException("Token inválido o sesión expirada. (401 Unauthorized)");
            }

            validateGoalBody(body);
            validateWeeklyTarget(body.getWeekly_target());
            validateDateRange(body.getStart_date(), body.getEnd_date());

            Goal entity = GoalMapper.toEntity(body, userId);
            Goal saved = goalRepository.save(entity);
            return GoalMapper.toResponse(saved);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar la meta. (502 Bad Gateway)");
        }
    }

    @Transactional
    public GoalResponse putOrPatchGoal(UUID goalId, GoalRequest body, UUID userId) {
        try {
            if (userId == null) {
                throw new SecurityException("Token inválido o sesión expirada. (401 Unauthorized)");
            }

            validateGoalBody(body);
            validateWeeklyTarget(body.getWeekly_target());
            validateDateRange(body.getStart_date(), body.getEnd_date());

            Goal current = goalRepository.findByIdAndUserId(goalId, userId)
                    .orElseThrow(() -> new NoSuchElementException("Meta no encontrada para id=" + goalId + " y userId=" + userId));

            GoalMapper.patch(current, body);
            Goal updated = goalRepository.save(current);
            return GoalMapper.toResponse(updated);

        } catch (IllegalArgumentException | NoSuchElementException | SecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar la meta. (502 Bad Gateway)");
        }
    }

    @Transactional
    public void softDelete(UUID goalId, UUID userId) {
        try {
            if (userId == null) {
                throw new SecurityException("Token inválido o sesión expirada. (401 Unauthorized)");
            }

            Goal g = goalRepository.findByIdAndUserId(goalId, userId)
                    .orElseThrow(() -> new NoSuchElementException("Meta no encontrada para id=" + goalId + " y userId=" + userId));

            g.setIsActive(false);
            goalRepository.save(g);

        } catch (NoSuchElementException | SecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al desactivar la meta. (500 Internal Server Error)");
        }
    }

    @Transactional
    public void hardDelete(UUID goalId, UUID userId) {
        try {
            if (userId == null) {
                throw new SecurityException("Token inválido o sesión expirada. (401 Unauthorized)");
            }

            Goal g = goalRepository.findByIdAndUserId(goalId, userId)
                    .orElseThrow(() -> new NoSuchElementException("Meta no encontrada para id=" + goalId + " y userId=" + userId));

            goalRepository.delete(g);

        } catch (NoSuchElementException | SecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la meta permanentemente. (500 Internal Server Error)");
        }
    }
}
