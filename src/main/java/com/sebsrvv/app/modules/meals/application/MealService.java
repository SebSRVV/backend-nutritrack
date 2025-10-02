package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
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

    public Optional<Meal> updateMeal(UUID userId, UUID mealId, Meal updated) {
        return mealRepository.findById(mealId).map(existing -> {
            if (!existing.getUserId().equals(userId)) {
                return null; // Prevención de acceso indebido
            }
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

    public void deleteMeal(UUID userId, UUID mealId) {
        mealRepository.findById(mealId).ifPresent(meal -> {
            if (meal.getUserId().equals(userId)) {
                mealRepository.delete(mealId);
            }
        });
    }

    public List<Meal> getMealsByDate(UUID userId, LocalDate date) {
        return mealRepository.findByUserAndDate(userId, date);
    }

    public List<MealCategory> getCategories() {
        return mealRepository.findAllCategories();
    }
}
