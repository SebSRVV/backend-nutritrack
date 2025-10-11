package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealCategory;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.web.dto.FoodCategoryBreakdownResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Servicio encargado de la lógica de negocio relacionada con las comidas (Meals).
 */
@Service
public class MealService {

    private final MealRepository mealRepository;

    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    public Meal registerMeal(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer) {
        meal.setId(null);
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
            // Si tu dominio tiene 'note', vuelve a habilitar:
            // existing.setNote(updated.getNote());
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
        if (date == null) {
            throw new IllegalArgumentException("date no puede ser null. Usa el método de rango.");
        }
        return mealRepository.findByUserAndDate(userId, date, bearer);
    }

    public List<MealCategory> getCategories(String bearer) {
        return mealRepository.findAllCategories(bearer);
    }

    public List<FoodCategoryBreakdownResponse> getCategoryBreakdown(
            UUID userId, String bearer, LocalDate from, LocalDate to) {

        if (userId == null) throw new IllegalArgumentException("userId no puede ser null");

        // Normaliza rango
        LocalDate effFrom = from, effTo = to;
        if (effFrom == null && effTo == null) {
            LocalDate today = LocalDate.now(java.time.ZoneOffset.UTC);
            effFrom = today; effTo = today;
        } else if (effFrom != null && effTo == null) {
            effTo = effFrom;
        } else if (effFrom == null) { // effTo != null
            effFrom = effTo;
        }

        // SIEMPRE rango + userId (para cumplir RLS)
        List<Meal> meals = mealRepository.findByUserAndDateRange(userId, effFrom, effTo, bearer);

        Map<Integer, FoodCategoryBreakdownResponse> map = new HashMap<>();
        for (Meal meal : meals) {
            if (meal == null || meal.getCategories() == null) continue;
            int kcals = meal.getCalories() == null ? 0 : meal.getCalories();
            for (MealCategory cat : meal.getCategories()) {
                FoodCategoryBreakdownResponse e = map.getOrDefault(cat.getId(), new FoodCategoryBreakdownResponse());
                e.setCategoryId(cat.getId());
                e.setName(cat.getName());
                e.setCount((e.getCount() == null ? 0 : e.getCount()) + 1);
                e.setCalories((e.getCalories() == null ? 0 : e.getCalories()) + kcals);
                map.put(cat.getId(), e);
            }
        }
        return new ArrayList<>(map.values());
    }

}

