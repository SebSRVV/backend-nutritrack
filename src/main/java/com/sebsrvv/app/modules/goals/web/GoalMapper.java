// src/main/java/com/sebsrvv/app/modules/goals/web/GoalMapper.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.domain.Goal;
import com.sebsrvv.app.modules.goals.domain.GoalProgress;
import com.sebsrvv.app.modules.goals.web.dto.*;

import java.util.UUID;

public class GoalMapper {

    public static Goal toEntity(GoalRequest r, UUID userId) {
        Goal g = new Goal();
        g.setUserId(userId);
        g.setGoalName(r.getGoal_name());
        g.setDescription(r.getDescription());
        g.setWeeklyTarget(r.getWeekly_target());
        g.setIsActive(r.getIs_active() == null ? true : r.getIs_active());
        g.setCategoryId(r.getCategory_id());
        g.setValueType(r.getValue_type());
        g.setUnit(r.getUnit());
        g.setStartDate(r.getStart_date());
        g.setEndDate(r.getEnd_date());
        g.setTargetValue(r.getTarget_value());
        return g;
    }

    public static void patch(Goal g, GoalRequest r) {
        if (r.getGoal_name()!=null) g.setGoalName(r.getGoal_name());
        if (r.getDescription()!=null) g.setDescription(r.getDescription());
        if (r.getWeekly_target()!=null) g.setWeeklyTarget(r.getWeekly_target());
        if (r.getIs_active()!=null) g.setIsActive(r.getIs_active());
        if (r.getCategory_id()!=null) g.setCategoryId(r.getCategory_id());
        if (r.getValue_type()!=null) g.setValueType(r.getValue_type());
        if (r.getUnit()!=null) g.setUnit(r.getUnit());
        if (r.getStart_date()!=null) g.setStartDate(r.getStart_date());
        if (r.getEnd_date()!=null) g.setEndDate(r.getEnd_date());
        if (r.getTarget_value()!=null) g.setTargetValue(r.getTarget_value());
    }

    public static GoalResponse toResponse(Goal g) {
        GoalResponse o = new GoalResponse();
        o.setId(g.getId());
        o.setGoal_name(g.getGoalName());
        o.setDescription(g.getDescription());
        o.setWeekly_target(g.getWeeklyTarget());
        o.setIs_active(g.getIsActive());
        o.setCategory_id(g.getCategoryId());
        o.setValue_type(g.getValueType());
        o.setUnit(g.getUnit());
        o.setStart_date(g.getStartDate());
        o.setEnd_date(g.getEndDate());
        o.setTarget_value(g.getTargetValue());
        o.setCreated_at(g.getCreatedAt());
        o.setUpdated_at(g.getUpdatedAt());
        return o;
    }

    public static GoalProgress toEntity(GoalProgressRequest r, UUID userId, UUID goalId) {
        GoalProgress p = new GoalProgress();
        p.setUserId(userId);
        p.setGoalId(goalId);
        p.setLogDate(r.getLog_date());
        p.setValue(r.getValue());
        p.setNote(r.getNote());
        return p;
    }

    public static GoalProgressResponse toResponse(GoalProgress p) {
        GoalProgressResponse o = new GoalProgressResponse();
        o.setId(p.getId());
        o.setGoal_id(p.getGoalId());
        o.setLog_date(p.getLogDate());
        o.setValue(p.getValue());
        o.setNote(p.getNote());
        return o;
    }
}
