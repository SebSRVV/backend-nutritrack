// src/main/java/com/sebsrvv/app/modules/meals/application/MealService.java
package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.exception.MealNotFoundException;
import com.sebsrvv.app.modules.meals.exception.UnauthorizedMealAccessException;
import com.sebsrvv.app.modules.meals.web.MealMapper;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio de dominio para meals.
 * Observe que TODAS las operaciones que dependen del usuario reciben userId (extraído en Controller).
 */
@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final MealMapper mealMapper;

    public MealService(MealRepository mealRepository, MealMapper mealMapper) {
        this.mealRepository = mealRepository;
        this.mealMapper = mealMapper;
    }

    // Crear meal (asociada a userId)
    public MealResponse createMeal(UUID userId, MealRequest request) {
        Meal meal = mealMapper.toEntity(userId, request);
        Meal saved = mealRepository.save(meal);
        return mealMapper.toResponse(saved);
    }

    // Listar meals del usuario
    @Transactional(readOnly = true)
    public List<MealResponse> getAllMeals(UUID userId) {
        return mealRepository.findByUserId(userId)
                .stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener meal específico (validando ownership)
    @Transactional(readOnly = true)
    public MealResponse getMeal(UUID mealId, UUID userId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));
        if (!meal.getUserId().equals(userId)) {
            throw new UnauthorizedMealAccessException("No tienes permiso para ver este meal.");
        }
        return mealMapper.toResponse(meal);
    }

    // Actualizar meal (valida ownership)
    public MealResponse updateMeal(UUID mealId, UUID userId, MealRequest request) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));
        if (!meal.getUserId().equals(userId)) {
            throw new UnauthorizedMealAccessException("No tienes permiso para actualizar este meal.");
        }
        mealMapper.updateEntityFromRequest(meal, request);
        Meal updated = mealRepository.save(meal);
        return mealMapper.toResponse(updated);
    }

    // Eliminar meal (valida ownership)
    public void deleteMeal(UUID mealId, UUID userId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));
        if (!meal.getUserId().equals(userId)) {
            throw new UnauthorizedMealAccessException("No tienes permiso para eliminar este meal.");
        }
        mealRepository.delete(meal);
    }
}
