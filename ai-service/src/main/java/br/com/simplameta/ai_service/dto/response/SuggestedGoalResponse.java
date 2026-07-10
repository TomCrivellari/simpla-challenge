package br.com.simplameta.ai_service.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SuggestedGoalResponse(

        String name,
        String description,
        BigDecimal targetAmount,
        LocalDate deadline,
        BigDecimal monthlyRequiredSavings

) {
}
