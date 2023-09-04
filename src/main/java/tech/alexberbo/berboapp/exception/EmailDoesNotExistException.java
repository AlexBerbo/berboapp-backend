package tech.alexberbo.berboapp.exception;

public class EmailDoesNotExistException extends Exception {
    public EmailDoesNotExistException(String message) {
        super(message);
    }
}
