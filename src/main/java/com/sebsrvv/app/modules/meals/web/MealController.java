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
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final Logger log = LoggerFactory.getLogger(MealController.class);
    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    public ResponseEntity<MealResponse> createMeal(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MealRequest request
    ) {
        if (jwt == null || jwt.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        MealResponse created = mealService.createMeal(jwt.getSubject(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<MealResponse>> listMeals(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (jwt == null || jwt.getSubject() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Instant iFrom = (from == null) ? Instant.EPOCH : from.atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant iTo = (to == null) ? Instant.now() : to.atStartOfDay().plusDays(1).minusSeconds(1).toInstant(ZoneOffset.UTC);
        List<MealResponse> list = mealService.getMealsForUserBetween(jwt.getSubject(), iFrom, iTo);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/{mealId}")
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

    @DeleteMapping("/{mealId}")
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
}
