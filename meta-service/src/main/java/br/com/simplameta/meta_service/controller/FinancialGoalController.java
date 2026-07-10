package br.com.simplameta.meta_service.controller;

import br.com.simplameta.meta_service.dto.request.FinancialGoalRequest;
import br.com.simplameta.meta_service.dto.response.FinancialGoalResponse;
import br.com.simplameta.meta_service.service.AuthenticatedUserService;
import br.com.simplameta.meta_service.service.FinancialGoalService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/goals")
@RequiredArgsConstructor
@Tag(
        name = "Financial Goals",
        description = "Authenticated user's financial goals and progress projections"
)
@SecurityRequirement(name = "bearerAuth")
public class FinancialGoalController {

    private final FinancialGoalService goalService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    @Operation(
            summary = "List financial goals",
            description = "Returns all financial goals owned by the authenticated user with calculated progress."
    )
    public ResponseEntity<List<FinancialGoalResponse>> findAll(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);

        return ResponseEntity.ok(goalService.findAll(userId));
    }

    @GetMapping("/{goalId}")
    @Operation(
            summary = "Get financial goal",
            description = "Returns one financial goal with current amount, remaining amount, monthly required savings and progress percentage."
    )
    public ResponseEntity<FinancialGoalResponse> findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID goalId
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);

        return ResponseEntity.ok(goalService.findById(userId, goalId));
    }

    @PostMapping
    @Operation(
            summary = "Create financial goal",
            description = "Creates a financial goal using a target amount and deadline."
    )
    public ResponseEntity<FinancialGoalResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody FinancialGoalRequest request
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);
        FinancialGoalResponse response = goalService.create(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{goalId}")
    @Operation(
            summary = "Update financial goal",
            description = "Updates a financial goal owned by the authenticated user."
    )
    public ResponseEntity<FinancialGoalResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID goalId,
            @Valid @RequestBody FinancialGoalRequest request
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);

        return ResponseEntity.ok(goalService.update(userId, goalId, request));
    }

    @DeleteMapping("/{goalId}")
    @Operation(
            summary = "Delete financial goal",
            description = "Deletes a financial goal and its related contributions."
    )
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID goalId
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);
        goalService.delete(userId, goalId, jwt.getTokenValue());

        return ResponseEntity.noContent().build();
    }
}
