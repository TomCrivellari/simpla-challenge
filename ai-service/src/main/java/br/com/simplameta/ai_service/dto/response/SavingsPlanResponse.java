package br.com.simplameta.ai_service.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record SavingsPlanResponse(

        BigDecimal monthlyAmount,
        BigDecimal weeklyAmount,
        BigDecimal dailyAmount,
        Integer monthsUntilDeadline,
        List<String> notes

) {
}
