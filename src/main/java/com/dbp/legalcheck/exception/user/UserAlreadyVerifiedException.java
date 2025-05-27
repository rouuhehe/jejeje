package com.dbp.legalcheck.exception.user;

import com.dbp.legalcheck.common.exception.ConflictException;

public class UserAlreadyVerifiedException extends ConflictException {
    public UserAlreadyVerifiedException() {
        super("User already verified");
    }
}
