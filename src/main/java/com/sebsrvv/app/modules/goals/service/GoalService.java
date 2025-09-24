package com.sebsrvv.app.modules.goals.service;

import com.sebsrvv.app.modules.goals.dto.*;
import com.sebsrvv.app.modules.goals.entity.FoodCategory;
import com.sebsrvv.app.modules.goals.entity.UserGoal;
import com.sebsrvv.app.modules.goals.repo.FoodCategoryRepository;
import com.sebsrvv.app.modules.goals.repo.UserGoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GoalService {
    private final UserGoalRepository goalRepo;
    private final FoodCategoryRepository catRepo;

    public List<GoalView> list(UUID userId){
        return goalRepo.findByUser_id(userId).stream().map(this::toView).toList();
    }

    @Transactional
    public GoalView create(UUID userId, CreateGoalDto dto){
        validateGoalType(dto.goal_type());
        UserGoal g = new UserGoal();
        g.setUser_id(userId);
        g.setGoal_name(dto.goal_name());
        g.setGoal_type(dto.goal_type().toLowerCase());
        g.setTarget_value(dto.target_value());
        g.setUnit(dto.unit());
        if(dto.category_id()!=null){
            FoodCategory cat = catRepo.findById(dto.category_id())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "category_id inválido"));
            g.setCategory(cat);
        }
        return toView(goalRepo.save(g));
    }

    @Transactional
    public GoalView updateProgress(UUID userId, UUID goalId, UpdateProgressDto dto){
        UserGoal g = goalRepo.findById(goalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta no encontrada"));
        if(!g.getUser_id().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permitido");
        g.setCurrent_progress(dto.current_progress());
        return toView(goalRepo.save(g));
    }

    @Transactional
    public void delete(UUID userId, UUID goalId){
        UserGoal g = goalRepo.findById(goalId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta no encontrada"));
        if(!g.getUser_id().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No permitido");
        goalRepo.delete(g);
    }

    private static void validateGoalType(String t){
        String v = t==null? "": t.toLowerCase();
        if(!(v.equals("increase")||v.equals("decrease")))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "goal_type debe ser increase|decrease");
    }

    private GoalView toView(UserGoal g){
        Integer catId = g.getCategory()==null? null : g.getCategory().getId();
        String  catNm = g.getCategory()==null? null : g.getCategory().getName();
        return new GoalView(
                g.getId(),
                g.getGoal_name(),
                g.getGoal_type(),
                g.getTarget_value(),
                g.getCurrent_progress(),
                g.getUnit(),
                catId,
                catNm
        );
    }
}
