package br.com.simplameta.auth_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.Instant;
import java.util.Locale;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private UserStatus status;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts;

    @Column(name = "locked_until")
    private Instant lockedUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static User create(
            String fullName,
            String email,
            String passwordHash
    ) {
        Instant now = Instant.now();

        return User.builder()
                .id(UUID.randomUUID())
                .fullName(fullName.trim())
                .email(email.trim().toLowerCase(Locale.ROOT))
                .passwordHash(passwordHash)
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .failedLoginAttempts(0)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.status = UserStatus.ACTIVE;
        this.updatedAt = Instant.now();
    }

    public void registerFailedLogin() {
        this.failedLoginAttempts++;
        this.updatedAt = Instant.now();
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.updatedAt = Instant.now();
    }

    public void lockUntil(Instant lockedUntil) {
        this.status = UserStatus.LOCKED;
        this.lockedUntil = lockedUntil;
        this.updatedAt = Instant.now();
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
        this.updatedAt = Instant.now();
    }
}
