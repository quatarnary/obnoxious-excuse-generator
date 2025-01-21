package com.ilyasbugra.excusegenerator.exception;

import com.ilyasbugra.excusegenerator.util.UserErrorMessages;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(String.format(UserErrorMessages.USER_NOT_FOUND, message));
    }
}
