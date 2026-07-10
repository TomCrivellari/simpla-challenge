package br.com.simplameta.ai_service.client;

import br.com.simplameta.ai_service.client.dto.FinanceDashboardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "finance-service",
        url = "${services.finance.url}"
)
public interface FinanceServiceClient {

    @GetMapping("/api/v1/dashboard")
    FinanceDashboardResponse getDashboard(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
    );
}
