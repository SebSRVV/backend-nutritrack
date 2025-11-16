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

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    // Crear meal usando el userId del token (Authorization)
    @PostMapping("/meals")
    public ResponseEntity<MealResponse> createMealAuth(@AuthenticationPrincipal Jwt jwt,
                                                       @Valid @RequestBody MealRequest request) {
        String userId = jwt.getSubject();
        MealResponse created = mealService.createMeal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Crear meal para userId en la URL (UUID). Solo si token.sub == userId (seguridad mínima)
    @PostMapping("/users/{userId}/meals")
    public ResponseEntity<MealResponse> createMealForUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String userId,
            @Valid @RequestBody MealRequest request) {

        String tokenSub = jwt != null ? jwt.getSubject() : null;
        if (tokenSub == null || !tokenSub.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        MealResponse created = mealService.createMeal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Listar meals del usuario por userId en URL (UUID)
    @GetMapping("/users/{userId}/meals")
    public ResponseEntity<List<MealResponse>> getMealsForUser(@AuthenticationPrincipal Jwt jwt,
                                                              @PathVariable String userId) {
        String tokenSub = jwt != null ? jwt.getSubject() : null;
        if (tokenSub == null || !tokenSub.equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        List<MealResponse> meals = mealService.getAllMeals(userId);
        return ResponseEntity.ok(meals);
    }

    // Obtener, actualizar y eliminar por mealId (usa token.sub para comparar dueño)
    @GetMapping("/meals/{mealId}")
    public ResponseEntity<MealResponse> getMeal(@AuthenticationPrincipal Jwt jwt,
                                                @PathVariable Long mealId) {
        String userId = jwt.getSubject();
        MealResponse res = mealService.getMeal(mealId, userId);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/meals/{mealId}")
    public ResponseEntity<MealResponse> updateMeal(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable Long mealId,
                                                   @Valid @RequestBody MealRequest request) {
        String userId = jwt.getSubject();
        MealResponse updated = mealService.updateMeal(mealId, userId, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/meals/{mealId}")
    public ResponseEntity<Void> deleteMeal(@AuthenticationPrincipal Jwt jwt,
                                           @PathVariable Long mealId) {
        String userId = jwt.getSubject();
        mealService.deleteMeal(mealId, userId);
        return ResponseEntity.noContent().build();
    }
}
