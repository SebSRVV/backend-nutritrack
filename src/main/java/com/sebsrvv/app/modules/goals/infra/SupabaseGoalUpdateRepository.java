// modules/goals/infra/SupabaseGoalUpdateRepository.java
package com.sebsrvv.app.modules.goals.infra;

import com.sebsrvv.app.modules.goals.domain.port.GoalUpdateCommandPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SupabaseGoalUpdateRepository implements GoalUpdateCommandPort {

    private final SupabaseDataClient supa;

    public SupabaseGoalUpdateRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public UpdatedGoal update(UUID userId, UUID goalId, UpdateFields f) {
        Map<String,Object> body = new HashMap<>();
        if (f.goalName() != null)      body.put("goal_name", f.goalName());
        if (f.weeklyTarget() != null)  body.put("weekly_target", f.weeklyTarget());
        if (f.isActive() != null)      body.put("is_active", f.isActive());
        if (f.categoryId() != null)    body.put("category_id", f.categoryId());
        if (f.description() != null)   body.put("description", f.description());

        String qp = "id=eq." + goalId + "&user_id=eq." + userId;

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                supa.patch("user_goals", qp, body)
                        .blockOptional().orElse(List.of());

        if (rows.isEmpty()) throw new NoSuchElementException("Goal no encontrado para el usuario");

        Map<String,Object> r = rows.get(0);

        return new UpdatedGoal(
                UUID.fromString((String) r.get("id")),
                UUID.fromString((String) r.get("user_id")),
                r.get("default_id") == null ? null : ((Number) r.get("default_id")).intValue(),
                (String) r.get("goal_name"),
                r.get("weekly_target") == null ? null : ((Number) r.get("weekly_target")).intValue(),
                (Boolean) r.getOrDefault("is_active", Boolean.TRUE),
                r.get("category_id") == null ? null : ((Number) r.get("category_id")).intValue(),
                (String) r.get("description"),
                (String) r.get("updated_at") // ISO-8601
        );
    }
}
