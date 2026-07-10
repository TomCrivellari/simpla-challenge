package br.com.simplameta.auth_service.exception;

public class InvalidLoginCredentialsException extends RuntimeException {

    public InvalidLoginCredentialsException() {
        super("Invalid email or password");
    }
}
