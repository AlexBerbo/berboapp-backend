package tech.alexberbo.berboapp.exception;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
