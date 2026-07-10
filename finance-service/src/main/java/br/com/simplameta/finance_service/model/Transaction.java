package br.com.simplameta.finance_service.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "transactions")
public class Transaction {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private TransactionType type;

    @Column(name = "description", nullable = false, length = 150)
    private String description;

    @Column(name = "amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(name = "category", length = 80)
    private String category;

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public static Transaction create(
            UUID userId,
            TransactionType type,
            String description,
            BigDecimal amount,
            String category,
            LocalDate transactionDate
    ) {
        Instant now = Instant.now();

        return Transaction.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .type(type)
                .description(description.trim())
                .amount(amount)
                .category(normalizeCategory(category))
                .transactionDate(transactionDate)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public void update(
            TransactionType type,
            String description,
            BigDecimal amount,
            String category,
            LocalDate transactionDate
    ) {
        this.type = type;
        this.description = description.trim();
        this.amount = amount;
        this.category = normalizeCategory(category);
        this.transactionDate = transactionDate;
        this.updatedAt = Instant.now();
    }

    private static String normalizeCategory(String category) {
        if (category == null || category.isBlank()) {
            return null;
        }

        return category.trim();
    }
}
