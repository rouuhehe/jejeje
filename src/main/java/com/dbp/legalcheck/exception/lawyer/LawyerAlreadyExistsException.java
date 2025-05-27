package com.dbp.legalcheck.exception.lawyer;

import com.dbp.legalcheck.common.exception.ConflictException;

public class LawyerAlreadyExistsException extends ConflictException {
    public LawyerAlreadyExistsException() {
        super("Lawyer with same email already exists");
    }

}
