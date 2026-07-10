package br.com.simplameta.finance_service.exception;

public class AuthenticatedUserNotFoundException extends RuntimeException {

    public AuthenticatedUserNotFoundException() {
        super("Authenticated user could not be identified");
    }
}
