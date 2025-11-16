package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Validated
public class MealController {

    private static final Logger log = LoggerFactory.getLogger(MealController.class);

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    /**
     * Crear meal usando el userId del token (Authorization).
     * Consumimos/Producimos JSON explícito para evitar problemas de Content-Type en Insomnia/Postman.
     */
    @PostMapping(value = "/meals", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MealResponse> createMealAuth(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MealRequest request
    ) {
        if (jwt == null || jwt.getSubject() == null) {
            log.warn("Token ausente o inválido en createMealAuth");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }

        String userId = jwt.getSubject();
        log.debug("createMealAuth userId={} request={}", userId, request);

        MealResponse created = mealService.createMeal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Crear meal para userId en la URL. Verificamos que token.sub == userId.
     */
    @PostMapping(value = "/users/{userId}/meals", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MealResponse> createMealForUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String userId,
            @Valid @RequestBody MealRequest request
    ) {
        String tokenSub = jwt != null ? jwt.getSubject() : null;
        log.debug("createMealForUser tokenSub={} userId={} request={}", tokenSub, userId, request);

        if (tokenSub == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o ausente");
        }
        if (!Objects.equals(tokenSub, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No estás autorizado para crear meals para este usuario");
        }

        MealResponse created = mealService.createMeal(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Listar meals del usuario por userId en URL (UUID).
     */
    @GetMapping(value = "/users/{userId}/meals", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MealResponse>> getMealsForUser(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String userId
    ) {
        String tokenSub = jwt != null ? jwt.getSubject() : null;
        log.debug("getMealsForUser tokenSub={} userId={}", tokenSub, userId);

        if (tokenSub == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o ausente");
        }
        if (!Objects.equals(tokenSub, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No estás autorizado para ver meals de este usuario");
        }

        List<MealResponse> meals = mealService.getAllMeals(userId);
        return ResponseEntity.ok(meals);
    }

    /**
     * Obtener meal por mealId (usa token.sub para comparar dueño).
     */
    @GetMapping(value = "/meals/{mealId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MealResponse> getMeal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId
    ) {
        if (jwt == null || jwt.getSubject() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o ausente");
        }
        String userId = jwt.getSubject();
        log.debug("getMeal userId={} mealId={}", userId, mealId);

        MealResponse res = mealService.getMeal(mealId, userId);
        return ResponseEntity.ok(res);
    }

    /**
     * Actualizar meal — validamos request (campo description no nulo gracias a @Valid).
     */
    @PutMapping(value = "/meals/{mealId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MealResponse> updateMeal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId,
            @Valid @RequestBody MealRequest request
    ) {
        if (jwt == null || jwt.getSubject() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o ausente");
        }
        String userId = jwt.getSubject();
        log.debug("updateMeal userId={} mealId={} request={}", userId, mealId, request);

        MealResponse updated = mealService.updateMeal(mealId, userId, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Eliminar meal.
     */
    @DeleteMapping("/meals/{mealId}")
    public ResponseEntity<Void> deleteMeal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId
    ) {
        if (jwt == null || jwt.getSubject() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido o ausente");
        }
        String userId = jwt.getSubject();
        log.debug("deleteMeal userId={} mealId={}", userId, mealId);

        mealService.deleteMeal(mealId, userId);
        return ResponseEntity.noContent().build();
    }
}
