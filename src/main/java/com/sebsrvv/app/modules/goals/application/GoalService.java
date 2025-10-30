// src/main/java/com/sebsrvv/app/modules/goals/application/GoalService.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.GoalRepository;
import com.sebsrvv.app.modules.goals.domain.GoalProgressRepository;
import com.sebsrvv.app.modules.goals.domain.*;
import com.sebsrvv.app.modules.goals.web.GoalMapper;
import com.sebsrvv.app.modules.goals.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private GoalProgressRepository progressRepository;

    @Transactional
    public GoalResponse createGoal(GoalRequest goal, UUID userId) {
        Goal newGoal = new Goal();
        //newGoal.setId(goal.getId());
        newGoal.setUserId(userId);
        newGoal.setGoalName(goal.getGoal_name());
        newGoal.setDescription(goal.getDescription());
        newGoal.setWeeklyTarget(goal.getWeekly_target());
        newGoal.setIsActive(goal.getIs_active());
        newGoal.setValueType(goal.getValue_type());
        newGoal.setUnit(goal.getUnit());
        newGoal.setTargetValue(goal.getTarget_value());
        newGoal.setStartDate(goal.getStart_date());
        newGoal.setEndDate(goal.getEnd_date());

        Goal saved = goalRepository.save(newGoal);
        return GoalMapper.toResponse(saved);
    }

    @Transactional
    public GoalResponse updateGoal(GoalRequest goal, UUID userId) {
        Goal destino = goalRepository.findById(userId).orElse(null);
        if (destino == null) {
            return null;
        } else {
            destino.setGoalName(goal.getGoal_name());
            destino.setDescription(goal.getDescription());
            destino.setWeeklyTarget(goal.getWeekly_target());
            destino.setIsActive(goal.getIs_active());
            destino.setValueType(goal.getValue_type());
            destino.setUnit(goal.getUnit());
            destino.setTargetValue(goal.getTarget_value());
            destino.setStartDate(goal.getStart_date());
            destino.setEndDate(goal.getEnd_date());
            Goal saved = goalRepository.save(destino);
            return GoalMapper.toResponse(saved);
        }
    }
}
