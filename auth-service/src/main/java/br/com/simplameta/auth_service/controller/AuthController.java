package br.com.simplameta.auth_service.controller;

import br.com.simplameta.auth_service.dto.request.LoginRequest;
import br.com.simplameta.auth_service.dto.request.RegisterRequest;
import br.com.simplameta.auth_service.dto.response.LoginResponse;
import br.com.simplameta.auth_service.dto.response.MeResponse;
import br.com.simplameta.auth_service.dto.response.RegisterResponse;
import br.com.simplameta.auth_service.service.LoginService;
import br.com.simplameta.auth_service.service.MeService;
import br.com.simplameta.auth_service.service.RegisterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "Endpoints for user authentication and registration"
)
public class AuthController {

    private final RegisterService registerService;
    private final LoginService loginService;
    private final MeService meService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Creates a new account using full name, email and password."
    )
    public ResponseEntity<RegisterResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        RegisterResponse response = registerService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates an account using email and password and returns a Bearer access token."
    )
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = loginService.login(request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get authenticated user",
            description = "Returns the authenticated user's id and full name.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<MeResponse> me(
            @AuthenticationPrincipal Jwt jwt
    ) {
        MeResponse response = meService.getAuthenticatedUser(jwt);

        return ResponseEntity.ok(response);
    }
}
