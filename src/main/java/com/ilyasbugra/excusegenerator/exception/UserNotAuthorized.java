package com.ilyasbugra.excusegenerator.exception;

import com.ilyasbugra.excusegenerator.util.UserErrorMessages;

public class UserNotAuthorized extends RuntimeException {
    public UserNotAuthorized(String message) {
        super(String.format(UserErrorMessages.USER_NOT_AUTHORIZED, message));
    }
}
