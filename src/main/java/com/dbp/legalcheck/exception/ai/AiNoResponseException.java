package com.dbp.legalcheck.exception.ai;

import com.dbp.legalcheck.common.exception.InternalServerErrorException;

public class AiNoResponseException extends InternalServerErrorException {
    public AiNoResponseException() {
        super("Error generating AI response");
    }
}
