package com.sebsrvv.app.modules.practices.infra;

import com.sebsrvv.app.modules.practices.domain.model.DefaultPractice;
import com.sebsrvv.app.modules.practices.domain.port.DefaultPracticeQueryPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SupabaseDefaultPracticeRepository implements DefaultPracticeQueryPort {

    private final SupabaseDataClient supa;

    public SupabaseDefaultPracticeRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public List<DefaultPractice> findAll() {
        // seleccionamos las columnas relevantes
        String qp = "select=id,practice_name,description,icon,frequency_target,is_active&order=sort_order.asc";

        List<Map<String,Object>> list = supa.select("default_practices", qp)
                .blockOptional()
                .orElse(Collections.emptyList());

        List<DefaultPractice> result = new ArrayList<>();
        for (Map<String,Object> row : list) {
            result.add(new DefaultPractice(
                    ((Number) row.get("id")).intValue(),
                    (String) row.get("practice_name"),
                    (String) row.get("description"),
                    (String) row.get("icon"),
                    ((Number) row.get("frequency_target")).intValue(),
                    (Boolean) row.get("is_active")
            ));
        }
        return result;
    }
}
