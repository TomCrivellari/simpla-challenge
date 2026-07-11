package br.com.simplameta.ai_service.service;

import br.com.simplameta.ai_service.dto.request.AiChatRequest;
import br.com.simplameta.ai_service.dto.response.AiChatResponse;
import br.com.simplameta.ai_service.dto.response.SavingsPlanResponse;
import br.com.simplameta.ai_service.dto.response.SuggestedGoalResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;

@Service
public class AiChatService {

    private final FinancialContextService financialContextService;
    private final GeminiClientService geminiClientService;

    public AiChatService(
            FinancialContextService financialContextService,
            GeminiClientService geminiClientService
    ) {
        this.financialContextService = financialContextService;
        this.geminiClientService = geminiClientService;
    }

    public AiChatResponse chat(String accessToken, AiChatRequest request) {
        var financialContext = financialContextService.getContext(accessToken);

        AiChatResponse response = geminiClientService.createChatResponse(request.messages(), financialContext);
        return normalizeMonthlyPlan(response);
    }

    private AiChatResponse normalizeMonthlyPlan(AiChatResponse response) {
        SuggestedGoalResponse goal = response.suggestedGoal();
        if (goal == null || goal.targetAmount() == null || goal.deadline() == null) {
            return response;
        }

        YearMonth currentMonth = YearMonth.now();
        YearMonth deadlineMonth = YearMonth.from(goal.deadline());
        long plannedMonths = Math.max(
                1,
                ChronoUnit.MONTHS.between(currentMonth, deadlineMonth) + 1
        );
        BigDecimal monthlyAmount = goal.targetAmount()
                .divide(BigDecimal.valueOf(plannedMonths), 2, RoundingMode.HALF_UP);

        SuggestedGoalResponse normalizedGoal = new SuggestedGoalResponse(
                goal.name(),
                goal.description(),
                goal.targetAmount(),
                goal.deadline(),
                monthlyAmount
        );

        SavingsPlanResponse currentPlan = response.savingsPlan();
        SavingsPlanResponse normalizedPlan = new SavingsPlanResponse(
                monthlyAmount,
                currentPlan == null ? BigDecimal.ZERO : currentPlan.weeklyAmount(),
                currentPlan == null ? BigDecimal.ZERO : currentPlan.dailyAmount(),
                Math.toIntExact(plannedMonths),
                currentPlan == null ? java.util.List.of() : currentPlan.notes()
        );

        return new AiChatResponse(
                response.message(),
                normalizedGoal,
                normalizedPlan,
                response.followUpQuestions(),
                response.actions()
        );
    }
}
