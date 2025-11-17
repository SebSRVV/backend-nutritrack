package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.web.dto.CreateMealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import com.sebsrvv.app.modules.meals.web.dto.UpdateMealRequest;
import com.sebsrvv.app.modules.meals.domain.Meal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/meals")
public class MealController {

    private final MealService mealService;

    public MealController(MealService mealService) {
        this.mealService = mealService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MealResponse create(@RequestBody CreateMealRequest request) {
        var command = new MealService.CreateMealCommand(
                request.userId(),
                request.description(),
                request.calories(),
                request.proteinGrams(),
                request.carbsGrams(),
                request.fatGrams(),
                request.mealType(),
                request.loggedAt(),
                request.categoryIds()
        );

        Meal meal = mealService.createMeal(command);
        return toResponse(meal);
    }

    @PutMapping("/{mealId}")
    public MealResponse update(@PathVariable UUID mealId,
                               @RequestParam UUID userId,
                               @RequestBody UpdateMealRequest request) {

        var command = new MealService.UpdateMealCommand(
                mealId,
                userId,
                request.description(),
                request.calories(),
                request.proteinGrams(),
                request.carbsGrams(),
                request.fatGrams(),
                request.mealType(),
                request.loggedAt(),
                request.categoryIds()
        );

        Meal meal = mealService.updateMeal(command);
        return toResponse(meal);
    }

    @DeleteMapping("/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID mealId,
                       @RequestParam UUID userId) {
        mealService.deleteMeal(mealId, userId);
    }

    @GetMapping("/{mealId}")
    public MealResponse getOne(@PathVariable UUID mealId,
                               @RequestParam UUID userId) {
        Meal meal = mealService.getMeal(mealId, userId);
        return toResponse(meal);
    }

    @GetMapping
    public List<MealResponse> getByDateRange(
            @RequestParam UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return mealService.getMeals(userId, from, to).stream()
                .map(this::toResponse)
                .toList();
    }

    // -------- mapper simple dominio -> response --------

    private MealResponse toResponse(Meal meal) {
        return new MealResponse(
                meal.getId(),
                meal.getUserId(),
                meal.getDescription(),
                meal.getCalories(),
                meal.getProteinGrams(),
                meal.getCarbsGrams(),
                meal.getFatGrams(),
                meal.getMealType().name(),
                meal.getLoggedAt(),
                meal.getCreatedAt(),
                meal.getCategoryIds()
        );
    }
}

