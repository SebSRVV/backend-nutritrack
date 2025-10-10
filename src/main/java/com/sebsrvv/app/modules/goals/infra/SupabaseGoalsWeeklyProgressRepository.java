// modules/goals/infra/SupabaseGoalsWeeklyProgressRepository.java
package com.sebsrvv.app.modules.goals.infra;

import com.sebsrvv.app.modules.goals.domain.port.GoalsWeeklyProgressPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SupabaseGoalsWeeklyProgressRepository implements GoalsWeeklyProgressPort {

    private final SupabaseDataClient supa;

    public SupabaseGoalsWeeklyProgressRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public List<UserGoal> findUserGoals(UUID userId) {
        // traemos name/weekly_target con fallback al catálogo
        String qp = "select=id,default_id,goal_name,weekly_target,is_active," +
                "default_goals(goal_name,weekly_target)" +
                "&user_id=eq." + userId;
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                supa.select("user_goals", qp).blockOptional().orElse(List.of());

        List<UserGoal> list = new ArrayList<>();
        for (var r : rows) {
            Map<String,Object> def = (Map<String,Object>) r.get("default_goals");
            String name = (String) r.get("goal_name");
            Integer weekly = r.get("weekly_target") == null ? null : ((Number) r.get("weekly_target")).intValue();
            if (name == null && def != null) name = (String) def.get("goal_name");
            if (weekly == null && def != null && def.get("weekly_target") != null)
                weekly = ((Number) def.get("weekly_target")).intValue();

            list.add(new UserGoal(
                    UUID.fromString((String) r.get("id")),
                    r.get("default_id") == null ? null : ((Number) r.get("default_id")).intValue(),
                    name,
                    weekly == null ? 0 : weekly,
                    (Boolean) r.getOrDefault("is_active", Boolean.TRUE)
            ));
        }
        return list;
    }

    @Override
    public Map<UUID, Map<LocalDate, Integer>> findDailyValues(UUID userId, Collection<UUID> goalIds,
                                                              LocalDate from, LocalDate to) {
        if (goalIds.isEmpty()) return Collections.emptyMap();

        String in = "(" + goalIds.stream().map(UUID::toString).collect(Collectors.joining(",")) + ")";
        String qp = "select=goal_id,log_date,value" +
                "&user_id=eq." + userId +
                "&goal_id=in." + in +
                "&log_date=gte." + from +
                "&log_date=lte." + to;

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                supa.select("user_goal_progress", qp).blockOptional().orElse(List.of());

        Map<UUID, Map<LocalDate, Integer>> out = new HashMap<>();
        for (var r : rows) {
            UUID gid = UUID.fromString((String) r.get("goal_id"));
            LocalDate date = LocalDate.parse((String) r.get("log_date"));
            int val = ((Number) r.get("value")).intValue(); // 0|1

            out.computeIfAbsent(gid, k -> new HashMap<>())
                    .merge(date, val, Integer::sum); // si hay varios 1 en el mismo día, sumará >1 (lo capamos en el use case)
        }
        return out;
    }
}
