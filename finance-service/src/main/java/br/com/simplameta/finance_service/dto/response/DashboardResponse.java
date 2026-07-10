package br.com.simplameta.finance_service.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResponse(

        BigDecimal balance,
        BigDecimal totalIncomes,
        BigDecimal totalExpenses,
        List<TransactionResponse> recentTransactions

) {
}
