package br.com.simplameta.meta_service.service;

import br.com.simplameta.meta_service.dto.response.FinancialGoalResponse;
import br.com.simplameta.meta_service.model.FinancialGoal;
import br.com.simplameta.meta_service.model.GoalContribution;
import br.com.simplameta.meta_service.repository.GoalContributionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GoalProjectionService {

    private static final int MONEY_SCALE = 2;
    private static final int PERCENTAGE_SCALE = 2;
    private static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);

    private final GoalContributionRepository contributionRepository;

    public FinancialGoalResponse toResponse(FinancialGoal goal, UUID userId) {
        List<GoalContribution> contributions = contributionRepository.findAllForProjection(goal.getId(), userId);
        BigDecimal currentAmount = contributions.stream()
                .map(GoalContribution::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal remainingAmount = goal.getTargetAmount().subtract(currentAmount).max(BigDecimal.ZERO);
        MonthlyProjection monthly = calculateMonthlyProjection(goal, contributions);
        BigDecimal progressPercentage = calculateProgressPercentage(currentAmount, goal.getTargetAmount());

        return FinancialGoalResponse.from(
                goal,
                money(currentAmount),
                money(remainingAmount),
                monthly.currentMonthRemaining(),
                monthly.monthlyTarget(),
                monthly.currentMonthContributed(),
                monthly.currentMonthRemaining(),
                monthly.currentMonthExtra(),
                monthly.overdueAmount(),
                progressPercentage
        );
    }

    MonthlyProjection calculateMonthlyProjection(FinancialGoal goal, List<GoalContribution> contributions) {
        YearMonth startMonth = YearMonth.from(goal.getCreatedAt().atZone(ZoneOffset.UTC));
        YearMonth deadlineMonth = YearMonth.from(goal.getDeadline());
        YearMonth currentMonth = YearMonth.now();
        long plannedMonths = Math.max(1, ChronoUnit.MONTHS.between(startMonth, deadlineMonth) + 1);
        BigDecimal monthlyTarget = money(goal.getTargetAmount().divide(BigDecimal.valueOf(plannedMonths), MONEY_SCALE, RoundingMode.HALF_UP));

        BigDecimal contributedThisMonth = sumForMonth(contributions, currentMonth);
        BigDecimal currentMonthRemaining = monthlyTarget.subtract(contributedThisMonth).max(BigDecimal.ZERO);
        BigDecimal currentMonthExtra = contributedThisMonth.subtract(monthlyTarget).max(BigDecimal.ZERO);

        long elapsedClosedMonths = currentMonth.isAfter(startMonth)
                ? Math.min(plannedMonths, ChronoUnit.MONTHS.between(startMonth, currentMonth))
                : 0;
        // Cada competencia e independente. Um aporte excedente feito em um mes
        // reduz o saldo total da meta, mas nao quita a mensalidade de outro mes.
        BigDecimal overdueAmount = BigDecimal.ZERO;
        for (long monthIndex = 0; monthIndex < elapsedClosedMonths; monthIndex++) {
            YearMonth closedMonth = startMonth.plusMonths(monthIndex);
            BigDecimal contributedInClosedMonth = sumForMonth(contributions, closedMonth);
            overdueAmount = overdueAmount.add(
                    monthlyTarget.subtract(contributedInClosedMonth).max(BigDecimal.ZERO)
            );
        }

        BigDecimal totalContributed = contributions.stream()
                .map(GoalContribution::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        boolean goalCompleted = totalContributed.compareTo(goal.getTargetAmount()) >= 0;

        if (goalCompleted) {
            currentMonthRemaining = BigDecimal.ZERO;
            overdueAmount = BigDecimal.ZERO;
        } else if (currentMonth.isAfter(deadlineMonth)) {
            currentMonthRemaining = BigDecimal.ZERO;
            currentMonthExtra = contributedThisMonth;
        }

        return new MonthlyProjection(
                money(monthlyTarget),
                money(contributedThisMonth),
                money(currentMonthRemaining),
                money(currentMonthExtra),
                money(overdueAmount)
        );
    }

    private BigDecimal sumForMonth(List<GoalContribution> contributions, YearMonth month) {
        return contributions.stream()
                .filter(item -> YearMonth.from(item.getContributionDate()).equals(month))
                .map(GoalContribution::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateProgressPercentage(BigDecimal currentAmount, BigDecimal targetAmount) {
        if (targetAmount.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO.setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
        return currentAmount.multiply(ONE_HUNDRED)
                .divide(targetAmount, PERCENTAGE_SCALE, RoundingMode.HALF_UP)
                .min(ONE_HUNDRED)
                .setScale(PERCENTAGE_SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }

    record MonthlyProjection(
            BigDecimal monthlyTarget,
            BigDecimal currentMonthContributed,
            BigDecimal currentMonthRemaining,
            BigDecimal currentMonthExtra,
            BigDecimal overdueAmount
    ) { }
}
