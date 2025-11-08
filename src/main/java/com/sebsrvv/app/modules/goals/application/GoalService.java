// src/main/java/com/sebsrvv/app/modules/goals/application/GoalService.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.Goal;
import com.sebsrvv.app.modules.goals.domain.GoalRepository;
import com.sebsrvv.app.modules.goals.exception.EmptyGoalDataException;
import com.sebsrvv.app.modules.goals.exception.GoalNotFoundException;
import com.sebsrvv.app.modules.goals.exception.InvalidDateRangeException;
import com.sebsrvv.app.modules.goals.exception.InvalidWeeklyTargetException;
import com.sebsrvv.app.modules.goals.web.GoalMapper;
import com.sebsrvv.app.modules.goals.web.dto.GoalRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    // ===== Validaciones RN-04 =====
    private void validateGoalForCreate(GoalRequest r) {
        if (r == null) throw new EmptyGoalDataException();

        if (r.getWeekly_target() != null) {
            int wt = r.getWeekly_target();
            if (wt < 1 || wt > 7) throw new InvalidWeeklyTargetException();
        }
        LocalDate s = r.getStart_date();
        LocalDate e = r.getEnd_date();
        if (s != null && e != null && e.isBefore(s)) throw new InvalidDateRangeException();
    }

    private void validateGoalForUpdate(GoalRequest r) {
        if (r == null) throw new EmptyGoalDataException();

        if (r.getWeekly_target() != null) {
            int wt = r.getWeekly_target();
            if (wt < 1 || wt > 7) throw new InvalidWeeklyTargetException();
        }
        LocalDate s = r.getStart_date();
        LocalDate e = r.getEnd_date();
        if (s != null && e != null && e.isBefore(s)) throw new InvalidDateRangeException();
    }

    // ===== Casos de uso =====

    @Transactional(readOnly = true)
    public List<GoalResponse> listGoals(UUID userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(GoalMapper::toResponse).toList();
    }

    @Transactional
    public GoalResponse createGoal(GoalRequest body, UUID userId) {
        validateGoalForCreate(body);                // RN-04
        Goal entity = GoalMapper.toEntity(body, userId);
        Goal saved = goalRepository.save(entity);
        return GoalMapper.toResponse(saved);
    }

    @Transactional
    public GoalResponse putOrPatchGoal(UUID goalId, GoalRequest body, UUID userId) {
        validateGoalForUpdate(body);                // RN-04
        // RN-06: ownership -> buscar por id y userId
        Goal current = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));
        GoalMapper.patch(current, body);
        return GoalMapper.toResponse(goalRepository.save(current));
    }

    @Transactional
    public void softDelete(UUID goalId, UUID userId) {
        // RN-06 + RN-12
        Goal g = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));
        g.setIsActive(false);                       // soft delete
        goalRepository.save(g);
    }

    @Transactional
    public void hardDelete(UUID goalId, UUID userId) {
        // RN-06
        Goal g = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new GoalNotFoundException(goalId));
        goalRepository.delete(g);
    }
}
