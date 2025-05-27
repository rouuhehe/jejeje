package com.dbp.legalcheck.exception.user;

import com.dbp.legalcheck.common.exception.ResourceNotFoundException;

public class UserNotFoundException extends ResourceNotFoundException {
    public UserNotFoundException() {
        super("User could not be found");
    }
}
