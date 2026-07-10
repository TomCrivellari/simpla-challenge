package br.com.simplameta.meta_service.dto.response;

import br.com.simplameta.meta_service.model.FinancialGoal;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record FinancialGoalResponse(

        UUID id,
        String name,
        String description,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal remainingAmount,
        BigDecimal monthlyRequiredSavings,
        BigDecimal progressPercentage,
        LocalDate deadline,
        Instant createdAt,
        Instant updatedAt

) {

    public static FinancialGoalResponse from(
            FinancialGoal goal,
            BigDecimal currentAmount,
            BigDecimal remainingAmount,
            BigDecimal monthlyRequiredSavings,
            BigDecimal progressPercentage
    ) {
        return new FinancialGoalResponse(
                goal.getId(),
                goal.getName(),
                goal.getDescription(),
                goal.getTargetAmount(),
                currentAmount,
                remainingAmount,
                monthlyRequiredSavings,
                progressPercentage,
                goal.getDeadline(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }
}
