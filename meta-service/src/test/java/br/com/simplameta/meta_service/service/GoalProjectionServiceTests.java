package br.com.simplameta.meta_service.service;

import br.com.simplameta.meta_service.model.FinancialGoal;
import br.com.simplameta.meta_service.model.GoalContribution;
import br.com.simplameta.meta_service.repository.GoalContributionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class GoalProjectionServiceTests {

    private final GoalProjectionService service = new GoalProjectionService(null);

    @Test
    void contributionReducesCurrentMonthAndExcessBecomesExtra() {
        FinancialGoal goal = goal(new BigDecimal("1000.00"), YearMonth.now().plusMonths(4));
        GoalContribution contribution = contribution("250.00", LocalDate.now());

        var projection = service.calculateMonthlyProjection(goal, List.of(contribution));

        assertThat(projection.monthlyTarget()).isEqualByComparingTo("200.00");
        assertThat(projection.currentMonthContributed()).isEqualByComparingTo("250.00");
        assertThat(projection.currentMonthRemaining()).isEqualByComparingTo("0.00");
        assertThat(projection.currentMonthExtra()).isEqualByComparingTo("50.00");
    }

    @Test
    void missingClosedMonthAmountBecomesOverdueAndCurrentMonthResets() {
        FinancialGoal goal = goalCreatedMonthsAgo(new BigDecimal("1000.00"), 2, YearMonth.now().plusMonths(2));
        GoalContribution previous = contribution("100.00", YearMonth.now().minusMonths(1).atDay(10));
        GoalContribution current = contribution("50.00", LocalDate.now());

        var projection = service.calculateMonthlyProjection(goal, List.of(previous, current));

        assertThat(projection.monthlyTarget()).isEqualByComparingTo("200.00");
        assertThat(projection.currentMonthRemaining()).isEqualByComparingTo("150.00");
        assertThat(projection.overdueAmount()).isEqualByComparingTo("300.00");
    }

    @Test
    void excessInOneMonthDoesNotPayAnotherMonthsDelay() {
        FinancialGoal goal = goalCreatedMonthsAgo(new BigDecimal("1000.00"), 2, YearMonth.now().plusMonths(2));
        GoalContribution excessInFirstMonth = contribution(
                "400.00",
                YearMonth.now().minusMonths(2).atDay(10)
        );

        var projection = service.calculateMonthlyProjection(goal, List.of(excessInFirstMonth));

        assertThat(projection.monthlyTarget()).isEqualByComparingTo("200.00");
        assertThat(projection.overdueAmount()).isEqualByComparingTo("200.00");
        assertThat(projection.currentMonthRemaining()).isEqualByComparingTo("200.00");
    }

    @Test
    void currentMonthRestartsAtOriginalMonthlyTarget() {
        FinancialGoal goal = goalCreatedMonthsAgo(new BigDecimal("1000.00"), 1, YearMonth.now().plusMonths(3));
        GoalContribution previousMonth = contribution(
                "200.00",
                YearMonth.now().minusMonths(1).atDay(10)
        );

        var projection = service.calculateMonthlyProjection(goal, List.of(previousMonth));

        assertThat(projection.monthlyTarget()).isEqualByComparingTo("200.00");
        assertThat(projection.currentMonthContributed()).isEqualByComparingTo("0.00");
        assertThat(projection.currentMonthRemaining()).isEqualByComparingTo("200.00");
        assertThat(projection.overdueAmount()).isEqualByComparingTo("0.00");
    }

    @Test
    void completedGoalHasNoMonthlyRemainingOrOverdueAmount() {
        FinancialGoal goal = goalCreatedMonthsAgo(new BigDecimal("1000.00"), 1, YearMonth.now().plusMonths(3));
        GoalContribution total = contribution("1000.00", LocalDate.now());

        var projection = service.calculateMonthlyProjection(goal, List.of(total));

        assertThat(projection.currentMonthRemaining()).isEqualByComparingTo("0.00");
        assertThat(projection.overdueAmount()).isEqualByComparingTo("0.00");
    }

    private FinancialGoal goal(BigDecimal target, YearMonth deadline) {
        return goalCreatedMonthsAgo(target, 0, deadline);
    }

    private FinancialGoal goalCreatedMonthsAgo(BigDecimal target, int monthsAgo, YearMonth deadline) {
        return FinancialGoal.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .name("Meta")
                .targetAmount(target)
                .deadline(deadline.atEndOfMonth())
                .createdAt(YearMonth.now().minusMonths(monthsAgo).atDay(1).atStartOfDay().toInstant(ZoneOffset.UTC))
                .updatedAt(Instant.now())
                .build();
    }

    private GoalContribution contribution(String amount, LocalDate date) {
        return GoalContribution.builder()
                .id(UUID.randomUUID())
                .goalId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .amount(new BigDecimal(amount))
                .contributionDate(date)
                .createdAt(Instant.now())
                .build();
    }
}
