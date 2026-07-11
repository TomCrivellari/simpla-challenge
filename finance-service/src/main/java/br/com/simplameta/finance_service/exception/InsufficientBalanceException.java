package br.com.simplameta.finance_service.exception;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException(BigDecimal availableBalance) {
        super("Saldo insuficiente. Saldo disponível: " + NumberFormat
                .getCurrencyInstance(Locale.of("pt", "BR"))
                .format(availableBalance));
    }
}
