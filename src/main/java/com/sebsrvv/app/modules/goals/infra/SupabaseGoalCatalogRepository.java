// modules/goals/infra/SupabaseGoalCatalogRepository.java
package com.sebsrvv.app.modules.goals.infra;

import com.sebsrvv.app.modules.goals.domain.port.GoalCatalogQueryPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SupabaseGoalCatalogRepository implements GoalCatalogQueryPort {

    private final SupabaseDataClient supa;

    public SupabaseGoalCatalogRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public Map<Integer, CatalogItem> getByDefaultIds(Iterable<Integer> defaultIds) {
        List<Integer> ids = new ArrayList<>();
        defaultIds.forEach(ids::add);
        if (ids.isEmpty()) return Collections.emptyMap();

        String in = "(" + String.join(",", ids.stream().map(String::valueOf).toList()) + ")";
        String qp = "select=id,goal_name,weekly_target,is_active&id=in." + in + "&is_active=is.true";

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> list = (List<Map<String,Object>>)(List<?>)
                supa.select("default_goals", qp).blockOptional().orElse(List.of());

        Map<Integer, CatalogItem> map = new HashMap<>();
        for (var r : list) {
            map.put(((Number) r.get("id")).intValue(),
                    new CatalogItem(
                            (String) r.get("goal_name"),
                            r.get("weekly_target") == null ? null : ((Number) r.get("weekly_target")).intValue()
                    )
            );
        }
        return map;
    }
}
