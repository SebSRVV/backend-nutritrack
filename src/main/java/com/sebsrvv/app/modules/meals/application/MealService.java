package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.domain.FoodCategory;
import com.sebsrvv.app.modules.meals.exception.ResourceNotFoundException;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    // ✅ Crear un nuevo meal
    public MealResponse createMeal(MealRequest request) {
        Meal meal = new Meal();
        meal.setUserId(request.getUserId());
        meal.setName(request.getName());
        meal.setMealType(request.getMealType());
        meal.setCalories(request.getCalories());
        meal.setNote(request.getNote());
        meal.setLoggedAt(request.getLoggedAt());

        // ✅ Si el DTO tiene un categoryId, busca el objeto FoodCategory
        if (request.getCategoryId() != null) {
            FoodCategory category = entityManager.getReference(FoodCategory.class, request.getCategoryId());
            meal.setCategory(category);
        }

        Meal saved = mealRepository.save(meal);
        return toResponse(saved);
    }

    // ✅ Obtener meals de un usuario por fecha
    public List<MealResponse> getMealsByDate(UUID userId, LocalDate date) {
        return mealRepository.findByUserIdAndLoggedAt(userId, date)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ Obtener meals en un rango de fechas
    public List<MealResponse> getMealsBetweenDates(UUID userId, LocalDate from, LocalDate to) {
        return mealRepository.findMealsBetweenDates(userId, from, to)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ✅ Actualizar meal existente
    public MealResponse updateMeal(UUID mealId, MealRequest request) {
        Meal existing = mealRepository.findById(mealId)
                .orElseThrow(() -> new ResourceNotFoundException("Meal not found with ID: " + mealId));

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getMealType() != null) existing.setMealType(request.getMealType());
        if (request.getCalories() != null) existing.setCalories(request.getCalories());
        if (request.getNote() != null) existing.setNote(request.getNote());
        if (request.getLoggedAt() != null) existing.setLoggedAt(request.getLoggedAt());

        // ✅ Actualizar categoría si se envía un nuevo ID
        if (request.getCategoryId() != null) {
            FoodCategory category = entityManager.getReference(FoodCategory.class, request.getCategoryId());
            existing.setCategory(category);
        }

        Meal updated = mealRepository.save(existing);
        return toResponse(updated);
    }

    // ✅ Eliminar un meal
    public void deleteMeal(UUID mealId) {
        if (!mealRepository.existsById(mealId)) {
            throw new ResourceNotFoundException("Meal not found with ID: " + mealId);
        }
        mealRepository.deleteById(mealId);
    }

    // ✅ Conversión manual a DTO
    private MealResponse toResponse(Meal meal) {
        MealResponse response = new MealResponse();
        response.setId(meal.getId());
        response.setUserId(meal.getUserId());
        response.setName(meal.getName());
        response.setMealType(meal.getMealType());
        response.setCalories(meal.getCalories());
        response.setNote(meal.getNote());
        response.setLoggedAt(meal.getLoggedAt());

        // ✅ Devolver solo el ID de la categoría (no el objeto entero)
        if (meal.getCategory() != null) {
            response.setCategoryId(meal.getCategory().getId());
        }

        return response;
    }
}
