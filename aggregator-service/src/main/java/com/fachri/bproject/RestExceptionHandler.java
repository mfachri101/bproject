package com.fachri.bproject;

import com.fachri.bproject.model.FlightSearchResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<FlightSearchResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        FlightSearchResponse errorResponse = FlightSearchResponse.builder()
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<FlightSearchResponse> handleException(Exception ex) {
        FlightSearchResponse errorResponse = FlightSearchResponse.builder()
                .errorMessage(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}