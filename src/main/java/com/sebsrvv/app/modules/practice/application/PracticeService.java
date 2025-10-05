package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.web.dto.ViewPracticesResponse;
import com.sebsrvv.app.modules.practice.domain.PracticeSelectionRequest;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PracticeService {

    private final SupabaseDataClient supabase;

    public PracticeService(SupabaseDataClient supabase) {
        this.supabase = supabase;
    }

    public Mono<List<ViewPracticesResponse>> saveUserSelections(String userId, PracticeSelectionRequest request) {
        // Supongamos que tienes una función RPC en Supabase llamada "save_user_practices"
        // que recibe: { user_id, selections }

        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", userId);
        payload.put("selections", request.getSelections());

        return supabase.callRpcAsServiceRole(
                "save_user_practices",
                payload,
                new ParameterizedTypeReference<>() {}
        );
    }
}
