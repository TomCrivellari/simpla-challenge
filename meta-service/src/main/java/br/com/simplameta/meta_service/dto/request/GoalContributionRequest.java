package br.com.simplameta.meta_service.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GoalContributionRequest(

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @Size(max = 150, message = "Description must have at most 150 characters")
        String description,

        @NotNull(message = "Contribution date is required")
        LocalDate contributionDate,

        Boolean affectBalance

) {

    public boolean shouldAffectBalance() {
        return affectBalance == null || affectBalance;
    }
}
