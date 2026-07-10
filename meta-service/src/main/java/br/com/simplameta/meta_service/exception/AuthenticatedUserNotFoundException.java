package br.com.simplameta.meta_service.exception;

public class AuthenticatedUserNotFoundException extends RuntimeException {

    public AuthenticatedUserNotFoundException() {
        super("Authenticated user could not be identified");
    }
}
