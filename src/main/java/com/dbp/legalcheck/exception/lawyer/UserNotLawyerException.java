package com.dbp.legalcheck.exception.lawyer;

import com.dbp.legalcheck.common.exception.UnauthorizedException;

public class UserNotLawyerException extends UnauthorizedException {
    public UserNotLawyerException() {
        super("User is not a lawyer");
    }
}
