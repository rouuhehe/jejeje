package com.dbp.legalcheck.exception.chatSession;

import com.dbp.legalcheck.common.exception.UnauthorizedException;

public class SessionNoOwnerException extends UnauthorizedException {
    public SessionNoOwnerException() {
        super("User is not the session owner");
    }
}
