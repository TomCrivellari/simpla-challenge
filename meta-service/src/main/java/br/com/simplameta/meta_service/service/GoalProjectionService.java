package br.com.simplameta.meta_service.service;

import br.com.simplameta.meta_service.dto.response.FinancialGoalResponse;
import br.com.simplameta.meta_service.model.FinancialGoal;
import br.com.simplameta.meta_service.repository.GoalContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalProjectionService {

    private static final int MONEY_SCALE = 2;
    private static final int PERCENTAGE_SCALE = 2;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final GoalContributionRepository contributionRepository;

    public FinancialGoalResponse toResponse(FinancialGoal goal, UUID userId) {
        BigDecimal currentAmount = contributionRepository.sumAmountByGoalIdAndUserId(goal.getId(), userId);
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentAmount).max(BigDecimal.ZERO);
        BigDecimal monthlyRequiredSavings = calculateMonthlyRequiredSavings(remainingAmount, goal.getDeadline());
        BigDecimal progressPercentage = calculateProgressPercentage(currentAmount, goal.getTargetAmount());

        return FinancialGoalResponse.from(
                goal,
                currentAmount.setScale(MONEY_SCALE, RoundingMode.HALF_UP),
                remainingAmount.setScale(MONEY_SCALE, RoundingMode.HALF_UP),
                monthlyRequiredSavings,
                progressPercentage
        );
    }

    private BigDecimal calculateMonthlyRequiredSavings(BigDecimal remainingAmount, LocalDate deadline) {
        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
        }

        long monthsUntilDeadline = Math.max(1, ChronoUnit.MONTHS.between(
                LocalDate.now().withDayOfMonth(1),
                deadline.withDayOfMonth(1)
        ) + 1);

        return remainingAmount.divide(BigDecimal.valueOf(monthsUntilDeadline), MONEY_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateProgressPercentage(BigDecimal currentAmount, BigDecimal targetAmount) {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
        }

        BigDecimal percentage = currentAmount
                .multiply(ONE_HUNDRED)
                .divide(targetAmount, PERCENTAGE_SCALE, RoundingMode.HALF_UP);

        return percentage.min(ONE_HUNDRED).setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }
}
