package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public ResponseEntity<MealResponse> createMeal(
            @PathVariable UUID userId,
            @Valid @RequestBody MealRequest request,
            @RequestHeader("Authorization") String authorization // ← JWT del usuario
    ) {
        Meal meal = mapRequestToMeal(request);
        meal.setUserId(userId);

        var saved = mealService.registerMeal(
                meal,
                request.getCategoryIds(),
                request.getCategories(),
                authorization
        );

        return ResponseEntity.ok(MealMapper.toResponse(saved));
    }

    @GetMapping
    public ResponseEntity<List<MealResponse>> getMealsByDate(
            @PathVariable UUID userId,
            @RequestParam String date,
            @RequestHeader("Authorization") String authorization
    ) {
        var meals = mealService.getMealsByDate(userId, LocalDate.parse(date), authorization);
        var responses = meals.stream()
                .map(MealMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{mealId}")
    public ResponseEntity<MealResponse> updateMeal(
            @PathVariable UUID userId,
            @PathVariable UUID mealId,
            @Valid @RequestBody MealRequest request,
            @RequestHeader("Authorization") String authorization
    ) {
        Meal updated = mapRequestToMeal(request);

        return mealService.updateMeal(
                        userId,
                        mealId,
                        updated,
                        request.getCategoryIds(),
                        request.getCategories(),
                        authorization
                )
                .map(m -> ResponseEntity.ok(MealMapper.toResponse(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{mealId}")
    public ResponseEntity<?> deleteMeal(
            @PathVariable UUID userId,
            @PathVariable UUID mealId,
            @RequestHeader("Authorization") String authorization
    ) {
        try {
            mealService.deleteMeal(userId, mealId, authorization);
            return ResponseEntity.ok().body("{\"status\":\"success\",\"message\":\"Meal deleted\"}");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("{\"status\":\"error\",\"message\":\"" + ex.getMessage() + "\"}");
        }
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
