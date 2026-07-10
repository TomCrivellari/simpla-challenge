package br.com.simplameta.finance_service.controller;

import br.com.simplameta.finance_service.dto.response.DashboardResponse;
import br.com.simplameta.finance_service.service.AuthenticatedUserService;
import br.com.simplameta.finance_service.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Tag(
        name = "Dashboard",
        description = "Authenticated user's balance and recent financial activity"
)
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    @Operation(
            summary = "Get financial dashboard",
            description = "Returns current balance, income total, expense total and recent transactions."
    )
    public ResponseEntity<DashboardResponse> getDashboard(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);
        DashboardResponse response = dashboardService.getDashboard(userId);

        return ResponseEntity.ok(response);
    }
}
