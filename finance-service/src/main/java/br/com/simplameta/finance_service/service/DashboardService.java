package br.com.simplameta.finance_service.service;

import br.com.simplameta.finance_service.dto.response.DashboardResponse;
import br.com.simplameta.finance_service.dto.response.TransactionResponse;
import br.com.simplameta.finance_service.model.TransactionType;
import br.com.simplameta.finance_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private static final int RECENT_TRANSACTIONS_LIMIT = 5;

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(UUID userId) {
        BigDecimal totalIncomes = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);
        BigDecimal balance = totalIncomes.subtract(totalExpenses);

        var recentTransactions = transactionRepository
                .findByUserId(userId, Sort.by(
                        Sort.Order.desc("transactionDate"),
                        Sort.Order.desc("createdAt")
                ))
                .stream()
                .limit(RECENT_TRANSACTIONS_LIMIT)
                .map(TransactionResponse::from)
                .toList();

        return new DashboardResponse(
                balance,
                totalIncomes,
                totalExpenses,
                recentTransactions
        );
    }
}
