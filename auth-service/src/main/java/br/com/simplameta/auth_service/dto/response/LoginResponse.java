package br.com.simplameta.auth_service.dto.response;

import br.com.simplameta.auth_service.model.UserStatus;

import java.util.UUID;

public record LoginResponse(

        String accessToken,
        String tokenType,
        long expiresIn,
        User user

) {

    public record User(

            UUID id,
            String fullName,
            String email,
            UserStatus status

    ) {
    }
}
