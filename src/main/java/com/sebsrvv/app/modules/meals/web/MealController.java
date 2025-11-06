package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.web.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;
    private final MealMapper mealMapper;

    public MealController(MealService mealService, MealMapper mealMapper) {
        this.mealService = mealService;
        this.mealMapper = mealMapper;
    }

    // 📌 Crear una comida
    @PostMapping
    public ResponseEntity<MealResponse> createMeal(@RequestBody MealRequest request) {
        var meal = mealService.createMeal(request);
        return ResponseEntity.ok(mealMapper.toResponse(meal));
    }

    // 📌 Listar comidas por usuario y fecha
    @GetMapping("/{userId}/date/{date}")
    public ResponseEntity<List<MealResponse>> getMealsByUserAndDate(
            @PathVariable UUID userId,
            @PathVariable String date
    ) {
        var meals = mealService.getMealsByUserAndDate(userId, date);
        return ResponseEntity.ok(mealMapper.toResponseList(meals));
    }

    // 📌 Obtener comidas entre fechas
    @PostMapping("/range")
    public ResponseEntity<List<MealResponse>> getMealsBetweenDates(
            @RequestBody FoodCategoryBreakdownRequest request
    ) {
        var meals = mealService.getMealsBetweenDates(request.getUserId(), request.getFrom(), request.getTo());
        return ResponseEntity.ok(mealMapper.toResponseList(meals));
    }

    // 📌 Obtener resumen por categoría
    @PostMapping("/summary")
    public ResponseEntity<List<FoodCategoryBreakdownResponse>> getCategoryBreakdown(
            @RequestBody FoodCategoryBreakdownRequest request
    ) {
        var summary = mealService.getFoodCategoryBreakdown(request);
        return ResponseEntity.ok(summary);
    }
}
