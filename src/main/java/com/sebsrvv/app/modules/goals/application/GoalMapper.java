package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.web.dto.GoalDto;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressDto;

import java.util.HashMap;
import java.util.Map;

final class GoalMapper {
    private GoalMapper() {}

    static Map<String,Object> toRow(GoalDto dto) {
        var m = new HashMap<String,Object>();
        if (dto.goal_name()     != null) m.put("goal_name", dto.goal_name());
        if (dto.description()   != null) m.put("description", dto.description());
        if (dto.weekly_target() != null) m.put("weekly_target", dto.weekly_target());
        if (dto.is_active()     != null) m.put("is_active", dto.is_active());
        if (dto.category_id()   != null) m.put("category_id", dto.category_id());
        return m;
    }

    static Map<String,Object> toRow(GoalProgressDto dto) {
        var m = new HashMap<String,Object>();
        if (dto.goal_id()  != null) m.put("goal_id", dto.goal_id());
        if (dto.log_date() != null) m.put("log_date", dto.log_date());
        if (dto.value()    != null) m.put("value", dto.value());
        if (dto.note()     != null) m.put("note", dto.note());
        return m;
    }
}
