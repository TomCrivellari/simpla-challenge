package br.com.simplameta.meta_service.controller;

import br.com.simplameta.meta_service.dto.request.GoalContributionRequest;
import br.com.simplameta.meta_service.dto.response.FinancialGoalResponse;
import br.com.simplameta.meta_service.dto.response.GoalContributionResponse;
import br.com.simplameta.meta_service.service.AuthenticatedUserService;
import br.com.simplameta.meta_service.service.GoalContributionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goals/{goalId}/contributions")
@RequiredArgsConstructor
@Tag(
        name = "Goal Contributions",
        description = "Movements related to financial goal progress"
)
@SecurityRequirement(name = "bearerAuth")
public class GoalContributionController {

    private final GoalContributionService contributionService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    @Operation(
            summary = "List goal contributions",
            description = "Returns all contributions registered for one financial goal."
    )
    public ResponseEntity<List<GoalContributionResponse>> findAll(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID goalId
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);

        return ResponseEntity.ok(contributionService.findAll(userId, goalId));
    }

    @PostMapping
    @Operation(
            summary = "Create goal contribution",
            description = "Registers a contribution and returns the updated goal progress."
    )
    public ResponseEntity<FinancialGoalResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID goalId,
            @Valid @RequestBody GoalContributionRequest request
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);
        FinancialGoalResponse response = contributionService.create(
                userId,
                goalId,
                jwt.getTokenValue(),
                request
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{contributionId}")
    @Operation(
            summary = "Delete goal contribution",
            description = "Deletes a contribution owned by the authenticated user."
    )
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID goalId,
            @PathVariable UUID contributionId
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);
        contributionService.delete(userId, goalId, contributionId, jwt.getTokenValue());

        return ResponseEntity.noContent().build();
    }
}
