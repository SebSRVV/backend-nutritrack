package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealCategory;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.web.dto.FoodCategoryBreakdownResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * Servicio encargado de la lógica de negocio relacionada con las comidas (Meals).
 * Se comunica con el repositorio de datos para registrar, actualizar o eliminar comidas.
 */
@Service
public class MealService {

    private final MealRepository mealRepository;

    // Inyección de dependencia del repositorio
    public MealService(MealRepository mealRepository) {
        this.mealRepository = mealRepository;
    }

    /**
     * Registra una nueva comida en la base de datos.
     */
    public Meal registerMeal(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer) {
        meal.setId(null); // deja que la BD genere el ID
        meal.setCreatedAt(Instant.now());
        return mealRepository.insert(meal, categoryIds, categoryNames, bearer);
    }

    /**
     * Actualiza una comida existente si pertenece al usuario.
     */
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

    /**
     * Elimina una comida si pertenece al usuario autenticado.
     */
    public void deleteMeal(UUID userId, UUID mealId, String bearer) {
        mealRepository.findById(mealId, bearer).ifPresent(meal -> {
            if (meal.getUserId().equals(userId)) {
                mealRepository.delete(mealId, bearer);
            } else {
                throw new IllegalArgumentException("Unauthorized deletion attempt");
            }
        });
    }

    /**
     * Obtiene todas las comidas de un usuario para una fecha específica.
     */
    public List<Meal> getMealsByDate(UUID userId, LocalDate date, String bearer) {
        return mealRepository.findByUserAndDate(userId, date, bearer);
    }

    /**
     * Devuelve todas las categorías de comidas.
     */
    public List<MealCategory> getCategories(String bearer) {
        return mealRepository.findAllCategories(bearer);
    }

    // ========================= MÉTODO NUEVO =========================
    /**
     * Obtiene un desglose de comidas por categoría, con conteo y calorías totales.
     * Ahora con rango de fechas real.
     * @param bearer token de autorización
     * @param from fecha inicial opcional (YYYY-MM-DD)
     * @param to fecha final opcional (YYYY-MM-DD)
     * @return lista de objetos FoodCategoryBreakdownResponse con resumen por categoría
     */
    public List<FoodCategoryBreakdownResponse> getCategoryBreakdown(String bearer, LocalDate from, LocalDate to) {
        // TODO: reemplazar null por userId real extraído del JWT si lo tienes
        UUID userId = null;

        List<Meal> meals;
        if (from != null && to != null) {
            // Llama a tu nuevo método en el repositorio (añadirlo allí)
            meals = mealRepository.findByUserAndDateRange(userId, from, to, bearer);
        } else {
            meals = mealRepository.findByUserAndDate(userId, null, bearer);
        }

        Map<Integer, FoodCategoryBreakdownResponse> map = new HashMap<>();
        for (Meal meal : meals) {
            if (meal.getCategories() == null) continue;
            for (MealCategory cat : meal.getCategories()) {
                FoodCategoryBreakdownResponse entry = map.getOrDefault(cat.getId(), new FoodCategoryBreakdownResponse());
                entry.setCategoryId(cat.getId());
                entry.setName(cat.getName());
                entry.setCount((entry.getCount() == null ? 0 : entry.getCount()) + 1);
                entry.setCalories((entry.getCalories() == null ? 0 : entry.getCalories()) + (meal.getCalories() == null ? 0 : meal.getCalories()));
                map.put(cat.getId(), entry);
            }
        }

        return new ArrayList<>(map.values());
    }
    // ========================= FIN MÉTODO NUEVO =========================
}
