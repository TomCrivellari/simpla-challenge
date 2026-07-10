package br.com.simplameta.ai_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChatMessageRequest(

        @NotBlank(message = "Role is required")
        @Pattern(regexp = "user|assistant", message = "Role must be user or assistant")
        String role,

        @NotBlank(message = "Content is required")
        @Size(max = 2000, message = "Content must have at most 2000 characters")
        String content

) {
}
