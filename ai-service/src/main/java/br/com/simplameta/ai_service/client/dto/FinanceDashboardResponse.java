package br.com.simplameta.ai_service.client.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record FinanceDashboardResponse(

        BigDecimal balance,
        BigDecimal totalIncomes,
        BigDecimal totalExpenses,
        List<Map<String, Object>> recentTransactions

) {
}
