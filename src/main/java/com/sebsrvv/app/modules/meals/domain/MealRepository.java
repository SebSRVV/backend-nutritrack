package com.sebsrvv.app.modules.meals.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MealRepository {
    Meal insert(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer);
    Meal update(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer);
    Optional<Meal> findById(UUID mealId, String bearer);
    void delete(UUID mealId, String bearer);
    List<Meal> findByUserAndDate(UUID userId, LocalDate date, String bearer);
    List<MealCategory> findAllCategories(String bearer);
}
