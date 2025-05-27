package com.dbp.legalcheck.exception.guide;

import com.dbp.legalcheck.common.exception.ConflictException;

public class GuideAlreadyExistsException extends ConflictException {
    public GuideAlreadyExistsException() {
        super("Guide with same title already exists");
    }
}
