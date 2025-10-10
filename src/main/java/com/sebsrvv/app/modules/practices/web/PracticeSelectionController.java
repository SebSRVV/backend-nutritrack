// practices/web/PracticeSelectionController.java
package com.sebsrvv.app.modules.practices.web;

import com.sebsrvv.app.modules.practices.web.dto.PracticeSelectionRequest;
import com.sebsrvv.app.modules.practices.web.dto.PracticeSelectionResponse;
import com.sebsrvv.app.modules.practices.application.SelectPracticesUseCase;
import com.sebsrvv.app.modules.practices.domain.port.PracticeSelectionCommandPort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{userId}/practices")
public class PracticeSelectionController {

    private final SelectPracticesUseCase useCase;

    public PracticeSelectionController(SelectPracticesUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/selection")
    public ResponseEntity<List<PracticeSelectionResponse>> selectPractices(
            @PathVariable UUID userId,
            @RequestBody @Validated PracticeSelectionRequest request
    ) {
        var cmds = request.selections().stream()
                .map(i -> new PracticeSelectionCommandPort.SelectionCommand(
                        i.defaultId(), i.active(), i.frequencyTarget()))
                .toList();

        var res = useCase.execute(userId, cmds);

        var body = res.stream().map(p ->
                new PracticeSelectionResponse(
                        p.getId(),
                        p.getDefaultId(),
                        p.getPracticeName(),
                        p.getDescription(),
                        p.getIcon(),
                        p.getFrequencyTarget(),
                        p.getIsActive(),
                        p.getSortOrder()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(body);
    }
}
