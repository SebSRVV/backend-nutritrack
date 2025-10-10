// modules/goals/web/GoalsSelectionController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.SelectGoalsUseCase;
import com.sebsrvv.app.modules.goals.domain.port.GoalSelectionCommandPort;
import com.sebsrvv.app.modules.goals.web.dto.GoalSelectionRequest;
import com.sebsrvv.app.modules.goals.web.dto.GoalSelectionResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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
            @RequestBody @Validated GoalSelectionRequest request
    ) {
        var cmds = request.selections().stream()
                .map(i -> new GoalSelectionCommandPort.SelectionCommand(i.defaultId(), i.active()))
                .toList();

        var res = useCase.execute(userId, cmds);

        var body = res.stream().map(g ->
                new GoalSelectionResponse(
                        g.getId().toString(),
                        g.getDefaultId(),
                        g.getGoalName(),
                        g.getWeeklyTarget(),
                        g.getActive()
                )
        ).collect(Collectors.toList());

        return ResponseEntity.ok(body);
    }
}
