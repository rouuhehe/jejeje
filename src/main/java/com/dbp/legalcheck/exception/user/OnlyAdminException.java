package com.dbp.legalcheck.exception.user;

import com.dbp.legalcheck.common.exception.ConflictException;

public class OnlyAdminException extends ConflictException {
    public OnlyAdminException() {
        super("User is the only admin");
    }
}
