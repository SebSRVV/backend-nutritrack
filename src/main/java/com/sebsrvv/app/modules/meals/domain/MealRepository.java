package com.sebsrvv.app.modules.meals.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MealRepository {
    Meal save(Meal meal);
    Optional<Meal> findById(UUID mealId);
    void delete(UUID mealId);
    List<Meal> findByUserAndDate(UUID userId, LocalDate date);
    List<MealCategory> findAllCategories();
}
