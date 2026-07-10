package br.com.simplameta.finance_service.controller;

import br.com.simplameta.finance_service.dto.request.TransactionRequest;
import br.com.simplameta.finance_service.dto.response.TransactionResponse;
import br.com.simplameta.finance_service.service.AuthenticatedUserService;
import br.com.simplameta.finance_service.service.TransactionService;
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
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
@Tag(
        name = "Transactions",
        description = "Authenticated user's incomes and expenses"
)
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;
    private final AuthenticatedUserService authenticatedUserService;

    @GetMapping
    @Operation(
            summary = "List transactions",
            description = "Returns all incomes and expenses owned by the authenticated user."
    )
    public ResponseEntity<List<TransactionResponse>> findAll(
            @AuthenticationPrincipal Jwt jwt
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);

        return ResponseEntity.ok(transactionService.findAll(userId));
    }

    @GetMapping("/{transactionId}")
    @Operation(
            summary = "Get transaction",
            description = "Returns one income or expense owned by the authenticated user."
    )
    public ResponseEntity<TransactionResponse> findById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID transactionId
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);

        return ResponseEntity.ok(transactionService.findById(userId, transactionId));
    }

    @PostMapping
    @Operation(
            summary = "Create transaction",
            description = "Creates an income or expense. Dashboard balance changes automatically from transaction totals."
    )
    public ResponseEntity<TransactionResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody TransactionRequest request
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);
        TransactionResponse response = transactionService.create(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{transactionId}")
    @Operation(
            summary = "Update transaction",
            description = "Updates an income or expense. Dashboard balance changes automatically from transaction totals."
    )
    public ResponseEntity<TransactionResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID transactionId,
            @Valid @RequestBody TransactionRequest request
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);

        return ResponseEntity.ok(transactionService.update(userId, transactionId, request));
    }

    @DeleteMapping("/{transactionId}")
    @Operation(
            summary = "Delete transaction",
            description = "Deletes an income or expense owned by the authenticated user."
    )
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID transactionId
    ) {
        UUID userId = authenticatedUserService.getUserId(jwt);
        transactionService.delete(userId, transactionId);

        return ResponseEntity.noContent().build();
    }
}
