// web/DefaultPracticeController.java
package com.sebsrvv.app.modules.practices.web;

import com.sebsrvv.app.modules.practices.application.GetDefaultPracticesUseCase;
import com.sebsrvv.app.modules.practices.web.dto.DefaultPracticeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/practices")
public class DefaultPracticeController {

    private final GetDefaultPracticesUseCase useCase;

    public DefaultPracticeController(GetDefaultPracticesUseCase useCase) {
        this.useCase = useCase;
    }

    @GetMapping("/defaults")
    public ResponseEntity<List<DefaultPracticeResponse>> getAllDefaults() {
        var result = useCase.execute();
        var body = result.stream()
                .map(p -> new DefaultPracticeResponse(
                        p.getId(),
                        p.getPracticeName(),
                        p.getDescription(),
                        p.getIcon(),
                        p.getFrequencyTarget(),
                        p.getIsActive()
                ))
                .toList();

        return ResponseEntity.ok(body);
    }
}
