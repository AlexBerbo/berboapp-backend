package tech.alexberbo.berboapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tech.alexberbo.berboapp.model.HttpResponse;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.*;
import static tech.alexberbo.berboapp.constant.exception.ExceptionConstants.*;

/**
    This is the exception handler class, this class handles all the known errors that could occur.
    The unknown errors are handled as 500 internal server error, so even the errors that are not known to me
    can be picked up and will return a corresponding message so that the developer can have information about it.
    This handler is passing through the controller. And any error message/exception will be handled here.
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandling extends ResponseEntityExceptionHandler implements ErrorController {
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return new ResponseEntity<>(HttpResponse.builder()
                .status(resolve(statusCode.value()))
                .statusCode(statusCode.value())
                .message(ERROR)
                .developerMessage(ex.getMessage())
                .timeStamp(LocalDateTime.now().toString()), statusCode);
    }
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        List<FieldError> fieldErrors = ex.getFieldErrors();
        String fieldMessage = fieldErrors.stream().map(FieldError::getDefaultMessage).collect(Collectors.joining(", "));
        return new ResponseEntity<>(HttpResponse.builder()
                .status(resolve(statusCode.value()))
                .statusCode(statusCode.value())
                .message(fieldMessage)
                .developerMessage(ex.getMessage())
                .timeStamp(LocalDateTime.now().toString()), statusCode);
    }
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<HttpResponse> sQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        return sendResponse(BAD_REQUEST, CONSTRAINT);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException(BadCredentialsException e) {
        return sendResponse(BAD_REQUEST, BAD_CREDENTIALS);
    }
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException(DisabledException e) {
        return sendResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }
    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<HttpResponse> emailExistsException(EmailExistsException e){
        return sendResponse(BAD_REQUEST, EMAIL_EXISTS);
    }
    @ExceptionHandler(EmailDoesNotExistException.class)
    public ResponseEntity<HttpResponse> emailDoesNotExistException(EmailDoesNotExistException e){
        return sendResponse(BAD_REQUEST, EMAIL_DOES_NOT_EXIST);
    }
    @ExceptionHandler(EmailException.class)
    public ResponseEntity<HttpResponse> emailException(EmailException e) {
        return sendResponse(BAD_REQUEST, UNABLE_TO_SEND_EMAIL);
    }
    @ExceptionHandler(CodeExpiredException.class)
    public ResponseEntity<HttpResponse> codeExpiredException(CodeExpiredException e) {
        return sendResponse(BAD_REQUEST, CODE_EXPIRED);
    }
    @ExceptionHandler(PasswordResetCodeExpiredException.class)
    public ResponseEntity<HttpResponse> passwordResetCodeExpiredException(PasswordResetCodeExpiredException e) {
        return sendResponse(BAD_REQUEST, RESET_PASSWORD_URL_EXPIRED);
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<HttpResponse> apiException(ApiException e) {
        return sendResponse(BAD_REQUEST, e.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> motherException(Exception e) {
        return sendResponse(INTERNAL_SERVER_ERROR, e.getMessage());
    }
    private ResponseEntity<HttpResponse> sendResponse(HttpStatus status, String message) {
        return ResponseEntity.badRequest().body(HttpResponse.builder()
                .status(status)
                .statusCode(status.value())
                .reason(message)
                .message(message)
                .build());
    }
}
