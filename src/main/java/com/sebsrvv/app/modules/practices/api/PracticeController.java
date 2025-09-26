package com.sebsrvv.app.modules.practices.api;

import com.sebsrvv.app.modules.practices.dto.*;
import com.sebsrvv.app.modules.practices.entity.HealthyPractice;
import com.sebsrvv.app.modules.practices.entity.PracticeLog;
import com.sebsrvv.app.modules.practices.service.PracticeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/practices")
@RequiredArgsConstructor
public class PracticeController {
    private final PracticeService service;

    @GetMapping
    public List<HealthyPractice> list(@RequestHeader("X-User-Id") UUID userId){
        return service.list(userId);
    }

    @PostMapping
    public HealthyPractice create(@RequestHeader("X-User-Id") UUID userId,
                                  @Valid @RequestBody CreatePracticeDto dto){
        return service.create(userId, dto);
    }

    @PostMapping("/check")
    public PracticeLog check(@RequestHeader("X-User-Id") UUID userId,
                             @Valid @RequestBody CheckPracticeDto dto){
        return service.check(userId, dto);
    }

    @GetMapping("/weekly-count")
    public long weekly(@RequestHeader("X-User-Id") UUID userId,
                       @RequestParam UUID practiceId,
                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start){
        return service.weeklyCount(userId, practiceId, start);
    }
}
