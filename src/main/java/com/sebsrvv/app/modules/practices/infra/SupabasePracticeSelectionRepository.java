// practices/infra/SupabasePracticeSelectionRepository.java
package com.sebsrvv.app.modules.practices.infra;

import com.sebsrvv.app.modules.practices.domain.model.PracticeSelection;
import com.sebsrvv.app.modules.practices.domain.port.PracticeSelectionCommandPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SupabasePracticeSelectionRepository implements PracticeSelectionCommandPort {

    private final SupabaseDataClient supa;

    public SupabasePracticeSelectionRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public List<PracticeSelection> upsertSelections(UUID userId, List<SelectionCommand> selections) {
        // 1) Upsert fila por fila en healthy_practices (constraint única user_id+default_id)
        for (SelectionCommand s : selections) {
            Map<String,Object> row = new HashMap<>();
            row.put("user_id", userId);
            row.put("default_id", s.defaultId());
            row.put("is_active", Boolean.TRUE.equals(s.isActive()));
            if (s.frequencyTarget() != null) row.put("frequency_target", s.frequencyTarget());
            // No tocamos name/description/icon/sort_order del usuario (si existen)
            supa.upsert("healthy_practices", row).block();
        }

        // 2) Select con embed de default_practices para fallback
        // Nota: el embed se basa en la FK healthy_practices.default_id -> default_practices.id
        String qp =
                "select=id,default_id,practice_name,description,icon,frequency_target,is_active,sort_order," +
                        "default_practices(name,description,icon,sort_order)" +
                        "&user_id=eq." + userId +
                        // filtramos sólo los default_id pedidos, para no traer todo el set del usuario
                        "&default_id=in." + toInList(selections.stream().map(SelectionCommand::defaultId).collect(Collectors.toSet()));

        List result = supa.select("healthy_practices", qp).blockOptional().orElse(Collections.emptyList());

        List<PracticeSelection> out = new ArrayList<>();
        for (Object o : result) {
            Map map = (Map) o;
            Map def = (Map) map.get("default_practices");

            UUID id = UUID.fromString((String) map.get("id"));
            Integer defaultId = ((Number) map.get("default_id")).intValue();
            Integer freq = map.get("frequency_target") == null ? null : ((Number) map.get("frequency_target")).intValue();
            Boolean active = map.get("is_active") != null && (Boolean) map.get("is_active");
            Integer sortOrder = map.get("sort_order") == null ? null : ((Number) map.get("sort_order")).intValue();

            String name = (String) map.get("practice_name");
            String desc = (String) map.get("description");
            String icon = (String) map.get("icon");

            // Fallback a catálogo si algún campo está nulo
            if (name == null && def != null) name = (String) def.get("name");
            if (desc == null && def != null) desc = (String) def.get("description");
            if (icon == null && def != null) icon = (String) def.get("icon");
            if (sortOrder == null && def != null && def.get("sort_order") != null)
                sortOrder = ((Number) def.get("sort_order")).intValue();

            out.add(PracticeSelection.of(id, userId, defaultId, name, desc, icon, freq, active, sortOrder));
        }
        return out;
    }

    private static String toInList(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) return "(null)"; // evitar traer todo
        return "(" + ids.stream().sorted().map(String::valueOf).collect(Collectors.joining(",")) + ")";
    }
}
