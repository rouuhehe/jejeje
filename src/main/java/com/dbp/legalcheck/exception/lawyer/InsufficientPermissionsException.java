package com.dbp.legalcheck.exception.lawyer;

import com.dbp.legalcheck.common.exception.UnauthorizedException;

public class InsufficientPermissionsException extends UnauthorizedException {
    public InsufficientPermissionsException() {
        super("User is neither admin not the lawyer");
    }
}
