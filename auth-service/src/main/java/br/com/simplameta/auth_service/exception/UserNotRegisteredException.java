package br.com.simplameta.auth_service.exception;

public class UserNotRegisteredException extends RuntimeException {

    public UserNotRegisteredException() {
        super("Não existe uma conta cadastrada com este e-mail.");
    }
}
