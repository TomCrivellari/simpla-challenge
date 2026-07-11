package br.com.simplameta.auth_service.service;

import br.com.simplameta.auth_service.dto.request.LoginRequest;
import br.com.simplameta.auth_service.dto.response.LoginResponse;
import br.com.simplameta.auth_service.exception.AccountUnavailableException;
import br.com.simplameta.auth_service.exception.InvalidLoginCredentialsException;
import br.com.simplameta.auth_service.exception.UserNotRegisteredException;
import br.com.simplameta.auth_service.model.User;
import br.com.simplameta.auth_service.model.UserStatus;
import br.com.simplameta.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailIgnoreCase(normalizeEmail(request.email()))
                .orElseThrow(UserNotRegisteredException::new);

        validateAccountAvailability(user);
        validatePassword(user, request.password());

        user.resetFailedLoginAttempts();

        String accessToken = jwtTokenService.generateAccessToken(user);

        return new LoginResponse(
                accessToken,
                "Bearer",
                jwtTokenService.accessTokenExpiresInSeconds(),
                toResponseUser(user)
        );
    }

    private void validateAccountAvailability(User user) {
        if (user.getStatus() == UserStatus.LOCKED) {
            throw new AccountUnavailableException("Account is locked");
        }

        if (user.getStatus() == UserStatus.DISABLED) {
            throw new AccountUnavailableException("Account is disabled");
        }
    }

    private void validatePassword(User user, String rawPassword) {
        if (passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return;
        }

        user.registerFailedLogin();

        throw new InvalidLoginCredentialsException();
    }

    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private LoginResponse.User toResponseUser(User user) {
        return new LoginResponse.User(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getStatus()
        );
    }
}
