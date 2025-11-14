// src/test/java/com/sebsrvv/app/modules/meals/application/MealServiceTest.java
package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.exception.MealNotFoundException;
import com.sebsrvv.app.modules.meals.exception.UnauthorizedMealAccessException;
import com.sebsrvv.app.modules.meals.web.MealMapper;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @InjectMocks
    private MealService mealService;

    private MealMapper mapper = new MealMapper();

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mealService = new MealService(mealRepository, mapper);
    }

    private MealRequest buildRequest() {
        MealRequest req = new MealRequest();
        req.setMealType(MealType.BREAKFAST);
        req.setDescription("Huevos con pan");
        req.setCalories(300.0);
        req.setProtein_g(20.0);
        req.setCarbs_g(25.0);
        req.setFat_g(10.0);
        req.setLoggedAt(Instant.now());
        return req;
    }

    @Test
    void createMeal_ok() {
        UUID userId = UUID.randomUUID();
        MealRequest req = buildRequest();

        Meal saved = mapper.toEntity(userId, req);
        saved.setId(UUID.randomUUID());

        when(mealRepository.save(any(Meal.class))).thenReturn(saved);

        var response = mealService.createMeal(userId, req);

        assertThat(response).isNotNull();
        assertThat(response.getDescription()).isEqualTo("Huevos con pan");
        verify(mealRepository).save(any(Meal.class));
    }

    @Test
    void updateMeal_throwsMealNotFound() {
        UUID userId = UUID.randomUUID();
        UUID mealId = UUID.randomUUID();

        when(mealRepository.findById(mealId)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                mealService.updateMeal(mealId, userId, buildRequest())
        ).isInstanceOf(MealNotFoundException.class);
    }

    @Test
    void updateMeal_throwsUnauthorized() {
        UUID userId = UUID.randomUUID();
        UUID anotherUser = UUID.randomUUID();
        UUID mealId = UUID.randomUUID();

        Meal meal = new Meal();
        meal.setId(mealId);
        meal.setUserId(anotherUser);

        when(mealRepository.findById(mealId)).thenReturn(Optional.of(meal));

        assertThatThrownBy(() ->
                mealService.updateMeal(mealId, userId, buildRequest())
        ).isInstanceOf(UnauthorizedMealAccessException.class);
    }

    @Test
    void deleteMeal_ok() {
        UUID userId = UUID.randomUUID();
        UUID mealId = UUID.randomUUID();

        Meal m = new Meal();
        m.setId(mealId);
        m.setUserId(userId);

        when(mealRepository.findById(mealId)).thenReturn(Optional.of(m));

        mealService.deleteMeal(mealId, userId);

        verify(mealRepository).delete(m);
    }
}
