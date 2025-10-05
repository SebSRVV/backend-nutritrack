package com.sebsrvv.app.modules.practice.web;

import com.sebsrvv.app.dto.PracticeResponse;
import com.sebsrvv.app.dto.PracticeSelectionRequest;
import com.sebsrvv.app.service.PracticeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/practices")
public class PracticeController {

    private final PracticeService practiceService;

    public PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @PostMapping(value = "/selection", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<List<PracticeResponse>> saveSelections(
            @PathVariable String userId,
            @RequestBody PracticeSelectionRequest request) {
        return practiceService.saveUserSelections(userId, request);
    }
}
