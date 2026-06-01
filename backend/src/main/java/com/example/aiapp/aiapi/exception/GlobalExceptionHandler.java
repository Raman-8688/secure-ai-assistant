package com.example.aiapp.aiapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
//        ErrorResponse error = new ErrorResponse(
//                "Something went wrong",
//                ex.getMessage()
//        );
//
//        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
//    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        // Log internally (use your logging framework)
        System.err.println("Internal error: " + ex.getMessage());

        // Return generic message to frontend — never expose stack trace
        return ResponseEntity.internalServerError()
                .body(Map.of("error", "Something went wrong. Please try again later."));
    }
}
