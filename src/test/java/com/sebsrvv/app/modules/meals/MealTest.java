package com.sebsrvv.app.modules.meals;

import com.sebsrvv.app.modules.meals.application.MealService;
import com.sebsrvv.app.modules.meals.domain.FoodCategory;
import com.sebsrvv.app.modules.meals.domain.FoodCategoryRepository;
import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.exception.FoodCategoryNotFoundException;
import com.sebsrvv.app.modules.meals.exception.InvalidMealException;
import com.sebsrvv.app.modules.meals.exception.MealNotFoundException;
import com.sebsrvv.app.modules.meals.web.dto.CreateMealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import com.sebsrvv.app.modules.meals.web.dto.UpdateMealRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MealService - Pruebas Unitarias")
class MealTest {

    @Mock private MealRepository mealRepository;
    @Mock private FoodCategoryRepository foodCategoryRepository;

    @InjectMocks private MealService mealService;

    // ---------- Helpers ----------

    private Jwt buildJwt(UUID userId, String email) {
        return Jwt.withTokenValue("token")
                .subject(userId.toString())
                .header("alg", "none")
                .claim("email", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private FoodCategory category(Integer id, String name) {
        FoodCategory c = new FoodCategory();
        c.setId(id);
        c.setName(name);
        return c;
    }

    private Meal meal(UUID id, UUID userId) {
        Meal m = new Meal();
        m.setId(id);
        m.setUserId(userId);
        m.setDescription("Comida inicial");
        m.setCalories(500);
        m.setProteinGrams(BigDecimal.TEN);
        m.setCarbsGrams(BigDecimal.TEN);
        m.setFatGrams(BigDecimal.TEN);
        m.setMealType(MealType.breakfast);
        m.setLoggedAt(OffsetDateTime.now(ZoneOffset.UTC));
        m.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        m.setCategories(new HashSet<>());
        return m;
    }

    // ---------- Tests ----------

    @Test
    @DisplayName("Debe crear un meal correctamente")
    void create_ValidData_CreatesMeal() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);

        CreateMealRequest request = new CreateMealRequest(
                "Pollo con arroz",
                650,
                BigDecimal.valueOf(45.5),
                BigDecimal.valueOf(60),
                BigDecimal.valueOf(18),
                "lunch",
                now,
                Set.of(1)
        );

        FoodCategory cat = category(1, "Proteína");
        when(foodCategoryRepository.findAllById(Set.of(1)))
                .thenReturn(List.of(cat));

        when(mealRepository.save(any(Meal.class)))
                .thenAnswer(inv -> {
                    Meal m = inv.getArgument(0, Meal.class);
                    if (m.getId() == null) {
                        m.setId(UUID.randomUUID());
                    }
                    return m;
                });

        // Act
        MealResponse response = mealService.create(jwt, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.description()).isEqualTo("Pollo con arroz");
        assertThat(response.calories()).isEqualTo(650);
        assertThat(response.mealType()).isEqualTo(MealType.lunch.name());
        assertThat(response.categoryIds()).containsExactly(1);

        ArgumentCaptor<Meal> captor = ArgumentCaptor.forClass(Meal.class);
        verify(mealRepository).save(captor.capture());
        Meal saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getMealType()).isEqualTo(MealType.lunch);
        assertThat(saved.getCategories()).extracting(FoodCategory::getId).containsExactly(1);
    }

    @Test
    @DisplayName("Debe lanzar InvalidMealException si las calorías son negativas")
    void create_NegativeCalories_ThrowsInvalidMealException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");

        CreateMealRequest request = new CreateMealRequest(
                "Algo",
                -10,
                null, null, null,
                "lunch",
                null,
                null
        );

        // Act / Assert
        assertThatThrownBy(() -> mealService.create(jwt, request))
                .isInstanceOf(InvalidMealException.class)
                .hasMessageContaining("calories");

        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Debe lanzar FoodCategoryNotFoundException si faltan categorías")
    void create_MissingCategory_ThrowsFoodCategoryNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");

        CreateMealRequest request = new CreateMealRequest(
                "Pollo con arroz",
                650,
                null, null, null,
                "lunch",
                null,
                Set.of(1, 2)
        );

        // solo devuelve una categoría
        when(foodCategoryRepository.findAllById(Set.of(1, 2)))
                .thenReturn(List.of(category(1, "Cat1")));

        // Act / Assert
        assertThatThrownBy(() -> mealService.create(jwt, request))
                .isInstanceOf(FoodCategoryNotFoundException.class);

        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Debe actualizar un meal correctamente")
    void update_ValidData_UpdatesMeal() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");
        UUID mealId = UUID.randomUUID();

        Meal existing = meal(mealId, userId);
        when(mealRepository.findByIdAndUserId(mealId, userId))
                .thenReturn(Optional.of(existing));
        when(foodCategoryRepository.findAllById(Set.of(1)))
                .thenReturn(List.of(category(1, "Cat1")));
        when(mealRepository.save(any(Meal.class)))
                .thenAnswer(inv -> inv.getArgument(0, Meal.class));

        UpdateMealRequest request = new UpdateMealRequest(
                "Nuevo desc",
                700,
                BigDecimal.valueOf(50),
                null,
                null,
                "dinner",
                null,
                Set.of(1)
        );

        // Act
        MealResponse response = mealService.update(jwt, mealId, request);

        // Assert
        assertThat(response.id()).isEqualTo(mealId);
        assertThat(response.description()).isEqualTo("Nuevo desc");
        assertThat(response.calories()).isEqualTo(700);
        assertThat(response.mealType()).isEqualTo(MealType.dinner.name());
        assertThat(response.categoryIds()).containsExactly(1);

        verify(mealRepository).findByIdAndUserId(mealId, userId);
        verify(mealRepository).save(any(Meal.class));
    }

    @Test
    @DisplayName("Debe lanzar MealNotFoundException al actualizar si el meal no existe o no es del usuario")
    void update_NotFound_ThrowsMealNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");
        UUID mealId = UUID.randomUUID();

        when(mealRepository.findByIdAndUserId(mealId, userId))
                .thenReturn(Optional.empty());

        UpdateMealRequest request = new UpdateMealRequest(
                "Nuevo desc",
                700,
                null, null, null,
                "dinner",
                null,
                null
        );

        // Act / Assert
        assertThatThrownBy(() -> mealService.update(jwt, mealId, request))
                .isInstanceOf(MealNotFoundException.class);

        verify(mealRepository).findByIdAndUserId(mealId, userId);
        verify(mealRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe obtener un meal por id cuando pertenece al usuario")
    void getOne_Valid_ReturnsMeal() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");
        UUID mealId = UUID.randomUUID();

        Meal existing = meal(mealId, userId);
        when(mealRepository.findByIdAndUserId(mealId, userId))
                .thenReturn(Optional.of(existing));

        // Act
        MealResponse response = mealService.getOne(jwt, mealId);

        // Assert
        assertThat(response.id()).isEqualTo(mealId);
        assertThat(response.userId()).isEqualTo(userId);
        assertThat(response.description()).isEqualTo("Comida inicial");

        verify(mealRepository).findByIdAndUserId(mealId, userId);
    }

    @Test
    @DisplayName("Debe lanzar MealNotFoundException si no existe el meal al obtener por id")
    void getOne_NotFound_ThrowsMealNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");
        UUID mealId = UUID.randomUUID();

        when(mealRepository.findByIdAndUserId(mealId, userId))
                .thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> mealService.getOne(jwt, mealId))
                .isInstanceOf(MealNotFoundException.class);

        verify(mealRepository).findByIdAndUserId(mealId, userId);
    }

    @Test
    @DisplayName("Debe lanzar InvalidMealException si from/to son nulos en getByDateRange")
    void getByDateRange_NullDates_ThrowsInvalidMealException() {
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");

        assertThatThrownBy(() -> mealService.getByDateRange(jwt, null, LocalDate.now()))
                .isInstanceOf(InvalidMealException.class);

        assertThatThrownBy(() -> mealService.getByDateRange(jwt, LocalDate.now(), null))
                .isInstanceOf(InvalidMealException.class);

        verifyNoInteractions(mealRepository);
    }

    @Test
    @DisplayName("Debe devolver meals en un rango de fechas")
    void getByDateRange_Valid_ReturnsMeals() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");
        LocalDate from = LocalDate.of(2025, 11, 1);
        LocalDate to = LocalDate.of(2025, 11, 30);

        Meal m = meal(UUID.randomUUID(), userId);
        when(mealRepository.findByUserIdAndLoggedAtBetween(any(), any(), any()))
                .thenReturn(List.of(m));

        // Act
        var result = mealService.getByDateRange(jwt, from, to);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).userId()).isEqualTo(userId);

        verify(mealRepository).findByUserIdAndLoggedAtBetween(eq(userId), any(), any());
    }

    @Test
    @DisplayName("Debe eliminar un meal cuando pertenece al usuario")
    void delete_Valid_DeletesMeal() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");
        UUID mealId = UUID.randomUUID();

        Meal existing = meal(mealId, userId);
        when(mealRepository.findByIdAndUserId(mealId, userId))
                .thenReturn(Optional.of(existing));

        // Act
        mealService.delete(jwt, mealId);

        // Assert
        verify(mealRepository).deleteByIdAndUserId(mealId, userId);
    }

    @Test
    @DisplayName("Debe lanzar MealNotFoundException al eliminar si el meal no existe o no es del usuario")
    void delete_NotFound_ThrowsMealNotFoundException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        Jwt jwt = buildJwt(userId, "user@test.com");
        UUID mealId = UUID.randomUUID();

        when(mealRepository.findByIdAndUserId(mealId, userId))
                .thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> mealService.delete(jwt, mealId))
                .isInstanceOf(MealNotFoundException.class);

        verify(mealRepository).findByIdAndUserId(mealId, userId);
        verify(mealRepository, never()).deleteByIdAndUserId(any(), any());
    }
}
