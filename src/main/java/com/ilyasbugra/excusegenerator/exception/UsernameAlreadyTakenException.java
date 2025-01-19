package com.ilyasbugra.excusegenerator.exception;

import com.ilyasbugra.excusegenerator.util.UserErrorMessages;

public class UsernameAlreadyTakenException extends RuntimeException {
    public UsernameAlreadyTakenException(String username) {
        super(String.format(UserErrorMessages.USERNAME_ALREADY_TAKEN, username));
    }
}
