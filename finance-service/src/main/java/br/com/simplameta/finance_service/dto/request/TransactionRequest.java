package br.com.simplameta.finance_service.dto.request;

import br.com.simplameta.finance_service.model.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionRequest(

        @NotNull(message = "Type is required")
        TransactionType type,

        @NotBlank(message = "Description is required")
        @Size(max = 150, message = "Description must have at most 150 characters")
        String description,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @Size(max = 80, message = "Category must have at most 80 characters")
        String category,

        @NotNull(message = "Transaction date is required")
        LocalDate transactionDate

) {
}
