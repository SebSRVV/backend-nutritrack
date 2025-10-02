package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    // Funciona Localmente pero no afecta a la tabla categories en Supabase
    @PostMapping
    public ResponseEntity<MealResponse> createMeal(
            @PathVariable UUID userId,
            @Valid @RequestBody MealRequest request) {
        Meal meal = mapRequestToMeal(request);
        meal.setUserId(userId);
        Meal saved = mealService.registerMeal(meal);
        return ResponseEntity.ok(MealMapper.toResponse(saved));
    }

    // OK 200 pero funciona con datos locales SQL y no con el SUPABASE
    @GetMapping
    public ResponseEntity<List<MealResponse>> getMealsByDate(
            @PathVariable UUID userId,
            @RequestParam String date) {
        List<Meal> meals = mealService.getMealsByDate(userId, LocalDate.parse(date));
        List<MealResponse> responses = meals.stream()
                .map(MealMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }


    // Si edita, OK 200 pero no afecta a la tabla meals en Supabase
    @PutMapping("/{mealId}")
    public ResponseEntity<MealResponse> updateMeal(
            @PathVariable UUID userId,
            @PathVariable UUID mealId,
            @Valid @RequestBody MealRequest request) {
        Meal updated = mapRequestToMeal(request);
        return mealService.updateMeal(userId, mealId, updated)
                .map(m -> ResponseEntity.ok(MealMapper.toResponse(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    // OK 200 pero no afecta a la tabla meals en Supabase
    @DeleteMapping("/{mealId}")
    public ResponseEntity<?> deleteMeal(
            @PathVariable UUID userId,
            @PathVariable UUID mealId) {
        mealService.deleteMeal(userId, mealId);
        return ResponseEntity.ok().body("{\"status\":\"success\",\"message\":\"Meal deleted\"}");
    }

    private Meal mapRequestToMeal(MealRequest request) {
        Meal meal = new Meal();
        meal.setMealType(MealType.valueOf(request.getMealType().toUpperCase()));
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProteinG(request.getProteinG());
        meal.setCarbsG(request.getCarbsG());
        meal.setFatG(request.getFatG());
        meal.setLoggedAt(request.getLoggedAt());
        meal.setNote(request.getNote());
        return meal;
    }
}
