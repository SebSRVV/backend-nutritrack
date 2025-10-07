package com.sebsrvv.app.modules.meals.infrastructure;

import com.sebsrvv.app.modules.meals.domain.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

@Repository
public class SupabaseMealRepository implements MealRepository {

    private final WebClient client;

    public SupabaseMealRepository(WebClient supabaseClient) {
        this.client = supabaseClient;
    }

    @Override
    public Meal save(Meal meal) {
        return client.post()
                .uri("/meals")
                .bodyValue(meal)
                .retrieve()
                .bodyToMono(Meal.class)
                .block();
    }

    @Override
    public Optional<Meal> findById(UUID mealId) {
        Meal[] result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/meals")
                        .queryParam("id", "eq." + mealId)
                        .build())
                .retrieve()
                .bodyToMono(Meal[].class)
                .block();

        return (result != null && result.length > 0) ? Optional.of(result[0]) : Optional.empty();
    }

    @Override
    public void delete(UUID mealId) {
        client.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/meals")
                        .queryParam("id", "eq." + mealId)
                        .build())
                .retrieve()
                .bodyToMono(Void.class)
                .block();
    }

    @Override
    public List<Meal> findByUserAndDate(UUID userId, LocalDate date) {
        Meal[] result = client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/meals")
                        .queryParam("user_id", "eq." + userId)
                        .queryParam("logged_at", "gte." + date + "T00:00:00Z")
                        .queryParam("logged_at", "lte." + date + "T23:59:59Z")
                        .build())
                .retrieve()
                .bodyToMono(Meal[].class)
                .block();

        return result != null ? List.of(result) : List.of();
    }

    @Override
    public List<MealCategory> findAllCategories() {
        MealCategory[] result = client.get()
                .uri("/meal_categories")
                .retrieve()
                .bodyToMono(MealCategory[].class)
                .block();

        return result != null ? List.of(result) : List.of();
    }
}
