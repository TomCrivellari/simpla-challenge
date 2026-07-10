package br.com.simplameta.finance_service.service;

import br.com.simplameta.finance_service.exception.AuthenticatedUserNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticatedUserService {

    public UUID getUserId(Jwt jwt) {
        if (jwt == null || jwt.getSubject() == null || jwt.getSubject().isBlank()) {
            throw new AuthenticatedUserNotFoundException();
        }

        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException exception) {
            throw new AuthenticatedUserNotFoundException();
        }
    }
}
