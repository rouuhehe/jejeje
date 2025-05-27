package com.dbp.legalcheck.config.exception;

import com.dbp.legalcheck.dto.exception.ExceptionDTO;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionDTO> handleResponseStatusException(ResponseStatusException e) {
        ExceptionDTO response = new ExceptionDTO();
        response.setStatus(e.getStatusCode().value());
        response.setMessage(e.getReason());

        return new ResponseEntity<>(response, e.getStatusCode());
    }

}
