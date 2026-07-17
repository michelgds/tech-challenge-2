package br.com.fiap.techchallenge.domain.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("Login ou senha inválidos");
    }
}

