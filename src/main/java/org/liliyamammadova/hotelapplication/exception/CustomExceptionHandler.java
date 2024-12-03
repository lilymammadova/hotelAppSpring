package org.liliyamammadova.hotelapplication.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ReservationException.class)
    public Map<String, String> handleInvalidArgument(ReservationException exception) {
        return Map.of("errorMessage", exception.getMessage());
    }
}
