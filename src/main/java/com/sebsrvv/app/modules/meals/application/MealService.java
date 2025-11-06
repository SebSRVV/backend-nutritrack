package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import com.sebsrvv.app.modules.meals.web.mapper.MealMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final MealMapper mealMapper;

    public MealService(MealRepository mealRepository, MealMapper mealMapper) {
        this.mealRepository = mealRepository;
        this.mealMapper = mealMapper;
    }

    /**
     * ✅ Guarda una nueva comida registrada por el usuario.
     */
    public MealResponse createMeal(MealRequest request) {
        Meal meal = mealMapper.toEntity(request);

        // Validar que la fecha no sea futura
        if (meal.getLoggedAt().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de registro no puede ser futura.");
        }

        // Validar tipo de comida
        try {
            MealType.valueOf(request.mealType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de comida inválido. Usa BREAKFAST, LUNCH, DINNER o SNACK.");
        }

        Meal savedMeal = mealRepository.save(meal);
        return mealMapper.toResponse(savedMeal);
    }

    /**
     * ✅ Obtiene todas las comidas de un usuario en una fecha específica.
     */
    @Transactional(readOnly = true)
    public List<MealResponse> getMealsByUserAndDate(UUID userId, LocalDate date) {
        List<Meal> meals = mealRepository.findByUserIdAndLoggedAt(userId, date);
        return mealMapper.toResponseList(meals);
    }

    /**
     * ✅ Obtiene todas las comidas de un usuario dentro de un rango de fechas.
     */
    @Transactional(readOnly = true)
    public List<MealResponse> getMealsBetweenDates(UUID userId, LocalDate from, LocalDate to) {
        List<Meal> meals = mealRepository.findMealsBetweenDates(userId, from, to);
        return mealMapper.toResponseList(meals);
    }

    /**
     * ✅ Actualiza una comida existente.
     */
    public MealResponse updateMeal(UUID id, MealRequest request) {
        Optional<Meal> optionalMeal = mealRepository.findById(id);
        if (optionalMeal.isEmpty()) {
            throw new IllegalArgumentException("No se encontró la comida con ID: " + id);
        }

        Meal meal = optionalMeal.get();
        meal.setName(request.name());
        meal.setCalories(request.calories());
        meal.setLoggedAt(request.loggedAt());
        meal.setNote(request.note());

        // Validar el tipo de comida actualizado
        try {
            meal.setMealType(MealType.valueOf(request.mealType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Tipo de comida inválido. Usa BREAKFAST, LUNCH, DINNER o SNACK.");
        }

        Meal updated = mealRepository.save(meal);
        return mealMapper.toResponse(updated);
    }

    /**
     * ✅ Elimina una comida por su ID.
     */
    public void deleteMeal(UUID id) {
        if (!mealRepository.existsById(id)) {
            throw new IllegalArgumentException("No existe una comida con ID: " + id);
        }
        mealRepository.deleteById(id);
    }

    /**
     * ✅ Obtiene los detalles de una comida específica por ID.
     */
    @Transactional(readOnly = true)
    public MealResponse getMealById(UUID id) {
        Meal meal = mealRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comida no encontrada con ID: " + id));
        return mealMapper.toResponse(meal);
    }

    /**
     * ✅ Lista todas las comidas registradas (solo para debug o admin).
     */
    @Transactional(readOnly = true)
    public List<MealResponse> getAllMeals() {
        List<Meal> meals = mealRepository.findAll();
        return mealMapper.toResponseList(meals);
    }
}
