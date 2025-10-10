// modules/goals/infra/SupabaseGoalSelectionRepository.java
package com.sebsrvv.app.modules.goals.infra;

import com.sebsrvv.app.modules.goals.domain.model.GoalSelection;
import com.sebsrvv.app.modules.goals.domain.port.GoalSelectionCommandPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SupabaseGoalSelectionRepository implements GoalSelectionCommandPort {

    private final SupabaseDataClient supa;

    public SupabaseGoalSelectionRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public List<GoalSelection> upsertSelections(UUID userId, List<SelectionCommand> selections) {
        // Insert/Update por cada ítem, copiando snapshot de default_goals (name + weekly_target)
        // 1) Traemos defaults para cada defaultId (vía join en el select final también)
        // 2) Upsert en user_goals con is_active y (si faltan) goal_name/weekly_target

        for (var s : selections) {
            Map<String,Object> row = new HashMap<>();
            row.put("user_id", userId);
            row.put("default_id", s.defaultId());
            row.put("is_active", Boolean.TRUE.equals(s.active()));
            // NOTA: si quieres congelar siempre snapshot:
            // también puedes setear goal_name/weekly_target aquí tras consultar el catálogo
            supa.upsert("user_goals", row).block();
        }

        String in = "(" + String.join(",", selections.stream().map(SelectionCommand::defaultId).map(String::valueOf).toList()) + ")";
        String qp = "select=id,default_id,goal_name,weekly_target,is_active," +
                "default_goals(goal_name,weekly_target)" +
                "&user_id=eq." + userId + "&default_id=in." + in;

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                supa.select("user_goals", qp).blockOptional().orElse(List.of());

        List<GoalSelection> out = new ArrayList<>();
        for (var r : rows) {
            var def = (Map<String,Object>) r.get("default_goals");

            UUID id = UUID.fromString((String) r.get("id"));
            Integer defaultId = ((Number) r.get("default_id")).intValue();
            String name = (String) r.get("goal_name");
            Integer weeklyTarget = r.get("weekly_target") == null ? null : ((Number) r.get("weekly_target")).intValue();
            Boolean active = (Boolean) r.getOrDefault("is_active", Boolean.TRUE);

            if (name == null && def != null) name = (String) def.get("goal_name");
            if (weeklyTarget == null && def != null && def.get("weekly_target") != null)
                weeklyTarget = ((Number) def.get("weekly_target")).intValue();

            out.add(GoalSelection.of(id, userId, defaultId, name, weeklyTarget, active));
        }
        return out;
    }
}
