// src/main/java/com/sebsrvv/app/modules/meals/web/MealController.java
package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controller REST para Meals.
 * Obtiene userId desde el JWT (subject) para evitar que el cliente proporcione userId en el body.
 */
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
    public ResponseEntity<MealResponse> createMeal(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MealRequest request) {

        UUID userId = UUID.fromString(jwt.getSubject());
        MealResponse created = mealService.createMeal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Listar meals del usuario
    @GetMapping
    public ResponseEntity<List<MealResponse>> getAllMeals(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        List<MealResponse> meals = mealService.getAllMeals(userId);
        return ResponseEntity.ok(meals);
    }

    // Obtener un meal por id (propio)
    @GetMapping("/{mealId}")
    public ResponseEntity<MealResponse> getMeal(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable UUID mealId) {
        UUID userId = UUID.fromString(jwt.getSubject());
        MealResponse res = mealService.getMeal(mealId, userId);
        return ResponseEntity.ok(res);
    }

    // Actualizar meal
    @PutMapping("/{mealId}")
    public ResponseEntity<MealResponse> updateMeal(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable UUID mealId,
                                                   @Valid @RequestBody MealRequest request) {
        UUID userId = UUID.fromString(jwt.getSubject());
        MealResponse updated = mealService.updateMeal(mealId, userId, request);
        return ResponseEntity.ok(updated);
    }

    // Eliminar meal
    @DeleteMapping("/{mealId}")
    public ResponseEntity<Void> deleteMeal(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable UUID mealId) {
        UUID userId = UUID.fromString(jwt.getSubject());
        mealService.deleteMeal(mealId, userId);
        return ResponseEntity.noContent().build();
    }
}
