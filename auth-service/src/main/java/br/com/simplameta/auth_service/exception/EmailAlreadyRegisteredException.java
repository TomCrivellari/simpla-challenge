package br.com.simplameta.auth_service.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String email) {
        super("Email is already registered: " + email);
    }
}