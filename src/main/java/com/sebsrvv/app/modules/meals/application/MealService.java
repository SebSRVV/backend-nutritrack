package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Instant;
import java.util.*;

@Service
public class MealService {

    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public Meal registerMeal(Meal meal) {
        meal.setId(UUID.randomUUID());
        meal.setCreatedAt(Instant.now());
        return mealRepository.save(meal);
    }

    public Optional<Meal> updateMeal(UUID userId, UUID mealId, Meal updated) {
        return mealRepository.findById(mealId).map(existing -> {
            if (!existing.getUserId().equals(userId)) {
                throw new IllegalArgumentException("Unauthorized: meal does not belong to this user");
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
            } else {
                throw new IllegalArgumentException("Unauthorized deletion attempt");
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
