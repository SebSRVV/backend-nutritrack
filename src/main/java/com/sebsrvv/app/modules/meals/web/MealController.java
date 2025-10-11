package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.web.dto.FoodCategoryBreakdownRequest;
import com.sebsrvv.app.modules.meals.web.dto.FoodCategoryBreakdownResponse;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Controlador REST para manejar las operaciones relacionadas con las comidas (Meals)
 * de un usuario específico.
 *
 * Ruta base: /api/users/{userId}/meals
 */
@RestController
@RequestMapping("/api/users/{userId}/meals")
public class MealController {

    private final MealService mealService;

    /**
     * Constructor: inyección del servicio MealService
     */
    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    /**
     * Crea una nueva comida (Meal) asociada a un usuario.
     *
     * Método: POST /api/users/{userId}/meals
     *
     * @param userId UUID del usuario dueño del registro
     * @param request Datos de la comida (DTO)
     * @param authorization Token Bearer del usuario autenticado
     * @return MealResponse con los datos guardados
     */
    @PostMapping
    public ResponseEntity<MealResponse> createMeal(
            @PathVariable UUID userId,
            @Valid @RequestBody MealRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        // Mapea el DTO a la entidad Meal
        Meal meal = mapRequestToMeal(request);
        meal.setUserId(userId);

        // Llama al servicio para registrar la comida con sus categorías
        var saved = mealService.registerMeal(
                meal,
                request.getCategoryIds(),
                request.getCategories(),
                authorization
        );

        // Devuelve la respuesta con la comida registrada
        return ResponseEntity.ok(MealMapper.toResponse(saved));
    }

    /**
     * Obtiene todas las comidas de un usuario en una fecha específica.
     *
     * Método: GET /api/users/{userId}/meals?date=YYYY-MM-DD
     */
    @GetMapping
    public ResponseEntity<List<MealResponse>> getMealsByDate(
            @PathVariable UUID userId,
            @RequestParam String date,
            @RequestHeader("Authorization") String authorization
    ) {
        // Obtiene las comidas filtradas por fecha
        var meals = mealService.getMealsByDate(userId, LocalDate.parse(date), authorization);

        // Convierte la lista de entidades en respuestas DTO
        var responses = meals.stream()
                .map(MealMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Actualiza una comida existente del usuario.
     *
     * Método: PUT /api/users/{userId}/meals/{mealId}
     */
    @PutMapping("/{mealId}")
    public ResponseEntity<MealResponse> updateMeal(
            @PathVariable UUID userId,
            @PathVariable UUID mealId,
            @Valid @RequestBody MealRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        // Convierte el DTO en entidad Meal actualizada
        Meal updated = mapRequestToMeal(request);

        // Intenta actualizar y devuelve la comida actualizada si existe
        return mealService.updateMeal(
                        userId,
                        mealId,
                        updated,
                        request.getCategoryIds(),
                        request.getCategories(),
                        authorization
                )
                .map(m -> ResponseEntity.ok(MealMapper.toResponse(m)))
                .orElse(ResponseEntity.notFound().build()); // Si no existe, retorna 404
    }

    /**
     * Elimina una comida registrada por el usuario.
     *
     * Método: DELETE /api/users/{userId}/meals/{mealId}
     */
    @DeleteMapping("/{mealId}")
    public ResponseEntity<?> deleteMeal(
            @PathVariable UUID userId,
            @PathVariable UUID mealId,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            // Elimina la comida si pertenece al usuario autenticado
            mealService.deleteMeal(userId, mealId, authorization);
            return ResponseEntity.ok().body("{\"status\":\"success\",\"message\":\"Meal deleted\"}");
        } catch (IllegalArgumentException ex) {
            // Si intenta eliminar una comida que no es suya
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"status\":\"error\",\"message\":\"" + ex.getMessage() + "\"}");
        }
    }

    /**
     * Obtiene el desglose de categorías de alimentos consumidos por un usuario.
     *
     * Método: GET /api/users/{userId}/meals/food-categories
     *
     * Parámetros opcionales: from (fecha inicio) y to (fecha fin)
     */
    @GetMapping("/food-categories")
    public ResponseEntity<List<FoodCategoryBreakdownResponse>> getFoodCategoryBreakdown(
            @PathVariable UUID userId,
            @RequestHeader("Authorization") String authorization,
            @Valid FoodCategoryBreakdownRequest request
    ) {
        // Convierte las fechas (si existen)
        LocalDate from = parseIso(request.getFrom());
        LocalDate to   = parseIso(request.getTo());

        // Normaliza el rango de fechas
        if (from == null && to == null) {
            LocalDate today = LocalDate.now(ZoneOffset.UTC);
            from = today; to = today;
        } else if (from != null && to == null) {
            to = from;
        } else if (from == null) {
            from = to;
        }

        // Llama al servicio para obtener el desglose de calorías por categoría
        var data = mealService.getCategoryBreakdown(userId, authorization, from, to);
        return ResponseEntity.ok(data);
    }

    /**
     * Método auxiliar para convertir una cadena ISO (yyyy-MM-dd) en LocalDate.
     * Devuelve null si la cadena es nula o vacía.
     */
    private static LocalDate parseIso(String s) {
        return (s == null || s.isBlank()) ? null : LocalDate.parse(s);
    }

    /**
     * Convierte un objeto MealRequest (DTO) a una entidad Meal.
     */
    private Meal mapRequestToMeal(MealRequest request) {
        Meal meal = new Meal();
        meal.setMealType(MealType.valueOf(request.getMealType().toUpperCase()));
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProteinG(request.getProteinG());
        meal.setCarbsG(request.getCarbsG());
        meal.setFatG(request.getFatG());
        meal.setLoggedAt(request.getLoggedAt());
        return meal;
    }
}
