package com.dbp.legalcheck.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidOperationException extends ResponseStatusException {
    public InvalidOperationException() {
        this("Bad Request");
    }

    public InvalidOperationException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
