package com.sebsrvv.app.modules.profile.api;

import com.sebsrvv.app.modules.profile.dto.*;
import com.sebsrvv.app.modules.profile.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    @GetMapping
    public ResponseEntity<ProfileView> get(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.of(service.get(userId));
    }

    @PostMapping
    public ProfileView upsert(@RequestHeader("X-User-Id") UUID userId,
                              @Valid @RequestBody ProfileUpsertDto dto) {
        return service.upsert(userId, dto);
    }

    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationView> recs(@RequestHeader("X-User-Id") UUID userId) {
        return ResponseEntity.of(service.getRecommendations(userId));
    }
}
