package com.sebsrvv.app.modules.meals.service;

import com.sebsrvv.app.modules.goals.entity.FoodCategory;
import com.sebsrvv.app.modules.goals.repo.FoodCategoryRepository;
import com.sebsrvv.app.modules.meals.dto.CreateMealLogDto;
import com.sebsrvv.app.modules.meals.dto.MealLogView;
import com.sebsrvv.app.modules.meals.entity.MealLog;
import com.sebsrvv.app.modules.meals.entity.MealLogCategory;
import com.sebsrvv.app.modules.meals.repo.MealLogCategoryRepository;
import com.sebsrvv.app.modules.meals.repo.MealLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
    import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MealService {
    private final MealLogRepository mealRepo;
    private final MealLogCategoryRepository mlcRepo;
    private final FoodCategoryRepository catRepo;

    @Transactional
    public MealLogView create(UUID userId, CreateMealLogDto dto){
        MealLog m = MealLog.builder()
                .user_id(userId)
                .description(dto.description())
                .calories(dto.calories())
                .protein_g(dto.protein_g())
                .carbs_g(dto.carbs_g())
                .fat_g(dto.fat_g())
                .meal_type(dto.meal_type())
                .logged_at(dto.logged_at())
                .meal_categories(dto.meal_categories())
                .ai_items(dto.ai_items())
                .build();
        m = mealRepo.save(m);

        if (dto.category_ids()!=null && !dto.category_ids().isEmpty()){
            for(Integer id : dto.category_ids()){
                FoodCategory cat = catRepo.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "category_id inválido: "+id));
                mlcRepo.save(MealLogCategory.builder().mealLog(m).category(cat).build());
            }
        }
        return toView(m);
    }

    public List<MealLogView> listByDay(UUID userId, LocalDate day){
        OffsetDateTime from = day.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime to   = day.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);
        return mealRepo.findByUser_idAndLogged_atBetween(userId, from, to).stream().map(this::toView).toList();
    }

    public List<MealLogView> listByRange(UUID userId, LocalDate fromDate, LocalDate toDate){
        OffsetDateTime from = fromDate.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime to   = toDate.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);
        return mealRepo.findByUser_idAndLogged_atBetween(userId, from, to).stream().map(this::toView).toList();
    }

    private MealLogView toView(MealLog m){
        return new MealLogView(
                m.getId(),
                m.getUser_id(),
                m.getDescription(),
                m.getCalories(),
                m.getProtein_g(),
                m.getCarbs_g(),
                m.getFat_g(),
                m.getMeal_type(),
                m.getLogged_at(),
                m.getMeal_categories()
        );
    }
}
