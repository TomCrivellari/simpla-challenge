package br.com.simplameta.auth_service.service;

import br.com.simplameta.auth_service.dto.response.MeResponse;
import br.com.simplameta.auth_service.exception.AuthenticatedUserNotFoundException;
import br.com.simplameta.auth_service.model.User;
import br.com.simplameta.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MeService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MeResponse getAuthenticatedUser(Jwt jwt) {
        UUID userId = resolveUserId(jwt);

        User user = userRepository.findById(userId)
                .orElseThrow(AuthenticatedUserNotFoundException::new);

        return new MeResponse(
                user.getId(),
                user.getFullName()
        );
    }

    private UUID resolveUserId(Jwt jwt) {
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (IllegalArgumentException exception) {
            throw new AuthenticatedUserNotFoundException();
        }
    }
}
