package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealCategory;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.exception.*;
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

    // Crear un nuevo meal
    public MealResponse createMeal(MealRequest request) {
        // Validaciones básicas
        if (request.getCalories() == null || request.getCalories() <= 0) {
            throw new InvalidMealDataException("Las calorías deben ser un número positivo.");
        }

        // Verificar si ya existe un meal con el mismo nombre y fecha para el usuario
        List<Meal> existing = mealRepository.findByUserIdAndLoggedAt(request.getUserId(), request.getLoggedAt());
        if (existing.stream().anyMatch(m -> m.getName().equalsIgnoreCase(request.getName()))) {
            throw new MealAlreadyExistsException("El meal ya existe para esta fecha y usuario.");
        }

        Meal meal = new Meal();
        meal.setUserId(request.getUserId());
        meal.setName(request.getName());
        meal.setMealType(request.getMealType());
        meal.setCalories(request.getCalories());
        meal.setNote(request.getNote());
        meal.setLoggedAt(request.getLoggedAt());

        // Buscar categoría si se envía un ID
        if (request.getCategoryId() != null) {
            MealCategory category;
            try {
                category = entityManager.getReference(MealCategory.class, request.getCategoryId());
            } catch (Exception e) {
                throw new CategoryNotFoundException("Categoría no encontrada con ID: " + request.getCategoryId());
            }
            meal.setCategory(category);
        }

        Meal saved = mealRepository.save(meal);
        return toResponse(saved);
    }

    // Obtener meals de un usuario por fecha
    public List<MealResponse> getMealsByDate(UUID userId, LocalDate date) {
        List<Meal> meals = mealRepository.findByUserIdAndLoggedAt(userId, date);
        if (meals.isEmpty()) {
            throw new MealNotFoundException("No se encontraron meals para esta fecha.");
        }
        return meals.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Obtener meals en un rango de fechas
    public List<MealResponse> getMealsBetweenDates(UUID userId, LocalDate from, LocalDate to) {
        List<Meal> meals = mealRepository.findMealsBetweenDates(userId, from, to);
        if (meals.isEmpty()) {
            throw new MealNotFoundException("No se encontraron meals en el rango de fechas.");
        }
        return meals.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Actualizar meal existente
    public MealResponse updateMeal(UUID mealId, MealRequest request) {
        Meal existing = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));

        if (request.getName() != null) existing.setName(request.getName());
        if (request.getMealType() != null) existing.setMealType(request.getMealType());
        if (request.getCalories() != null) {
            if (request.getCalories() <= 0)
                throw new InvalidMealDataException("Las calorías deben ser un número positivo.");
            existing.setCalories(request.getCalories());
        }
        if (request.getNote() != null) existing.setNote(request.getNote());
        if (request.getLoggedAt() != null) existing.setLoggedAt(request.getLoggedAt());

        // Actualizar categoría si se envía un nuevo ID
        if (request.getCategoryId() != null) {
            MealCategory category;
            try {
                category = entityManager.getReference(MealCategory.class, request.getCategoryId());
            } catch (Exception e) {
                throw new CategoryNotFoundException("Categoría no encontrada con ID: " + request.getCategoryId());
            }
            existing.setCategory(category);
        }

        Meal updated = mealRepository.save(existing);
        return toResponse(updated);
    }

    // Eliminar un meal
    public void deleteMeal(UUID mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));
        mealRepository.delete(meal);
    }

    // Conversión manual a DTO
    private MealResponse toResponse(Meal meal) {
        MealResponse response = new MealResponse();
        response.setId(meal.getId());
        response.setUserId(meal.getUserId());
        response.setName(meal.getName());
        response.setMealType(meal.getMealType());
        response.setCalories(meal.getCalories());
        response.setNote(meal.getNote());
        response.setLoggedAt(meal.getLoggedAt());

        if (meal.getCategory() != null) {
            response.setCategoryId(meal.getCategory().getId());
            response.setCategoryName(meal.getCategory().getName());
        }

        return response;
    }
}