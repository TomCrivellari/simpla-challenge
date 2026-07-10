package br.com.simplameta.meta_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialGoalRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 120, message = "Name must have at most 120 characters")
        String name,

        @Size(max = 255, message = "Description must have at most 255 characters")
        String description,

        @NotNull(message = "Target amount is required")
        @DecimalMin(value = "0.01", message = "Target amount must be greater than zero")
        BigDecimal targetAmount,

        @NotNull(message = "Deadline is required")
        @FutureOrPresent(message = "Deadline must be today or a future date")
        LocalDate deadline

) {
}
