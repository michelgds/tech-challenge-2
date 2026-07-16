package br.com.fiap.usuarios.domain.exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException() {
        super("Login ou senha inválidos");
    }
}

