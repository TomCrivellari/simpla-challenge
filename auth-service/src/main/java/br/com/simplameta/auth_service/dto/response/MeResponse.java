package br.com.simplameta.auth_service.dto.response;

import java.util.UUID;

public record MeResponse(

        UUID id,
        String fullName

) {
}
