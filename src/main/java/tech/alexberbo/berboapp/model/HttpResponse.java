package tech.alexberbo.berboapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.util.Map;

/**
    This is a custom response that I put in all the response messages that are returned to the user.
 */
@Data
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HttpResponse {
    protected HttpStatus status;
    protected int statusCode;
    protected String timeStamp;
    protected String reason;
    protected String message;
    protected String developerMessage;
    protected Map<?, ?> data;
}
