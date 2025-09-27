package br.com.connectai.api.errors.handlers;

import br.com.connectai.api.errors.NotFoundException;
import br.com.connectai.api.errors.WrongCredentialsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private static Map<String, String> generateResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        response.put("date", String.valueOf(new Date()));

        return response;
    }

    @ExceptionHandler({WrongCredentialsException.class})
    public ResponseEntity<Map<String, String>> wrongCredentialsException(WrongCredentialsException e) {
        LOGGER.error(e.getMessage());
        System.err.println(e.getStackTrace());
        return new ResponseEntity<>(generateResponse(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<Map<String, String>> notFoundException(NotFoundException e) {
        LOGGER.error(e.getMessage());
        System.err.println(e.getStackTrace());
        return new ResponseEntity<>(generateResponse(e.getMessage()), HttpStatus.NOT_FOUND);
    }


}
