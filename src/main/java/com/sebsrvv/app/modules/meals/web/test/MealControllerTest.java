package com.sebsrvv.app.modules.meals.web.test;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.domain.MealType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class MealControllerTest {

    @Autowired
    private MealService mealService;

    @Test
    public void testCreateMeal() {
        MealRequest request = new MealRequest();
        request.setUserId(UUID.fromString("641ef3e1-9d56-4487-8e1e-d89733103ed0"));
        request.setName("Test Meal");
        request.setMealType(MealType.LUNCH);
        request.setCalories(500.0);
        request.setNote("Testing meal creation");
        request.setLoggedAt(LocalDate.now());
        request.setCategoryId(null); // O puedes poner un UUID válido

        mealService.createMeal(request);

        System.out.println("✅ Prueba de creación de Meal ejecutada correctamente.");
    }
}
