package br.com.simplameta.meta_service.dto.response;

import br.com.simplameta.meta_service.model.GoalContribution;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record GoalContributionResponse(

        UUID id,
        UUID goalId,
        BigDecimal amount,
        String description,
        boolean affectBalance,
        UUID financeTransactionId,
        LocalDate contributionDate,
        Instant createdAt

) {

    public static GoalContributionResponse from(GoalContribution contribution) {
        return new GoalContributionResponse(
                contribution.getId(),
                contribution.getGoalId(),
                contribution.getAmount(),
                contribution.getDescription(),
                contribution.isAffectBalance(),
                contribution.getFinanceTransactionId(),
                contribution.getContributionDate(),
                contribution.getCreatedAt()
        );
    }
}
