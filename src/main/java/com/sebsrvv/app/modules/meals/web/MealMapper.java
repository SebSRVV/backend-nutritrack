package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.MealLog;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class MealMapper {

    /**
     * Convierte DTO -> Entidad (nueva)
     */
    public MealLog toEntity(String userId, MealRequest request) {
        MealLog meal = new MealLog();
        meal.setUserId(userId);
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein_g(request.getProtein_g());
        meal.setCarbs_g(request.getCarbs_g());
        meal.setFat_g(request.getFat_g());
        meal.setMealType(request.getMealType());

        LocalDate ld = request.getLoggedAt();
        if (ld != null) {
            meal.setLoggedAt(ld.atStartOfDay().toInstant(ZoneOffset.UTC));
        } else {
            meal.setLoggedAt(Instant.now());
        }

        List<String> items = request.getMealItems();
        meal.setMealItems(items == null ? new ArrayList<>() : new ArrayList<>(items));

        return meal;
    }

    /**
     * Convierte Entidad -> DTO (nombre esperado por MealService)
     */
    public MealResponse toResponse(MealLog meal) {
        if (meal == null) return null;
        MealResponse dto = new MealResponse();
        dto.setId(meal.getId());
        dto.setDescription(meal.getDescription());
        dto.setCalories(meal.getCalories());
        dto.setProtein_g(meal.getProtein_g());
        dto.setCarbs_g(meal.getCarbs_g());
        dto.setFat_g(meal.getFat_g());
        dto.setMealType(meal.getMealType());

        Instant inst = meal.getLoggedAt();
        if (inst != null) {
            dto.setLoggedAt(LocalDate.ofInstant(inst, ZoneOffset.UTC));
        }

        List<String> items = meal.getMealItems();
        dto.setMealItems(items == null ? new ArrayList<>() : new ArrayList<>(items));
        return dto;
    }

    /**
     * Mantengo también toDto por compatibilidad interna si acaso:
     */
    public MealResponse toDto(MealLog meal) {
        return toResponse(meal);
    }

    /**
     * Actualiza una entidad existente desde el request (sin cambiar userId ni id).
     * Usado en updateMeal.
     */
    public void updateEntityFromRequest(MealLog existing, MealRequest request) {
        if (existing == null || request == null) return;

        // Solo permitir update de campos que el request provee
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }

        // Numbers: permitimos nulos (si null -> no cambiamos)
        if (request.getCalories() != null) {
            existing.setCalories(request.getCalories());
        }
        if (request.getProtein_g() != null) {
            existing.setProtein_g(request.getProtein_g());
        }
        if (request.getCarbs_g() != null) {
            existing.setCarbs_g(request.getCarbs_g());
        }
        if (request.getFat_g() != null) {
            existing.setFat_g(request.getFat_g());
        }

        // MealType
        if (request.getMealType() != null) {
            existing.setMealType(request.getMealType());
        }

        // loggedAt: si viene, convertimos LocalDate -> Instant (start of day UTC)
        if (request.getLoggedAt() != null) {
            existing.setLoggedAt(request.getLoggedAt().atStartOfDay().toInstant(ZoneOffset.UTC));
        }

        // mealItems: si viene nulo, lo dejamos; si viene lista, reemplazamos por copia (mantener consistencia)
        if (request.getMealItems() != null) {
            existing.setMealItems(new ArrayList<>(request.getMealItems()));
        }
    }

}
