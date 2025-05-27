package com.dbp.legalcheck.exception.guide;

import com.dbp.legalcheck.common.exception.UnauthorizedException;

public class UserNotAuthorException extends UnauthorizedException {
    public UserNotAuthorException() {
        super("User is not the guide author");
    }
}
