package br.com.simplameta.ai_service.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record AiChatRequest(

        @NotEmpty(message = "Messages are required")
        @Size(max = 20, message = "Send at most 20 messages")
        List<@Valid ChatMessageRequest> messages

) {
}
