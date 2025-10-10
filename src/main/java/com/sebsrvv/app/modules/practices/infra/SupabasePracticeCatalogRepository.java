// practices/infrastructure/adapter/SupabasePracticeCatalogRepository.java
package com.sebsrvv.app.modules.practices.infra;

import com.sebsrvv.app.modules.practices.domain.port.PracticeCatalogQueryPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SupabasePracticeCatalogRepository implements PracticeCatalogQueryPort {

    private final SupabaseDataClient supa;

    public SupabasePracticeCatalogRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public Map<Integer, CatalogItem> getByDefaultIds(Iterable<Integer> defaultIds) {
        List<Integer> ids = new ArrayList<>();
        defaultIds.forEach(ids::add);
        if (ids.isEmpty()) return Collections.emptyMap();

        String in = "(" + ids.stream().sorted().map(String::valueOf).reduce((a,b) -> a + "," + b).orElse("") + ")";
        String qp = "select=id,practice_name,description,icon,sort_order&id=in." + in;

        List list = supa.select("default_practices", qp).blockOptional().orElse(Collections.emptyList());
        Map<Integer, CatalogItem> map = new HashMap<>();
        for (Object o : list) {
            Map m = (Map) o;
            map.put(((Number)m.get("id")).intValue(),
                    new CatalogItem(
                            (String) m.get("practice_name"),
                            (String) m.get("description"),
                            (String) m.get("icon"),
                            m.get("sort_order") == null ? null : ((Number)m.get("sort_order")).intValue()
                    ));
        }
        return map;
    }
}
