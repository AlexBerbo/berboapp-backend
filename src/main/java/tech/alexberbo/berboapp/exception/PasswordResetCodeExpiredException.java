package tech.alexberbo.berboapp.exception;

public class PasswordResetCodeExpiredException extends Exception {
    public PasswordResetCodeExpiredException(String message) {
        super(message);
    }
}
