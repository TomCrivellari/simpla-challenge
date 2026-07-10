package br.com.simplameta.auth_service.service;

import br.com.simplameta.auth_service.dto.request.RegisterRequest;
import br.com.simplameta.auth_service.dto.response.RegisterResponse;
import br.com.simplameta.auth_service.exception.EmailAlreadyRegisteredException;
import br.com.simplameta.auth_service.model.User;
import br.com.simplameta.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        validateEmailAvailability(normalizedEmail);

        String passwordHash = passwordEncoder.encode(request.password());

        User user = User.create(
                request.fullName(),
                normalizedEmail,
                passwordHash
        );

        User savedUser = saveUser(user, normalizedEmail);

        return toResponse(savedUser);
    }

    private User saveUser(User user, String email) {
        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }

    private void validateEmailAvailability(String email) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }

    private String normalizeEmail(String email) {
        return email
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private RegisterResponse toResponse(User user) {
        return new RegisterResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
