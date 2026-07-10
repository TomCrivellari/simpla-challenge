package br.com.simplameta.ai_service.controller;

import br.com.simplameta.ai_service.dto.request.AiChatRequest;
import br.com.simplameta.ai_service.dto.response.AiChatResponse;
import br.com.simplameta.ai_service.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@Tag(
        name = "AI Assistant",
        description = "Chat assistant for financial goal planning"
)
@SecurityRequirement(name = "bearerAuth")
public class AiChatController {

    private final AiChatService aiChatService;

    public AiChatController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @PostMapping("/chat")
    @Operation(
            summary = "Chat about financial goals",
            description = "Returns goal suggestions and saving plans. The user must confirm any suggested action in the frontend."
    )
    public ResponseEntity<AiChatResponse> chat(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody AiChatRequest request
    ) {
        return ResponseEntity.ok(aiChatService.chat(jwt.getTokenValue(), request));
    }
}
