// modules/goals/infra/SupabaseGoalProgressDetailRepository.java
package com.sebsrvv.app.modules.goals.infra;

import com.sebsrvv.app.modules.goals.domain.port.GoalProgressDetailPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;

@Repository
public class SupabaseGoalProgressDetailRepository implements GoalProgressDetailPort {

    private final SupabaseDataClient supa;

    public SupabaseGoalProgressDetailRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public Goal findGoal(UUID userId, UUID goalId) {
        String qp = "select=id,default_id,goal_name,weekly_target,is_active"
                + "&id=eq." + goalId
                + "&user_id=eq." + userId
                + "&limit=1";

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                supa.select("user_goals", qp).blockOptional().orElse(List.of());

        if (rows.isEmpty()) return null;
        var r = rows.get(0);
        return new Goal(
                UUID.fromString((String) r.get("id")),
                r.get("default_id") == null ? null : ((Number) r.get("default_id")).intValue(),
                (String) r.get("goal_name"),
                r.get("weekly_target") == null ? 0 : ((Number) r.get("weekly_target")).intValue(),
                (Boolean) r.getOrDefault("is_active", Boolean.TRUE)
        );
    }

    @Override
    public List<DayLog> findDaily(UUID userId, UUID goalId, LocalDate from, LocalDate to) {
        String qp = "select=log_date,value,note"
                + "&user_id=eq." + userId
                + "&goal_id=eq." + goalId
                + "&log_date=gte." + from
                + "&log_date=lte." + to
                + "&order=log_date.asc";

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                supa.select("user_goal_progress", qp).blockOptional().orElse(List.of());

        List<DayLog> out = new ArrayList<>();
        for (var r : rows) {
            out.add(new DayLog(
                    LocalDate.parse((String) r.get("log_date")),
                    r.get("value") == null ? 0 : ((Number) r.get("value")).intValue(), // 0 | 1
                    (String) r.get("note")
            ));
        }
        return out;
    }
}
