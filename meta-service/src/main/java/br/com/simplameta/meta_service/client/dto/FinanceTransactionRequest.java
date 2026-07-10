package br.com.simplameta.meta_service.client.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record FinanceTransactionRequest(

        String type,
        String description,
        BigDecimal amount,
        String category,
        LocalDate transactionDate

) {
}
