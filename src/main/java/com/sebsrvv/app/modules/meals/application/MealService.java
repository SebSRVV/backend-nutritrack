package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MealService {
    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public Meal registerMeal(Meal meal) {
        meal.setId(UUID.randomUUID());
        meal.setCreatedAt(java.time.Instant.now());
        return mealRepository.save(meal);
    }

    public Optional<Meal> updateMeal(UUID mealId, Meal updated) {
        return mealRepository.findById(mealId).map(existing -> {
            existing.setMealType(updated.getMealType());
            existing.setDescription(updated.getDescription());
            existing.setCalories(updated.getCalories());
            existing.setProteinG(updated.getProteinG());
            existing.setCarbsG(updated.getCarbsG());
            existing.setFatG(updated.getFatG());
            existing.setLoggedAt(updated.getLoggedAt());
            existing.setCategories(updated.getCategories());
            existing.setNote(updated.getNote());
            return mealRepository.save(existing);
        });
    }

    public void deleteMeal(UUID mealId) {
        mealRepository.delete(mealId);
    }

    public List<Meal> getMealsByDate(UUID userId, LocalDate date) {
        return mealRepository.findByUserAndDate(userId, date);
    }

    public List<MealCategory> getCategories() {
        return mealRepository.findAllCategories();
    }
}
