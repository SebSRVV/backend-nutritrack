package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meals")
@CrossOrigin(origins = "*")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    // Crear meal
    @PostMapping
    public ResponseEntity<MealResponse> createMeal(@Valid @RequestBody MealRequest request) {
        MealResponse created = mealService.createMeal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Listar todos los meals
    @GetMapping
    public ResponseEntity<List<MealResponse>> getAllMeals() {
        List<MealResponse> meals = mealService.getAllMeals();
        return ResponseEntity.ok(meals);
    }

    // Actualizar meal
    @PutMapping("/{mealId}")
    public ResponseEntity<MealResponse> updateMeal(@PathVariable UUID mealId,
                                                   @Valid @RequestBody MealRequest request) {
        MealResponse updated = mealService.updateMeal(mealId, request);
        return ResponseEntity.ok(updated);
    }

    // Eliminar meal
    @DeleteMapping("/{mealId}")
    public ResponseEntity<Void> deleteMeal(@PathVariable UUID mealId) {
        mealService.deleteMeal(mealId);
        return ResponseEntity.noContent().build();
    }
}
