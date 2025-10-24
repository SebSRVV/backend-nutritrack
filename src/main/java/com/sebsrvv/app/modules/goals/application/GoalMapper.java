package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.Goal;
import com.sebsrvv.app.modules.goals.web.dto.GoalDto;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public final class GoalMapper {

    private GoalMapper(){}

    public static Map<String,Object> toRow(GoalDto dto) {
        var m = new HashMap<String,Object>();
        if (dto.goal_name() != null)     m.put("goal_name", dto.goal_name());
        if (dto.description() != null)   m.put("description", dto.description());
        if (dto.weekly_target() != null) m.put("weekly_target", dto.weekly_target());
        if (dto.is_active() != null)     m.put("is_active", dto.is_active());
        if (dto.category_id() != null)   m.put("category_id", dto.category_id());
        // nuevos opcionales
        if (dto.value_type() != null)    m.put("value_type", dto.value_type());
        if (dto.unit() != null)          m.put("unit", dto.unit());
        if (dto.start_date() != null)    m.put("start_date", dto.start_date());
        if (dto.end_date() != null)      m.put("end_date", dto.end_date());
        if (dto.target_value() != null)  m.put("target_value", dto.target_value());
        return m;
    }

    public static Goal fromRow(Map<String,Object> r) {
        Goal g = new Goal();
        g.setId((String) r.get("id"));
        g.setUserId((String) r.get("user_id"));
        g.setGoalName((String) r.get("goal_name"));
        g.setDescription((String) r.get("description"));
        if (r.get("weekly_target") != null) g.setWeeklyTarget(((Number) r.get("weekly_target")).intValue());
        if (r.get("is_active") != null)     g.setIsActive((Boolean) r.get("is_active"));
        if (r.get("category_id") != null)   g.setCategoryId(((Number) r.get("category_id")).intValue());
        g.setValueType((String) r.get("value_type"));
        g.setUnit((String) r.get("unit"));
        g.setStartDate((String) r.get("start_date"));
        g.setEndDate((String) r.get("end_date"));
        if (r.get("target_value") != null)  g.setTargetValue(new BigDecimal(r.get("target_value").toString()));
        g.setCreatedAt((String) r.get("created_at"));
        g.setUpdatedAt((String) r.get("updated_at"));
        return g;
    }
}
