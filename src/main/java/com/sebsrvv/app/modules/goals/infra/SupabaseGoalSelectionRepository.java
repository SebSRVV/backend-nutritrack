// modules/goals/infra/SupabaseGoalSelectionRepository.java
package com.sebsrvv.app.modules.goals.infra;

import com.sebsrvv.app.modules.goals.domain.model.GoalSelection;
import com.sebsrvv.app.modules.goals.domain.port.GoalSelectionCommandPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SupabaseGoalSelectionRepository implements GoalSelectionCommandPort {

    private final SupabaseDataClient supa;

    public SupabaseGoalSelectionRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public List<GoalSelection> upsertSelections(UUID userId, List<SelectionCommand> selections, String authorization) {
        // armamos el array para el RPC
        List<Map<String,Object>> payloadItems = new ArrayList<>();
        for (var s : selections) {
            Map<String,Object> it = new HashMap<>();
            it.put("defaultId", s.defaultId());
            it.put("active", Boolean.TRUE.equals(s.active()));
            payloadItems.add(it);
        }

        Map<String,Object> payload = Map.of("p_selections", payloadItems);

        List<Map<String,Object>> rows = supa.callRpc(
                "upsert_goal_selections",
                payload,
                authorization,
                new ParameterizedTypeReference<List<Map<String,Object>>>() {}
        ).blockOptional().orElse(List.of());

        List<GoalSelection> out = new ArrayList<>();
        for (var r : rows) {
            UUID id = UUID.fromString(String.valueOf(r.get("id")));
            Integer defaultId = r.get("default_id") == null ? null : ((Number) r.get("default_id")).intValue();
            String goalName = (String) r.get("goal_name");
            Integer weeklyTarget = r.get("weekly_target") == null ? null : ((Number) r.get("weekly_target")).intValue();
            Boolean active = (Boolean) r.get("is_active");
            out.add(GoalSelection.of(id, userId, defaultId, goalName, weeklyTarget, active));
        }
        return out;
    }
}
