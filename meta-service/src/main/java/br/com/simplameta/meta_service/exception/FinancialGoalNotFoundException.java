package br.com.simplameta.meta_service.exception;

import java.util.UUID;

public class FinancialGoalNotFoundException extends RuntimeException {

    public FinancialGoalNotFoundException(UUID goalId) {
        super("Financial goal not found: " + goalId);
    }
}
