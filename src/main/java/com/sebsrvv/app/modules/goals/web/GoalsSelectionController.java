// modules/goals/web/GoalsSelectionController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.SelectGoalsUseCase;
import com.sebsrvv.app.modules.goals.domain.port.GoalSelectionCommandPort;
import com.sebsrvv.app.modules.goals.web.dto.GoalSelectionRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalSelectionResponse;
import org.springframework.http.HttpHeaders;               // 👈 FALTA ESTE
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;                                   // (opcional, pero más explícito)

@RestController
@RequestMapping("/api/users/{userId}/goals")
public class GoalsSelectionController {

    private final SelectGoalsUseCase useCase;

    public GoalsSelectionController(SelectGoalsUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping("/selection")
    public ResponseEntity<List<GoalSelectionResponse>> selectGoals(
            @PathVariable UUID userId,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,  // 👈 ya compila
            @RequestBody @Validated GoalSelectionRequest request
    ) {
        var cmds = request.selections().stream()
                .map(i -> new GoalSelectionCommandPort.SelectionCommand(i.defaultId(), i.active()))
                .toList();

        var res = useCase.execute(userId, cmds, authorization);

        var body = res.stream().map(g ->
                new GoalSelectionResponse(
                        g.getId().toString(),
                        g.getDefaultId(),
                        g.getGoalName(),
                        g.getWeeklyTarget(),
                        g.getActive()
                )
        ).toList();

        return ResponseEntity.ok(body);
    }
}
