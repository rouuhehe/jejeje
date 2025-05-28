package com.dbp.legalcheck.exception.chatSession;

import com.dbp.legalcheck.common.exception.InvalidOperationException;

public class ClosedSessionException extends InvalidOperationException {
    public ClosedSessionException() {
        super("Current Session is already closed");
    }
}
