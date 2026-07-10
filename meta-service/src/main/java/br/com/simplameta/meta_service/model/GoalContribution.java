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
@Table(name = "goal_contributions")
public class GoalContribution {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "goal_id", nullable = false, updatable = false)
    private UUID goalId;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "affect_balance", nullable = false)
    private boolean affectBalance;

    @Column(name = "finance_transaction_id")
    private UUID financeTransactionId;

    @Column(name = "contribution_date", nullable = false)
    private LocalDate contributionDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public static GoalContribution create(
            UUID goalId,
            UUID userId,
            BigDecimal amount,
            String description,
            boolean affectBalance,
            UUID financeTransactionId,
            LocalDate contributionDate
    ) {
        return GoalContribution.builder()
                .id(UUID.randomUUID())
                .goalId(goalId)
                .userId(userId)
                .amount(amount)
                .description(normalizeDescription(description))
                .affectBalance(affectBalance)
                .financeTransactionId(financeTransactionId)
                .contributionDate(contributionDate)
                .createdAt(Instant.now())
                .build();
    }

    private static String normalizeDescription(String description) {
        if (description == null || description.isBlank()) {
            return null;
        }

        return description.trim();
    }
}
