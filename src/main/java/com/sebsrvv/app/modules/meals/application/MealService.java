package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.exception.MealNotFoundException;
import com.sebsrvv.app.modules.meals.web.MealMapper;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final MealMapper mealMapper;

    public MealService(MealRepository mealRepository, MealMapper mealMapper) {
        this.mealRepository = mealRepository;
        this.mealMapper = mealMapper;
    }

    // Crear meal (POST)
    public MealResponse createMeal(MealRequest request) {
        Meal meal = mealMapper.toEntity(request);
        Meal saved = mealRepository.save(meal);
        return mealMapper.toResponse(saved);
    }

    // Listar todos los meals (GET)
    public List<MealResponse> getAllMeals() {
        return mealRepository.findAll()
                .stream()
                .map(mealMapper::toResponse)
                .collect(Collectors.toList());
    }

    // Actualizar meal (PUT)
    public MealResponse updateMeal(UUID mealId, MealRequest request) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));
        meal.setMealType(request.getMealType());
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein_g(request.getProtein_g());
        meal.setCarbs_g(request.getCarbs_g());
        meal.setFat_g(request.getFat_g());
        meal.setLoggedAt(request.getLoggedAt());
        Meal updated = mealRepository.save(meal);
        return mealMapper.toResponse(updated);
    }

    // Eliminar meal (DELETE)
    public void deleteMeal(UUID mealId) {
        Meal meal = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));
        mealRepository.delete(meal);
    }
}
