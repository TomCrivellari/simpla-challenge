package br.com.simplameta.auth_service.dto.response;

import br.com.simplameta.auth_service.model.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record RegisterResponse(

        UUID id,
        String fullName,
        String email,
        UserStatus status,
        Instant createdAt

) {
}