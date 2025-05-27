package com.dbp.legalcheck.exception.lawyer;

import com.dbp.legalcheck.common.exception.ResourceNotFoundException;

public class LawyerNotFoundException extends ResourceNotFoundException {
    public LawyerNotFoundException() {
        super("Lawyer not found");
    }
}
