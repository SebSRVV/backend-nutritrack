package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.web.dto.CreateMealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import com.sebsrvv.app.modules.meals.web.dto.UpdateMealRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService service;

    public MealController(MealService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MealResponse create(@AuthenticationPrincipal Jwt jwt,
                               @RequestBody CreateMealRequest r) {
        return service.create(jwt, r);
    }

    @PatchMapping("/{mealId}")
    public MealResponse update(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable UUID mealId,
                               @RequestBody UpdateMealRequest r) {
        return service.update(jwt, mealId, r);
    }

    @DeleteMapping("/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal Jwt jwt,
                       @PathVariable UUID mealId) {
        service.delete(jwt, mealId);
    }

    @GetMapping("/{mealId}")
    public MealResponse getOne(@AuthenticationPrincipal Jwt jwt,
                               @PathVariable UUID mealId) {
        return service.getOne(jwt, mealId);
    }

    @GetMapping
    public List<MealResponse> getByDateRange(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return service.getByDateRange(jwt, from, to);
    }
}
