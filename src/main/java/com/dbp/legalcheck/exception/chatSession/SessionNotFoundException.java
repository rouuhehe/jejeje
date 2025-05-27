package com.dbp.legalcheck.exception.chatSession;

import com.dbp.legalcheck.common.exception.UnauthorizedException;

public class SessionNotFoundException extends UnauthorizedException {
    public SessionNotFoundException() {
        super("Session was not found");
    }
}
