package tech.alexberbo.berboapp.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import tech.alexberbo.berboapp.model.HttpResponse;

import java.io.OutputStream;

import static java.time.LocalTime.now;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
public class FilterExceptionHandler {
    public static void handleExceptions(HttpServletResponse response, Exception e) {
        HttpResponse httpResponse;
        if(e instanceof BadCredentialsException || e instanceof ApiException ||
                e instanceof DisabledException || e instanceof LockedException) {
            httpResponse = getHttpResponse(response, e.getMessage(), BAD_REQUEST);
            writeResponse(httpResponse, response);
        } else if (e instanceof TokenExpiredException) {
            httpResponse = getHttpResponse(response, "You need to login again!", UNAUTHORIZED);
            writeResponse(httpResponse, response);
        }
        else {
            httpResponse = getHttpResponse(response, "An Error occurred, please try again later!", INTERNAL_SERVER_ERROR);
            writeResponse(httpResponse, response);
        }
        log.error(e.getMessage());
    }

    private static void writeResponse(HttpResponse httpResponse, HttpServletResponse response) {
        OutputStream out;
        try {
            out = response.getOutputStream();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(out, httpResponse);
            out.flush();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    private static HttpResponse getHttpResponse(HttpServletResponse response, String message, HttpStatus httpStatus) {
        HttpResponse httpResponse = HttpResponse.builder()
                .status(httpStatus)
                .statusCode(httpStatus.value())
                .reason(httpStatus.getReasonPhrase())
                .message(message)
                .timeStamp(now().toString())
                .build();
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(httpStatus.value());
        return httpResponse;
    }

}
