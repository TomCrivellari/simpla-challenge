package br.com.simplameta.meta_service.service;

import br.com.simplameta.meta_service.client.FinanceServiceClient;
import br.com.simplameta.meta_service.client.dto.FinanceTransactionRequest;
import br.com.simplameta.meta_service.client.dto.FinanceTransactionResponse;
import br.com.simplameta.meta_service.exception.FinanceServiceCommunicationException;
import br.com.simplameta.meta_service.model.FinancialGoal;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FinanceTransactionService {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String GOAL_CONTRIBUTION_CATEGORY = "Financial Goal";

    private final FinanceServiceClient financeServiceClient;

    public UUID createGoalContributionExpense(
            String accessToken,
            FinancialGoal goal,
            BigDecimal amount,
            LocalDate contributionDate
    ) {
        FinanceTransactionRequest request = new FinanceTransactionRequest(
                "EXPENSE",
                "Aporte para meta: " + goal.getName(),
                amount,
                GOAL_CONTRIBUTION_CATEGORY,
                contributionDate
        );

        try {
            FinanceTransactionResponse response = financeServiceClient.createTransaction(
                    authorization(accessToken),
                    request
            );

            return response.id();
        } catch (FeignException exception) {
            if (exception.status() == 422) {
                throw new br.com.simplameta.meta_service.exception.InsufficientBalanceException();
            }
            throw new FinanceServiceCommunicationException();
        }
    }

    public void deleteGoalContributionExpense(String accessToken, UUID financeTransactionId) {
        try {
            financeServiceClient.deleteTransaction(
                    authorization(accessToken),
                    financeTransactionId
            );
        } catch (FeignException exception) {
            throw new FinanceServiceCommunicationException();
        }
    }

    private String authorization(String accessToken) {
        return BEARER_PREFIX + accessToken;
    }
}
