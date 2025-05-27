package com.dbp.legalcheck.exception.user;

import com.dbp.legalcheck.common.exception.ConflictException;

public class EmailConflictException extends ConflictException {
    public EmailConflictException() {
        super("Email already exists");
    }
}
