package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;

import java.util.stream.Collectors;

/**
 * Clase utilitaria para convertir objetos del dominio (Meal)
 * a objetos de respuesta (MealResponse) que serán devueltos por la API.
 */
public class MealMapper {

    /**
     * Convierte una entidad Meal en un DTO MealResponse.
     *
     * @param meal Objeto del dominio que representa una comida.
     * @return MealResponse DTO listo para ser enviado al cliente.
     */
    public static MealResponse toResponse(Meal meal) {
        // Crea una nueva instancia de respuesta
        MealResponse response = new MealResponse();

        // Asigna los campos básicos de la entidad Meal al DTO
        response.setId(meal.getId());
        response.setUserId(meal.getUserId());
        response.setMealType(meal.getMealType().name().toLowerCase()); // Convierte el enum a minúsculas
        response.setDescription(meal.getDescription());
        response.setCalories(meal.getCalories());
        response.setProteinG(meal.getProteinG());
        response.setCarbsG(meal.getCarbsG());
        response.setFatG(meal.getFatG());
        response.setLoggedAt(meal.getLoggedAt());
        response.setCreatedAt(meal.getCreatedAt());
        response.setNote(meal.getNote());

        // Si la comida tiene categorías asociadas, las convierte a DTOs
        if (meal.getCategories() != null) {
            response.setCategories(
                    meal.getCategories().stream().map(c -> {
                        // Por cada categoría, crea un DTO interno CategoryDto
                        MealResponse.CategoryDto dto = new MealResponse.CategoryDto();
                        dto.setId(c.getId());
                        dto.setName(c.getName());
                        return dto;
                    }).collect(Collectors.toList()) // Convierte el stream en una lista
            );
        }

        // Devuelve el objeto listo para responder al cliente
        return response;
    }
}

