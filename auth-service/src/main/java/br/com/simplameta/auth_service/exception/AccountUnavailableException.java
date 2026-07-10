package br.com.simplameta.auth_service.exception;

public class AccountUnavailableException extends RuntimeException {

    public AccountUnavailableException(String message) {
        super(message);
    }
}
