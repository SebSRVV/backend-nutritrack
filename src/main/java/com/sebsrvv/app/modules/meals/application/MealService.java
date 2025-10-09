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
 * Se comunica con el repositorio de datos para registrar, categorias, actualizar o eliminar comidas.
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


    /**
     * Obtiene un desglose de comidas por categoría, con conteo y calorías totales.
     * Aplica un filtro por rango de fechas directamente desde el servicio.
     *
     * @param bearer token de autorización
     * @param from   fecha inicial opcional (YYYY-MM-DD)
     * @param to     fecha final opcional (YYYY-MM-DD)
     * @return lista de objetos FoodCategoryBreakdownResponse con resumen por categoría
     */
    public List<FoodCategoryBreakdownResponse> getCategoryBreakdown(String bearer, LocalDate from, LocalDate to) {
        // TODO: reemplazar null por el userId real extraído del JWT si está disponible
        UUID userId = null;

        // Obtiene todas las comidas del usuario (sin filtrar por fecha en el repositorio)
        // Si `date` es null, el repositorio debe devolver todas las comidas del usuario
        List<Meal> allMeals = mealRepository.findByUserAndDate(userId, null, bearer);

        // Lista donde se guardarán solo las comidas dentro del rango de fechas especificado
        List<Meal> filteredMeals = new ArrayList<>();

        for (Meal meal : allMeals) {
            // Ignora comidas que no tengan fecha registrada
            if (meal.getLoggedAt() == null) continue;

            // Convierte la fecha y hora de la comida (Instant) a LocalDate para poder compararla
            LocalDate mealDate = meal.getLoggedAt().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            // Verifica si la comida está dentro del rango de fechas solicitado (incluyendo límites)
            boolean isAfterOrEqualFrom = (from == null) || (!mealDate.isBefore(from)); // >= from
            boolean isBeforeOrEqualTo = (to == null) || (!mealDate.isAfter(to));       // <= to

            // Si cumple con ambas condiciones, se incluye en la lista filtrada
            if (isAfterOrEqualFrom && isBeforeOrEqualTo) {
                filteredMeals.add(meal);
            }
        }

        // Mapa temporal para agrupar los datos por categoría
        Map<Integer, FoodCategoryBreakdownResponse> map = new HashMap<>();

        // Recorre cada comida filtrada
        for (Meal meal : filteredMeals) {
            // Ignora comidas sin categorías asociadas
            if (meal.getCategories() == null) continue;

            // Por cada categoría de la comida, actualiza el resumen acumulado
            for (MealCategory cat : meal.getCategories()) {
                // Obtiene el objeto resumen actual o crea uno nuevo si aún no existe
                FoodCategoryBreakdownResponse entry = map.getOrDefault(cat.getId(), new FoodCategoryBreakdownResponse());

                // Establece los datos de la categoría
                entry.setCategoryId(cat.getId());
                entry.setName(cat.getName());

                // Incrementa el contador de comidas en esa categoría
                entry.setCount((entry.getCount() == null ? 0 : entry.getCount()) + 1);

                // Suma las calorías de la comida a esa categoría (evita nulls)
                entry.setCalories((entry.getCalories() == null ? 0 : entry.getCalories())
                        + (meal.getCalories() == null ? 0 : meal.getCalories()));

                // Guarda el resultado actualizado en el mapa
                map.put(cat.getId(), entry);
            }
        }

        // Convierte el mapa de resultados a una lista para devolverla como respuesta
        return new ArrayList<>(map.values());
    }

}
