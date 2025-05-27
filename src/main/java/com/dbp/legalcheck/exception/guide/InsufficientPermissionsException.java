package com.dbp.legalcheck.exception.guide;

import com.dbp.legalcheck.common.exception.UnauthorizedException;

public class InsufficientPermissionsException extends UnauthorizedException {
    public InsufficientPermissionsException() {
        super("User does not have permissions to delete this guide");
    }
}
