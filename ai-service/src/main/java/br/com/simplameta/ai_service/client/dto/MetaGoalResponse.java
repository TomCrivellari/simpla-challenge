package br.com.simplameta.ai_service.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MetaGoalResponse(

        UUID id,
        String name,
        String description,
        BigDecimal targetAmount,
        BigDecimal currentAmount,
        BigDecimal remainingAmount,
        BigDecimal monthlyRequiredSavings,
        BigDecimal progressPercentage,
        LocalDate deadline

) {
}
