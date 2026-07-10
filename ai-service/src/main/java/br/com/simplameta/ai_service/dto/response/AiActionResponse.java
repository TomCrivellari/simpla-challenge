package br.com.simplameta.ai_service.dto.response;

import java.util.Map;

public record AiActionResponse(

        String type,
        String label,
        Map<String, Object> payload

) {
}
