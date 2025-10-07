package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealCategory;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
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

    public Meal registerMeal(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer) {
        meal.setId(null); // deja que lo genere la BD si quieres
        meal.setCreatedAt(Instant.now());
        return mealRepository.insert(meal, categoryIds, categoryNames, bearer);
    }

    public Optional<Meal> updateMeal(UUID userId, UUID mealId, Meal updated,
                                     List<Integer> categoryIds, List<String> categoryNames,
                                     String bearer) {
        return mealRepository.findById(mealId, bearer).map(existing -> {
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
            existing.setNote(updated.getNote());
            return mealRepository.update(existing, categoryIds, categoryNames, bearer);
        });
    }

    public void deleteMeal(UUID userId, UUID mealId, String bearer) {
        mealRepository.findById(mealId, bearer).ifPresent(meal -> {
            if (meal.getUserId().equals(userId)) {
                mealRepository.delete(mealId, bearer);
            } else {
                throw new IllegalArgumentException("Unauthorized deletion attempt");
            }
        });
    }

    public List<Meal> getMealsByDate(UUID userId, LocalDate date, String bearer) {
        return mealRepository.findByUserAndDate(userId, date, bearer);
    }

    public List<MealCategory> getCategories(String bearer) {
        return mealRepository.findAllCategories(bearer);
    }
}
