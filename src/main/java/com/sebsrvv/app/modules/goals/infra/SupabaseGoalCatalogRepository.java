// modules/goals/infra/SupabaseGoalCatalogRepository.java
package com.sebsrvv.app.modules.goals.infra;

import com.sebsrvv.app.modules.goals.domain.port.GoalCatalogQueryPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SupabaseGoalCatalogRepository implements GoalCatalogQueryPort {

    private final SupabaseDataClient supaClient;

    public SupabaseGoalCatalogRepository(SupabaseDataClient supaClient) {
        this.supaClient = supaClient;
    }

    @Override
    public Map<Integer, CatalogItem> getByDefaultIds(Iterable<Integer> defaultIds) {
        List<Integer> ids = new ArrayList<>();
        defaultIds.forEach(ids::add);
        if (ids.isEmpty()) return Collections.emptyMap();

        String in = "(" + String.join(",", ids.stream().map(String::valueOf).toList()) + ")";
        String qp = "select=id,goal_name,weekly_target,is_active&id=in." + in + "&is_active=is.true";

        List<Map<String,Object>> rows = supaClient
                .select("default_goals", qp)   // usa overload sin Authorization
                .blockOptional()
                .orElseGet(List::of);

        Map<Integer, CatalogItem> map = new HashMap<>();
        for (var r : rows) {
            Integer id = ((Number) r.get("id")).intValue();
            String goalName = (String) r.get("goal_name");
            Integer weeklyTarget = r.get("weekly_target") == null
                    ? null
                    : ((Number) r.get("weekly_target")).intValue();

            map.put(id, new CatalogItem(goalName, weeklyTarget));
        }
        return map;
    }
}
