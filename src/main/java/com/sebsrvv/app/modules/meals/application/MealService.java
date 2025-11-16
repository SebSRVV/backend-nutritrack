package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.*;
import com.sebsrvv.app.modules.meals.exception.*;
import com.sebsrvv.app.modules.meals.web.MealMapper;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final MealMapper mealMapper;

    public MealService(MealRepository mealRepository,
                       MealMapper mealMapper) {
        this.mealRepository = mealRepository;
        this.mealMapper = mealMapper;
    }

    // Crear meal
    public MealResponse createMeal(String userId, MealRequest request) {
        if (request.getCalories() == null || request.getCalories() < 0) {
            throw new InvalidMealDataException("Calorías inválidas");
        }
        MealLog meal = mealMapper.toEntity(userId, request);
        MealLog saved = mealRepository.save(meal);
        return mealMapper.toResponse(saved);
    }

    // Listar todas las meals de un usuario
    @Transactional(readOnly = true)
    public List<MealResponse> getAllMeals(String userId) {
        return mealRepository.findByUserId(userId)
                .stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Obtener una meal específica
    @Transactional(readOnly = true)
    public MealResponse getMeal(Long mealId, String userId) {
        MealLog meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));

        if (!meal.getUserId().equals(userId)) {
            throw new UnauthorizedMealAccessException("No tienes permiso para ver este meal.");
        }
        return mealMapper.toResponse(meal);
    }

    // Actualizar meal
    public MealResponse updateMeal(Long mealId, String userId, MealRequest request) {
        MealLog meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));

        if (!meal.getUserId().equals(userId)) {
            throw new UnauthorizedMealAccessException("No tienes permiso para actualizar este meal.");
        }

        mealMapper.updateEntityFromRequest(meal, request);
        MealLog updated = mealRepository.save(meal);
        return mealMapper.toResponse(updated);
    }

    // Eliminar meal
    public void deleteMeal(Long mealId, String userId) {
        MealLog meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));

        if (!meal.getUserId().equals(userId)) {
            throw new UnauthorizedMealAccessException("No tienes permiso para eliminar este meal.");
        }

        mealRepository.delete(meal);
    }
}
