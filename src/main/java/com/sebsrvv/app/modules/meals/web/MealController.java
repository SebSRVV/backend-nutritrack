package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para gestionar las comidas (Meals) asociadas a un usuario.
 */
@RestController
@RequestMapping("/api/users/{userId}/meals")
@CrossOrigin(origins = "*") // Permite peticiones desde el front o Insomnia
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    // Crear un nuevo meal para un usuario
    @PostMapping
    public ResponseEntity<MealResponse> createMeal(
            @PathVariable UUID userId,
            @Valid @RequestBody MealRequest request) {

        request.setUserId(userId); // Asigna el ID del usuario al request
        MealResponse created = mealService.createMeal(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Obtener meals de un usuario en una fecha específica
    @GetMapping
    public ResponseEntity<List<MealResponse>> getMealsByDate(
            @PathVariable UUID userId,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<MealResponse> meals = mealService.getMealsByDate(userId, date);
        return ResponseEntity.ok(meals);
    }

    //  Obtener meals de un usuario entre dos fechas
    @GetMapping("/range")
    public ResponseEntity<List<MealResponse>> getMealsBetweenDates(
            @PathVariable UUID userId,
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        List<MealResponse> meals = mealService.getMealsBetweenDates(userId, from, to);
        return ResponseEntity.ok(meals);
    }

    // Actualizar un meal existente
    @PutMapping("/{mealId}")
    public ResponseEntity<MealResponse> updateMeal(
            @PathVariable UUID userId,
            @PathVariable UUID mealId,
            @Valid @RequestBody MealRequest request) {

        request.setUserId(userId);
        MealResponse updated = mealService.updateMeal(mealId, request);
        return ResponseEntity.ok(updated);
    }

    // Eliminar un meal
    @DeleteMapping("/{mealId}")
    public ResponseEntity<String> deleteMeal(
            @PathVariable UUID userId,
            @PathVariable UUID mealId) {

        mealService.deleteMeal(mealId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body("Meal eliminado correctamente");
    }
}

