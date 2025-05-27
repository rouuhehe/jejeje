package com.dbp.legalcheck.exception.guide;

import com.dbp.legalcheck.common.exception.ResourceNotFoundException;

public class GuideNotFoundException extends ResourceNotFoundException {
    public GuideNotFoundException() {
        super("Guide not found");
    }
}
