// modules/users/infra/SupabaseUserProfileRepository.java
package com.sebsrvv.app.modules.users.infra;

import com.sebsrvv.app.supabase.SupabaseDataClient;
import com.sebsrvv.app.modules.users.port.UserProfileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;

@Repository
public class SupabaseUserProfileRepository implements UserProfileRepository {
    private final SupabaseDataClient data;
    private final String table;

    public SupabaseUserProfileRepository(SupabaseDataClient data,
                                         @Value("${supabase.profilesTable}") String table) {
        this.data = data; this.table = table;
    }

    @Override
    public Mono<Void> insertProfile(String id, String email, Map<String,Object> extra) {
        var row = new HashMap<String,Object>();
        row.put("id", id);
        row.put("email", email);
        row.putAll(extra);
        return data.insert(table, row).then();
    }
}
