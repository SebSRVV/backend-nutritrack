// src/main/java/com/sebsrvv/app/modules/users/application/UsersAnalyticsService.java
package com.sebsrvv.app.modules.users.application;

import com.sebsrvv.app.modules.users.web.dto.FoodByCategoryRequest;
import com.sebsrvv.app.modules.users.web.dto.FoodByCategoryResponse;
import com.sebsrvv.app.modules.users.web.dto.IntakeVsGoalRequest;
import com.sebsrvv.app.modules.users.web.dto.IntakeVsGoalResponse;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UsersAnalyticsService {

    private final SupabaseDataClient supabase;

    public UsersAnalyticsService(SupabaseDataClient supabase) {
        this.supabase = supabase;
    }

    /* ===================== food-by-category ===================== */

    public Mono<FoodByCategoryResponse> foodByCategory(UUID userId,
                                                       FoodByCategoryRequest req,
                                                       String authHeader) {
        Map<String, Object> payload = Map.of(
                "p_user_id", userId.toString(),
                "from_date", req.getFrom(),
                "to_date",   req.getTo(),
                "group_by",  req.getGroupBy()
        );

        Mono<List<FoodRow>> call = (authHeader != null && !authHeader.isBlank())
                ? supabase.callRpc("food_by_category", payload, authHeader,
                new ParameterizedTypeReference<List<FoodRow>>() {})
                : supabase.callRpcAsServiceRole("food_by_category", payload,
                new ParameterizedTypeReference<List<FoodRow>>() {});

        return call.map(rows -> mapToResponse(rows, req));
    }

    private FoodByCategoryResponse mapToResponse(List<FoodRow> rows, FoodByCategoryRequest req) {
        FoodByCategoryResponse res = new FoodByCategoryResponse();
        res.range = new FoodByCategoryResponse.Range(req.getFrom(), req.getTo(), req.getGroupBy());

        FoodByCategoryResponse.Totals totals = new FoodByCategoryResponse.Totals();
        totals.calories = safeToInt(rows.stream()
                .map(r -> r.totalCalories != null ? r.totalCalories : 0L)
                .mapToLong(Long::longValue).sum());
        totals.protein_g = rows.stream().map(r -> nz(r.totalProteinG)).reduce(BigDecimal.ZERO, BigDecimal::add);
        totals.carbs_g   = rows.stream().map(r -> nz(r.totalCarbsG)).reduce(BigDecimal.ZERO, BigDecimal::add);
        totals.fat_g     = rows.stream().map(r -> nz(r.totalFatG)).reduce(BigDecimal.ZERO, BigDecimal::add);
        res.totals = totals;

        Map<Integer, List<FoodRow>> byCat = rows.stream()
                .collect(Collectors.groupingBy(r -> r.categoryId != null ? r.categoryId : 0));

        List<FoodByCategoryResponse.CategorySeries> categories = new ArrayList<>();
        DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE;

        for (Map.Entry<Integer, List<FoodRow>> e : byCat.entrySet()) {
            Integer catId = e.getKey();
            List<FoodRow> group = e.getValue();

            FoodByCategoryResponse.CategorySeries cs = new FoodByCategoryResponse.CategorySeries();
            cs.categoryId = catId;
            cs.name = group.stream().map(r -> r.categoryName).filter(Objects::nonNull)
                    .findFirst().orElse("Uncategorized");
            cs.calories = safeToInt(group.stream()
                    .map(r -> r.totalCalories != null ? r.totalCalories : 0L)
                    .mapToLong(Long::longValue).sum());
            cs.count = safeToInt(group.stream()
                    .map(r -> r.itemsCount != null ? r.itemsCount : 0L)
                    .mapToLong(Long::longValue).sum());

            Comparator<FoodRow> byStart = Comparator.comparing(
                    r -> Optional.ofNullable(r.periodStart).orElse(LocalDate.MIN)
            );

            cs.series = group.stream()
                    .sorted(byStart)
                    .map(r -> {
                        FoodByCategoryResponse.Point p = new FoodByCategoryResponse.Point();
                        p.bucket   = r.periodStart != null ? r.periodStart.format(ISO) : "";
                        p.calories = safeToInt(r.totalCalories != null ? r.totalCalories : 0L);
                        p.count    = safeToInt(r.itemsCount != null ? r.itemsCount : 0L);
                        return p;
                    })
                    .collect(Collectors.toList());

            categories.add(cs);
        }

        categories.sort(Comparator.comparingInt((FoodByCategoryResponse.CategorySeries c) -> c.calories).reversed());
        res.categories = categories;
        return res;
    }

    private static BigDecimal nz(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }

    private static int safeToInt(long v) {
        if (v > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        if (v < Integer.MIN_VALUE) return Integer.MIN_VALUE;
        return (int) v;
    }

    /* ===================== intake-vs-goal ===================== */

    public Mono<IntakeVsGoalResponse> intakeVsGoal(UUID userId,
                                                   IntakeVsGoalRequest req,
                                                   String authHeader) {

        Map<String, Object> payload = Map.of(
                "p_user_id", userId.toString(),
                "from_date", req.getFrom(),
                "to_date",   req.getTo()
        );

        Mono<List<IntakeRow>> call = (authHeader != null && !authHeader.isBlank())
                ? supabase.callRpc("intake_vs_goal", payload, authHeader,
                new ParameterizedTypeReference<List<IntakeRow>>() {})
                : supabase.callRpcAsServiceRole("intake_vs_goal", payload,
                new ParameterizedTypeReference<List<IntakeRow>>() {});

        return call.map(rows -> {
            IntakeVsGoalResponse res = new IntakeVsGoalResponse();

            int goal = rows.stream()
                    .map(r -> r.goalKcal != null ? r.goalKcal : 0)
                    .findFirst().orElse(0);
            res.goalKcal = goal;

            DateTimeFormatter ISO = DateTimeFormatter.ISO_DATE;

            List<IntakeVsGoalResponse.Day> days = new ArrayList<>();
            long sumCalories = 0;
            long sumDelta = 0;
            int within = 0;

            for (IntakeRow r : rows) {
                IntakeVsGoalResponse.Day d = new IntakeVsGoalResponse.Day();
                d.date = r.logDate != null ? r.logDate.format(ISO) : "";
                int cals = safeToInt(r.calories != null ? r.calories : 0L);
                d.calories = cals;
                d.delta = cals - goal;

                sumCalories += cals;
                sumDelta += d.delta;
                if (cals <= goal) within++;

                days.add(d);
            }

            res.days = days;

            IntakeVsGoalResponse.Summary s = new IntakeVsGoalResponse.Summary();
            int n = Math.max(days.size(), 1); // evitar /0
            s.daysWithinGoal = within;
            s.avgCalories = (double) sumCalories / n;
            s.avgDelta = (double) sumDelta / n;

            res.summary = s;
            return res;
        });
    }
}
