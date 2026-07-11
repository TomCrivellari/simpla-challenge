package br.com.simplameta.meta_service.exception;

public class InsufficientBalanceException extends RuntimeException {

    public InsufficientBalanceException() {
        super("Saldo insuficiente para realizar o aporte.");
    }
}
