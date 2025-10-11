package com.sebsrvv.app.modules.meals.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Interfaz del dominio que define las operaciones que debe cumplir
 * cualquier implementación del repositorio de comidas (MealRepository).
 *
 * 🔹 Será implementada por la clase: SupabaseMealRepository
 * 🔹 Se inyectará en MealService mediante Spring (@Repository + @Service)
 */
public interface MealRepository {

    /**
     * Inserta un nuevo registro de comida (Meal) con sus categorías.
     *
     * @param meal Objeto Meal a insertar.
     * @param categoryIds IDs de categorías existentes.
     * @param categoryNames Nombres de categorías nuevas a crear.
     * @param bearer Token JWT del usuario autenticado.
     * @return El objeto Meal insertado con su ID asignado.
     */
    Meal insert(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer);

    /**
     * Actualiza un registro existente de comida.
     *
     * @param meal Objeto Meal con los nuevos datos.
     * @param categoryIds IDs de categorías asociadas.
     * @param categoryNames Nombres de nuevas categorías.
     * @param bearer Token JWT del usuario autenticado.
     * @return El objeto Meal actualizado.
     */
    Meal update(Meal meal, List<Integer> categoryIds, List<String> categoryNames, String bearer);

    /**
     * Busca una comida por su ID.
     *
     * @param mealId ID del Meal.
     * @param bearer Token JWT del usuario autenticado.
     * @return Optional con el Meal encontrado o vacío si no existe.
     */
    Optional<Meal> findById(UUID mealId, String bearer);

    /**
     * Elimina un Meal por su ID.
     *
     * @param mealId ID del Meal a eliminar.
     * @param bearer Token JWT del usuario autenticado.
     */
    void delete(UUID mealId, String bearer);

    /**
     * Busca comidas por usuario y fecha.
     *
     * @param userId ID del usuario.
     * @param date Fecha específica.
     * @param bearer Token JWT del usuario autenticado.
     * @return Lista de Meals registrados en esa fecha.
     */
    List<Meal> findByUserAndDate(UUID userId, LocalDate date, String bearer);

    /**
     * Obtiene todas las categorías de comidas disponibles.
     *
     * @param bearer Token JWT del usuario autenticado.
     * @return Lista de categorías.
     */
    List<MealCategory> findAllCategories(String bearer);

    // ========================= MÉTODO NUEVO =========================
    /**
     * Busca comidas de un usuario entre un rango de fechas.
     *
     * @param userId ID del usuario
     * @param from Fecha inicial
     * @param to Fecha final
     * @param bearer Token JWT del usuario autenticado
     * @return Lista de Meals dentro del rango de fechas
     */
    List<Meal> findByUserAndDateRange(UUID userId, LocalDate from, LocalDate to, String bearer);
    // ========================= FIN MÉTODO NUEVO =========================
}
