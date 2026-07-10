package br.com.simplameta.meta_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "financial_goals")
public class FinancialGoal {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "target_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal targetAmount;

    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static FinancialGoal create(
            UUID userId,
            String name,
            String description,
            BigDecimal targetAmount,
            LocalDate deadline
    ) {
        Instant now = Instant.now();

        return FinancialGoal.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .name(name.trim())
                .description(normalizeDescription(description))
                .targetAmount(targetAmount)
                .deadline(deadline)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void update(
            String name,
            String description,
            BigDecimal targetAmount,
            LocalDate deadline
    ) {
        this.name = name.trim();
        this.description = normalizeDescription(description);
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.updatedAt = Instant.now();
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}
