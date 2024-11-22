package com.example.adminservice.exception;

import com.example.adminservice.response.Response;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class HandleException {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex){
        Response response = Response.builder()
                .isSuccess(false)
                .message(ex.getMessage())
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<?> handleExpiredToken(ExpiredJwtException ex){
        Response response = Response.builder()
                .isSuccess(false)
                .message("TOKEN EXPIRED")
                .data(null)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
