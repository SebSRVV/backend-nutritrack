package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.MealLog;
import com.sebsrvv.app.modules.meals.infra.MealRepositoryImpl;
import com.sebsrvv.app.modules.meals.domain.MealJpaRepository;
import com.sebsrvv.app.modules.meals.exception.InvalidMealDataException;
import com.sebsrvv.app.modules.meals.exception.MealNotFoundException;
import com.sebsrvv.app.modules.meals.exception.UnauthorizedMealAccessException;
import com.sebsrvv.app.modules.meals.web.MealMapper;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MealService {

    private final MealJpaRepository mealRepository;
    private final MealRepositoryImpl mealRepositoryImpl;
    private final MealMapper mapper;

    public MealService(MealJpaRepository mealRepository, MealRepositoryImpl mealRepositoryImpl, MealMapper mapper) {
        this.mealRepository = mealRepository;
        this.mealRepositoryImpl = mealRepositoryImpl;
        this.mapper = mapper;
    }

    public MealResponse createMeal(String userId, MealRequest request) {
        if (userId == null) throw new UnauthorizedMealAccessException("Usuario no autenticado");
        if (request == null) throw new InvalidMealDataException("Request vacío");
        MealLog entity = mapper.toEntity(userId, request);
        if (entity.getLoggedAt() == null) entity.setLoggedAt(Instant.now());
        MealLog saved = mealRepository.save(entity);
        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<MealResponse> getMealsForUserBetween(String userId, Instant from, Instant to) {
        if (userId == null) throw new UnauthorizedMealAccessException("Usuario no autenticado");
        List<MealLog> meals = mealRepositoryImpl.findMealsBetweenInstants(userId, from, to);
        return meals.stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    public MealResponse updateMeal(Long mealId, String userId, MealRequest request) {
        if (userId == null) throw new UnauthorizedMealAccessException("Usuario no autenticado");
        MealLog existing = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));
        if (!userId.equals(existing.getUserId())) {
            throw new UnauthorizedMealAccessException("No tienes permiso para modificar esta meal");
        }
        mapper.updateEntityFromRequest(existing, request);
        MealLog saved = mealRepository.save(existing);
        return mapper.toResponse(saved);
    }

    public void deleteMeal(Long mealId, String userId) {
        if (userId == null) throw new UnauthorizedMealAccessException("Usuario no autenticado");
        MealLog existing = mealRepository.findById(mealId)
                .orElseThrow(() -> new MealNotFoundException("Meal no encontrado con ID: " + mealId));
        if (!userId.equals(existing.getUserId())) {
            throw new UnauthorizedMealAccessException("No tienes permiso para eliminar esta meal.");
        }
        mealRepository.delete(existing);
    }
}
