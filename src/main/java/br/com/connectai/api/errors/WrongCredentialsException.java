package br.com.connectai.api.errors;

public class WrongCredentialsException extends RuntimeException {
    public WrongCredentialsException() {
        super("Credenciais inválidas");
    }
}
