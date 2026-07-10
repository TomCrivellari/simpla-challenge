package br.com.simplameta.finance_service.dto.response;

import br.com.simplameta.finance_service.model.Transaction;
import br.com.simplameta.finance_service.model.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponse(

        UUID id,
        TransactionType type,
        String description,
        BigDecimal amount,
        String category,
        LocalDate transactionDate,
        Instant createdAt,
        Instant updatedAt

) {

    public static TransactionResponse from(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getType(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCategory(),
                transaction.getTransactionDate(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt()
        );
    }
}
