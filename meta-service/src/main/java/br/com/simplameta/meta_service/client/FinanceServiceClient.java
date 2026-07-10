package br.com.simplameta.meta_service.client;

import br.com.simplameta.meta_service.client.dto.FinanceTransactionRequest;
import br.com.simplameta.meta_service.client.dto.FinanceTransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.UUID;

@FeignClient(
        name = "finance-service",
        url = "${services.finance.url}"
)
public interface FinanceServiceClient {

    @PostMapping("/api/v1/transactions")
    FinanceTransactionResponse createTransaction(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody FinanceTransactionRequest request
    );

    @DeleteMapping("/api/v1/transactions/{transactionId}")
    void deleteTransaction(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @PathVariable UUID transactionId
    );
}
