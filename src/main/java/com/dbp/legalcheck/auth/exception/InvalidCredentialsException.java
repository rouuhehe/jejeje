package com.dbp.legalcheck.auth.exception;

import com.dbp.legalcheck.common.exception.UnauthorizedException;

public class InvalidCredentialsException extends UnauthorizedException {

    public InvalidCredentialsException() {
        super("Invalid username/email or password");
    }
}
