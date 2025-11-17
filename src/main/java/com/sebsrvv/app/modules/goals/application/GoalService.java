// src/main/java/com/sebsrvv/app/modules/goals/application/GoalService.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.Goal;
import com.sebsrvv.app.modules.goals.domain.GoalRepository;
import com.sebsrvv.app.modules.goals.web.GoalMapper;
import com.sebsrvv.app.modules.goals.web.dto.GoalRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class GoalService {

    private final GoalRepository goalRepository;

    public GoalService(GoalRepository goalRepository) {
        this.goalRepository = goalRepository;
    }

    // ===== Casos de uso =====

    @Transactional(readOnly = true)
    public List<GoalResponse> listGoals(UUID userId) {
        return goalRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(GoalMapper::toResponse).toList();
    }

    @Transactional
    public GoalResponse createGoal(GoalRequest body, UUID userId) {
        Goal entity = GoalMapper.toEntity(body, userId);
        Goal saved = goalRepository.save(entity);
        return GoalMapper.toResponse(saved);
    }

    @Transactional
    public GoalResponse putOrPatchGoal(UUID goalId, GoalRequest body, UUID userId) {
        // Manejo del error 404/Propiedad (RN-06)
        Goal current = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "La meta que intentas modificar no existe o no te pertenece."));

        GoalMapper.patch(current, body);
        return GoalMapper.toResponse(goalRepository.save(current));
    }

    @Transactional
    public void softDelete(UUID goalId, UUID userId) {
        // Manejo del error 404/Propiedad (RN-06 + RN-12)
        Goal g = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "La meta que intentas eliminar no existe o no te pertenece."));

        g.setIsActive(false); // soft delete
        goalRepository.save(g);
    }

    @Transactional
    public void hardDelete(UUID goalId, UUID userId) {
        // Manejo del error 404/Propiedad (RN-06)
        Goal g = goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND, "La meta que intentas eliminar no existe o no te pertenece."));

        goalRepository.delete(g);
    }
}