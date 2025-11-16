package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller que expone dos formas de acceder a meals:
 *  - /api/meals (usa userId del JWT)
 *  - /api/users/{userId}/meals (usa el userId de la ruta; útil para admin/compatibilidad)
 *
 * Ambas formas delegan al mismo MealService.
 */
@RestController
@RequestMapping
public class MealController {

    private final Logger log = LoggerFactory.getLogger(MealController.class);
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    // -------------------------
    // Crear meal (ruta principal)
    // POST /api/meals
    // -------------------------
    @PostMapping("/api/meals")
    public ResponseEntity<MealResponse> createMeal(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MealRequest request
    ) {
        log.info("createMeal (jwt-subject) called. jwt present? {}", jwt != null ? jwt.getSubject() : "no-jwt");
        if (jwt == null || jwt.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MealResponse created = mealService.createMeal(jwt.getSubject(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // -------------------------
    // Crear meal (ruta compatible con frontend que incluye userId en path)
    // POST /api/users/{userId}/meals
    // -------------------------
    @PostMapping("/api/users/{userId}/meals")
    public ResponseEntity<MealResponse> createMealForUserPath(
            @PathVariable("userId") String userIdPath,
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MealRequest request
    ) {
        // Opción de seguridad: si quieres forzar que el userIdPath sea igual al subject del token,
        // descomenta la verificación. Por ahora lo permitimos (útil para admins o testing).
        log.info("createMealForUserPath called. pathUser={}, jwt-subject={}", userIdPath, jwt != null ? jwt.getSubject() : "no-jwt");

        // Si quieres exigir autenticación del token:
        if (jwt == null || jwt.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Si quieres forzar que solo el propio usuario pueda crear usando su id:
        // if (!userIdPath.equals(jwt.getSubject())) { return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); }

        MealResponse created = mealService.createMeal(userIdPath, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // -------------------------
    // Listar meals (por JWT subject)
    // GET /api/meals?from=...&to=...
    // -------------------------
    @GetMapping("/api/meals")
    public ResponseEntity<List<MealResponse>> listMeals(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (jwt == null || jwt.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Instant fromInst = from == null ? null : from.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
        Instant toInst = to == null ? null : to.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
        List<MealResponse> list = mealService.getMealsForUserBetween(jwt.getSubject(), fromInst, toInst);
        return ResponseEntity.ok(list);
    }

    // -------------------------
    // Listar meals (ruta con userId)
    // GET /api/users/{userId}/meals
    // -------------------------
    @GetMapping("/api/users/{userId}/meals")
    public ResponseEntity<List<MealResponse>> listMealsForUserPath(
            @PathVariable("userId") String userIdPath,
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        // autenticación requerida
        if (jwt == null || jwt.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        // si quieres restringir y solo permitir al propio usuario:
        // if (!userIdPath.equals(jwt.getSubject())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        Instant fromInst = from == null ? null : from.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
        Instant toInst = to == null ? null : to.atStartOfDay().toInstant(java.time.ZoneOffset.UTC);
        List<MealResponse> list = mealService.getMealsForUserBetween(userIdPath, fromInst, toInst);
        return ResponseEntity.ok(list);
    }

    // -------------------------
    // Update y Delete: puedes añadir rutas /api/users/{userId}/meals/{mealId} si necesitas la compatibilidad
    // Aquí dejo las rutas principales basadas en /api/meals
    // -------------------------

    @PutMapping("/api/meals/{mealId}")
    public ResponseEntity<MealResponse> updateMeal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId,
            @Valid @RequestBody MealRequest request
    ) {
        if (jwt == null || jwt.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MealResponse updated = mealService.updateMeal(mealId, jwt.getSubject(), request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/meals/{mealId}")
    public ResponseEntity<Void> deleteMeal(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId
    ) {
        if (jwt == null || jwt.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        mealService.deleteMeal(mealId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    // Compatible delete/update by path userId if needed (optional)
    @PutMapping("/api/users/{userId}/meals/{mealId}")
    public ResponseEntity<MealResponse> updateMealForUserPath(
            @PathVariable("userId") String userIdPath,
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId,
            @Valid @RequestBody MealRequest request
    ) {
        if (jwt == null || jwt.getSubject() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        // optional guard: if (!userIdPath.equals(jwt.getSubject())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        MealResponse updated = mealService.updateMeal(mealId, userIdPath, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/users/{userId}/meals/{mealId}")
    public ResponseEntity<Void> deleteMealForUserPath(
            @PathVariable("userId") String userIdPath,
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId
    ) {
        if (jwt == null || jwt.getSubject() == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        mealService.deleteMeal(mealId, userIdPath);
        return ResponseEntity.noContent().build();
    }
}
