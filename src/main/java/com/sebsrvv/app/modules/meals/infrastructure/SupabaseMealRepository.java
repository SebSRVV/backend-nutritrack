package com.sebsrvv.app.modules.meals.infrastructure;

import com.sebsrvv.app.modules.meals.domain.*;
import java.time.LocalDate;
import java.util.*;

public class SupabaseMealRepository implements MealRepository {

    // Simulación, aquí usarías tu SupabaseDataClient
    private final Map<UUID, Meal> db = new HashMap<>();
    private final List<MealCategory> categories = List.of(
            new MealCategory() {{ setId(1); setName("Frutas"); setDescription("Frutas naturales"); }},
            new MealCategory() {{ setId(2); setName("Proteínas"); setDescription("Carnes, huevos, legumbres"); }},
            new MealCategory() {{ setId(3); setName("Ultraprocesados"); setDescription("Comida procesada"); }}
    );

    @Override
    public Meal save(Meal meal) {
        db.put(meal.getId(), meal);
        return meal;
    }

    @Override
    public Optional<Meal> findById(UUID mealId) {
        return Optional.ofNullable(db.get(mealId));
    }

    @Override
    public void delete(UUID mealId) {
        db.remove(mealId);
    }

    @Override
    public List<Meal> findByUserAndDate(UUID userId, LocalDate date) {
        return db.values().stream()
                .filter(m -> m.getUserId().equals(userId)
                        && m.getLoggedAt().atZone(java.time.ZoneOffset.UTC).toLocalDate().equals(date))
                .toList();
    }

    @Override
    public List<MealCategory> findAllCategories() {
        return categories;
    }
}
