package br.com.simplameta.ai_service.service;

import br.com.simplameta.ai_service.client.FinanceServiceClient;
import br.com.simplameta.ai_service.client.MetaServiceClient;
import br.com.simplameta.ai_service.client.dto.FinanceDashboardResponse;
import br.com.simplameta.ai_service.client.dto.MetaGoalResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinancialContextService {

    private static final String BEARER_PREFIX = "Bearer ";

    private final FinanceServiceClient financeServiceClient;
    private final MetaServiceClient metaServiceClient;

    public FinancialContextService(
            FinanceServiceClient financeServiceClient,
            MetaServiceClient metaServiceClient
    ) {
        this.financeServiceClient = financeServiceClient;
        this.metaServiceClient = metaServiceClient;
    }

    public FinancialContext getContext(String accessToken) {
        String authorization = BEARER_PREFIX + accessToken;

        FinanceDashboardResponse dashboard = financeServiceClient.getDashboard(authorization);
        List<MetaGoalResponse> goals = metaServiceClient.findGoals(authorization);

        return new FinancialContext(dashboard, goals);
    }

    public record FinancialContext(
            FinanceDashboardResponse dashboard,
            List<MetaGoalResponse> goals
    ) {
    }
}
