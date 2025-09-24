package com.sebsrvv.app.modules.meals.api;

import com.sebsrvv.app.modules.meals.dto.CreateMealLogDto;
import com.sebsrvv.app.modules.meals.dto.MealLogView;
import com.sebsrvv.app.modules.meals.service.MealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/meals")
@RequiredArgsConstructor
public class MealController {
    private final MealService service;

    @PostMapping
    public MealLogView create(@RequestHeader("X-User-Id") UUID userId,
                              @Valid @RequestBody CreateMealLogDto dto){
        return service.create(userId, dto);
    }

    @GetMapping("/day")
    public List<MealLogView> byDay(@RequestHeader("X-User-Id") UUID userId,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date){
        return service.listByDay(userId, date);
    }

    @GetMapping("/range")
    public List<MealLogView> byRange(@RequestHeader("X-User-Id") UUID userId,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){
        return service.listByRange(userId, from, to);
    }
}
